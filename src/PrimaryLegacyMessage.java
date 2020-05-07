public class PrimaryLegacyMessage extends Message{
    byte[] content = new byte[23];
    PrimaryLegacyMessage(int messageID, int senderID, byte[] content){
        super(messageID, senderID);
        this.content = content;
    }
}
