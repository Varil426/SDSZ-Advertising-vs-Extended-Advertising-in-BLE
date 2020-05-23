import java.time.Instant;

public class PrimaryExtendedMessage extends Message{
    int channel;
    Instant timeForSecondary;

    public PrimaryExtendedMessage(int messageID, int senderID, int channel, Instant timeForSecondary) {
        super(messageID, senderID);
        this.channel = channel;
        this.timeForSecondary = timeForSecondary;
    }
}
