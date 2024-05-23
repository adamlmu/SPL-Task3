package bgu.spl.net.impl.BGSServer.messages;

public class BlockMessage extends Message{
    private String blockingUsername;

    public BlockMessage(Integer opcode, String _blockingUsername){
        super(opcode);
        blockingUsername = _blockingUsername;
    }

    public String getBlockingUsername() {
        return blockingUsername;
    }
}
