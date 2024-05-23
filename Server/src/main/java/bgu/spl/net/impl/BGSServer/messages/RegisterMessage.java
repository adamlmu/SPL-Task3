package bgu.spl.net.impl.BGSServer.messages;

public class RegisterMessage extends Message {
    private String username;
    private String password;
    private String birthday;

    public RegisterMessage(Integer _opcode, String _username, String _password, String _birthday){
        super(_opcode);
        username = _username;
        password = _password;
        birthday = _birthday;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }
}
