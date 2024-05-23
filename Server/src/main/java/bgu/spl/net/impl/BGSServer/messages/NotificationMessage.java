package bgu.spl.net.impl.BGSServer.messages;

public class NotificationMessage extends Message{
    private short type;                //pm or post
    private String postingUser;
    private String content;
    private int postingID;

    public NotificationMessage(Integer opcode,int id, short _type, String _postingUser, String _content){
        super(opcode);
        postingID = id;
        type = _type;
        content = _content;
        postingUser = _postingUser;
    }

    public String getContent() {
        return content;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public short getType() {
        return type;
    }

    public int getPostingID() {
        return postingID;
    }
}
