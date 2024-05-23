package bgu.spl.net.impl.BGSServer.messages;

public class AckMessage extends Message{
    private Integer msgOpcode;
    private String content;

    public AckMessage(Integer opcode, Integer _msgOpcode, String _content){
        super(opcode);
        msgOpcode = _msgOpcode;
        content = _content;
    }

    public String getContent() {
        return content;
    }

    public Integer getMsgOpcode() {
        return msgOpcode;
    }
}
