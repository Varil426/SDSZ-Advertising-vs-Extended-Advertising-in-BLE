public class PrimaryLegacyMessage extends Message{
    PrimaryLegacyMessage(int messageID, int senderID, byte[] content){
        super(messageID, senderID);
        this.content = content;
    }
    PrimaryLegacyMessage(int messageID, int senderID, byte[] content, boolean last){
        super(messageID, senderID, last);
        this.content = content;
    }
}
