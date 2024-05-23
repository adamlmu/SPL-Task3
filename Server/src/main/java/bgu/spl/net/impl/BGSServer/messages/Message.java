package bgu.spl.net.impl.BGSServer.messages;

public abstract class Message {
    private Integer opcode;

    public Message(Integer _opcode){
        opcode = _opcode;
    }

    public Integer getOpcode() {
        return opcode;
    }
}


