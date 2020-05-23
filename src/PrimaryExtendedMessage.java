public class PrimaryExtendedMessage extends Message{
    int channel;
    long time;

    public PrimaryExtendedMessage(int messageID, int senderID, int channel, long time) {
        super(messageID, senderID);
        this.channel = channel;
        this.time = time;
    }

    @Override
    public String toString() {
        return "PrimaryExtendedMessage{" +
                "messageID=" + messageID +
                ", senderID=" + senderID +
                ", channel=" + channel +
                ", time=" + time +
                '}';
    }
}
