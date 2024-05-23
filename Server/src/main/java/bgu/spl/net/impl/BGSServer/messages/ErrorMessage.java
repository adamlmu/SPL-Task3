package bgu.spl.net.impl.BGSServer.messages;

public class ErrorMessage extends Message{
    private Integer msgOpcode;

    public ErrorMessage(Integer opcode, Integer _msgOpcode){
        super(opcode);
        msgOpcode = _msgOpcode;
    }

    public Integer getMsgOpcode() {
        return msgOpcode;
    }
}
