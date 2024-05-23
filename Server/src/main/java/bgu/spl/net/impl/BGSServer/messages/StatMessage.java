package bgu.spl.net.impl.BGSServer.messages;

import java.util.LinkedList;

public class StatMessage extends Message {
    LinkedList<String> usernames;

    public StatMessage(Integer opcode, LinkedList<String> _usernames){
        super(opcode);
        usernames = _usernames;
    }

    public LinkedList<String> getUsernames() {
        return usernames;
    }
}
