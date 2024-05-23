package bgu.spl.net.impl.BGSServer.messages;

public class PostMessage extends Message{
    private String content;

    public PostMessage(Integer opcode, String _content){
        super(opcode);
        content = _content;
    }

    public String getContent() {
        return content;
    }
}
