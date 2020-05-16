public class SecondaryMessage extends Message{
    byte[] content = new byte[255];
    public SecondaryMessage(int messageID, int senderID, byte[] content) {
        super(messageID, senderID);
        this.content = content;
    }
    public SecondaryMessage(int messageID, int senderID, byte[] content, boolean lastMessage) {
        super(messageID, senderID, lastMessage);
        this.content = content;
    }
}
