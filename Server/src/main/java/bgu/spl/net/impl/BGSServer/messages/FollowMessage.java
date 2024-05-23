package bgu.spl.net.impl.BGSServer.messages;

public class FollowMessage extends Message{
    private boolean follow;         //if true = follow, false = unfollow
    private String username;

    public FollowMessage(Integer _opcode, boolean _follow, String _username){
        super(_opcode);
        follow = _follow;
        username = _username;
    }

    public String getUsername() {
        return username;
    }

    public boolean getFollow() {
        return follow;
    }
}
