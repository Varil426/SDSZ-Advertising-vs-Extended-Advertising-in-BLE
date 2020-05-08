import java.nio.ByteBuffer;
import java.util.Arrays;

public class ExtendedDevice extends Device {
    PrimaryExtendedMessage receivedAdvertisement;
    PrimaryExtendedMessage primary;
    SecondaryMessage secondary;
    int contentPart = 0;

    ExtendedDevice(int deviceID) {
        super(deviceID);
    }
    @Override
    void scan() {
        for (int i = Simulation.World.numberOfChannels-3; i < Simulation.World.numberOfChannels; i++) {
            if(!Simulation.World.channels[i].empty) {
                Message currentMessage = Simulation.World.channels[i].getPayload();
                if(!(currentMessage instanceof PrimaryExtendedMessage))continue;
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
                    this.receivedAdvertisement = (PrimaryExtendedMessage) currentMessage;
                    this.mode = Mode.LISTEN;
                }
            }
        }
    }

    @Override
    void advertise() {
        if(this.advertiseFor > this.advertiseCounter && this.contentPart <= (int) Math.ceil(this.data.length/247)) {
            Simulation.World.channels[37+this.advertiseCounter %3].setPayload(this.primary);
            this.advertiseCounter++;
        } else {
            //Może robić problemy
            if(this.contentPart <= (int) Math.ceil(this.data.length/247)) {
                this.generateSecondaryAdvertisement();
                this.mode = Mode.SECONDARY;
            } else {
                this.mode = Mode.FINISHED;
                this.contentPart = 0;
            }
        }
    }

    void secondaryAdvertise() {
        System.out.println(Simulation.World.getTime() + " " + this.primary.time);
        if(Simulation.World.getTime()-Simulation.World.timeStep/2 <= this.primary.time && Simulation.World.getTime()+Simulation.World.timeStep/2 >= this.primary.time) {
            Simulation.World.channels[this.primary.channel].setPayload(this.secondary);
            this.mode = Mode.ADVERTISE;
            this.generatePrimaryAdvertisement();
        }
    }

    void secondaryListen() {
        if(!Simulation.World.channels[this.receivedAdvertisement.channel].empty) {
            SecondaryMessage currentMessage = (SecondaryMessage) Simulation.World.channels[this.receivedAdvertisement.channel].getPayload();
            for (byte b :
                    currentMessage.content) {
                System.out.print(b + " ");
            }
            System.out.println();
            this.mode = Mode.SCAN;
        }
    }

    void generatePrimaryAdvertisement() {
        this.advertiseCounter = 0;
        this.primary = new PrimaryExtendedMessage(this.rand.nextInt(100), this.deviceID, this.rand.nextInt(37), Simulation.World.getTime()+(this.advertiseFor+2)*Simulation.World.timeStep);
    }
    void generateSecondaryAdvertisement() {
        int numberOfParts = (int) Math.ceil(this.data.length/247);
        ByteBuffer buffer;
        if(this.contentPart == numberOfParts) buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*247, this.data.length));
        else buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*247, (this.contentPart+1)*247));
        this.secondary = new SecondaryMessage(this.rand.nextInt(100),this.deviceID,buffer.array());
        this.contentPart++;
    }
}
