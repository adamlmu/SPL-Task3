package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.messages.Message;
import bgu.spl.net.srv.BaseServer;

public class TPCMain {
    public static void main(String[] args) {
        try(BaseServer<Message> server = BaseServer.BGSServer(Integer.parseInt(args[0]), BGSProtocol::new, BGSEncoderDecoder::new);){
            server.serve();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
