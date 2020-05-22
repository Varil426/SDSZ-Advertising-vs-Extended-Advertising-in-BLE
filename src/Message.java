public abstract class Message {
    int messageID;
    int senderID;
    boolean lastMessage = false;
    byte[] content;

    public Message(int messageID, int senderID) {
        this.messageID = messageID;
        this.senderID = senderID;
    }

    public Message(int messageID, int senderID, boolean last) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.lastMessage = last;
    }

    void setMessageID(int messageID) {
        this.messageID = messageID;
    }
    int getMessageID() {
        return this.messageID;
    }
    void setSenderID(int senderID){
        this.senderID = senderID;
    }
    int getSenderID() {
        return this.senderID;
    }
}
