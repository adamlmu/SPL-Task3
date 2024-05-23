package bgu.spl.net.impl.BGSServer.messages;

public class PMMessage extends Message{
    private String content;
    private String addressee;

    public PMMessage(Integer opcode, String _content, String _addressee){
        super(opcode);
        addressee = _addressee;
        content = _content;
    }

    public String getAddressee() {
        return addressee;
    }

    public String getContent() {
        return content;
    }
}
