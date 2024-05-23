package bgu.spl.net.impl.BGSServer.messages;

public class LoginMessage extends Message{
    private String username;
    private String password;
    private boolean captcha;

    public LoginMessage(Integer _opcode, String _username, String _password, boolean _captcha){
        super(_opcode);
        username = _username;
        password = _password;
        captcha = _captcha;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getCaptcha() {
        return captcha;
    }
}
