package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.messages.Message;
import bgu.spl.net.srv.Reactor;

public class ReactorMain {
    public static void main(String[] args) {
        Reactor<Message> reactor = Reactor.reactor(Integer.parseInt(args[1]),Integer.parseInt(args[0]), BGSProtocol::new, BGSEncoderDecoder::new);
        reactor.serve();
    }
}
