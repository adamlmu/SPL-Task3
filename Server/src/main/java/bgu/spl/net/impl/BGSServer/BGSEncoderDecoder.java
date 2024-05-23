package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGSServer.messages.*;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class BGSEncoderDecoder implements MessageEncoderDecoder<Message> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private short opcode = 0;

    @Override
    public Message decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison
        if (nextByte == ';') {
            String msg = popString();
            return decodeStringToMsg(msg);
        }
        pushByte(nextByte);

        return null; //not a line yet
    }

    @Override
    public byte[] encode(Message msg){ // ACK / ERROR / NOTIFICATION
        short opShort = msg.getOpcode().shortValue();
        if (msg.getOpcode().equals(9)){// 2 + 1 + content + 1
            NotificationMessage notification = (NotificationMessage)msg;
            String str = " "+notification.getPostingUser()+" "+notification.getContent()+";";
            byte[] bytesContent = str.getBytes(StandardCharsets.UTF_8);
            byte[] response = new byte[bytesContent.length + 4];
            response[0] = (byte)((opShort >> 8) & 0xFF);
            response[1] = (byte)(opShort & 0xFF);
            response[2] = (byte)(notification.getType() & 0xFF);
            System.arraycopy(bytesContent,0,response,3,bytesContent.length);
            return response;
        }
        //msg.getContent().getBytes().length
        if (msg.getOpcode().equals(10)){//2+2+content + 1
            AckMessage ack = (AckMessage)msg;
            short msgShort = ack.getMsgOpcode().shortValue();
            byte[] opCodes = shortToBytes(opShort,msgShort);
            if (ack.getContent()==null) {
                return opCodes;
            }
            else{
                byte[] content = (ack.getContent()+";").getBytes(StandardCharsets.UTF_8);
                byte[] response = new byte[4 + content.length];
                for (int i = 0; i < 4 ; i++){
                    response[i] = opCodes[i];
                }
                System.arraycopy(content,0,response,4,content.length);
                return response;
            }
        }
        if (msg.getOpcode().equals(11)){//size is always 5 = 2 + 2 + 1
            ErrorMessage error = (ErrorMessage) msg;
            short msgShort = error.getMsgOpcode().shortValue();
            byte[] response = shortToBytes(opShort,msgShort);
            return response;
        }
        return null;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        opcode = bytesToShort(bytes);
        String result = new String(bytes, 2, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    private Message decodeStringToMsg(String str){
        String[] strWords = str.split("\0");    //need to check how client encoder works
        // expected : Adam\0pass\028-08-1995\0 -> [1Adam][pass][28-08-1995]
        // reality : Adampass28-08-1995
        if (opcode == 1){
            String username = strWords[0];
            String password = strWords[1];
            String birthday = strWords[2];
            return new RegisterMessage(1,username,password,birthday);
        }
        // Adam\0pass\01\0 -> [2Adam][pass][1]
        if (opcode == 2){
            String username = strWords[0];
            String password = strWords[1];
            boolean captcha;
            captcha = strWords[2].equals("1");
            return new LoginMessage(2,username,password,captcha);
        }
        //
        if (opcode == 3){
            return new LogoutMessage(3);
        }
        // 0\0itay\0 -> [40][itay]
        if (opcode == 4){
            boolean follow;
            follow = strWords[0].equals("0");
            String username = strWords[1];
            return new FollowMessage(4,follow,username);
        }
        // How you doing\0 -> [5How you doing]
        if (opcode == 5){
            String content = strWords[0];
            return new PostMessage(5, content);
        }
        // \0How you doing\0 -> [Itay][How you doing]
        if (opcode == 6){
            String addressee = strWords[0];
            String content = strWords[1]; //if there is " "
            return new PMMessage(6,content,addressee);
        }
        // 7
        if (opcode == 7){
            return new LogStatMessage(7);
        }
        // Itay|Dor|Shlomo|Spongebob\0 -> [8Itay|Dor|Shlomo|Spongebob]
        if (opcode == 8){
            String[] names = strWords[0].split("'|'");
            LinkedList<String> usernames = new LinkedList<>(Arrays.asList(names));
            return new StatMessage(8,usernames);
        }
        // 12Spongebob\0 -> [12Spongebob]
        if (opcode == 12){
            String blocking = strWords[0];
            return new BlockMessage(12,blocking);
        }
        return null;
    }

    private short bytesToShort(byte[] byteArr){
            short result = (short) ((byteArr[0] & 0xff) << 8);
            result += (short) (byteArr[1] & 0xff);
            return result;
    }

    private byte[] shortToBytes(short op, short msg) {
        byte[] bytesArr = new byte[5];
        bytesArr[0] = (byte)((op >> 8) & 0xFF);
        bytesArr[1] = (byte)(op & 0xFF);
        bytesArr[2] = (byte)((msg >> 8) & 0xFF);
        bytesArr[3] = (byte)(msg & 0xFF);
        bytesArr[4] = (byte)(';');
        return bytesArr;
    }


}
