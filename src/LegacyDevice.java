import java.nio.ByteBuffer;
import java.util.Arrays;

public class LegacyDevice extends Device {
    //byte[] data = new byte[2550];
    PrimaryLegacyMessage message;
    int contentPart = 0;
    LegacyDevice(int deviceID) {
        super(deviceID);
    }
    @Override
    void scan() {
        for (int i = Simulation.World.numberOfChannels-3; i < Simulation.World.numberOfChannels; i++) {
            if(!Simulation.World.channels[i].empty) {
                Message currentMessage = Simulation.World.channels[i].getPayload();
                if(!(currentMessage instanceof PrimaryLegacyMessage))continue;
                boolean newMessage = true;
                if(!this.receivedMessages.isEmpty()) {
                    for (Pair m : this.receivedMessages) {
                        Message myMessage = (Message) m.getL();
                        if(myMessage.messageID == currentMessage.messageID && myMessage.senderID == currentMessage.senderID) {
                            newMessage = false;
                            break;
                        }
                    }
                }
                if(newMessage) {
                    this.receivedMessages.add(new Pair<Message, Integer>(currentMessage, 7));
                    //TODO Zapisywanie wiadomości
                    for (byte b : ((PrimaryLegacyMessage) currentMessage).content) {
                        System.out.print(b + " ");
                    }
                    System.out.println();
                }
            }
        }
    }

    @Override
    void advertise() {
        if(this.sendFor > this.sentCounter) {
            Simulation.World.channels[37+this.sentCounter%3].setPayload(this.message);
            this.sentCounter++;
        } else {
            if(this.contentPart <= (int) Math.ceil(this.data.length/23)) {
                this.generateMessage();
            } else {
                this.mode = Mode.FINISHED;
            }
        }
    }

    @Override
    void generateMessage() {
        this.sentCounter = 0;
        int numberOfParts = (int) Math.ceil(this.data.length/23);
        ByteBuffer buffer;
        if(this.contentPart == numberOfParts) buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*23, this.data.length));
        else buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*23, (this.contentPart+1)*23));
        this.message = new PrimaryLegacyMessage(this.rand.nextInt(100),this.deviceID,buffer.array());
        this.contentPart++;
    }
}
