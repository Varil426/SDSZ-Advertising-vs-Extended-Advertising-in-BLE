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
                    this.receivedMessages.add(new Pair<Message, Long>(currentMessage, System.nanoTime()));
                    for (byte b : ((PrimaryLegacyMessage) currentMessage).content) {
                        //System.out.print(b + " ");
                        this.receivedData.add(b);
                    }
                    //System.out.println();
                    if(currentMessage.lastMessage) {
                        this.mode = Mode.FINISHED;
                        System.out.println(this.receivedData.toString());
                    }
                }
            }
        }
    }

    @Override
    void advertise() {
        //TODO tmp zmienić
        long tmp = (long) Math.ceil(32000000/1048576);
        if(this.advertiseFor > this.advertiseCounter) {
            Simulation.World.channels[37+this.advertiseCounter %3].setPayload(this.message, tmp);
            try {
                sleep(0, (int) tmp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.advertiseCounter++;
        } else {
            if(this.contentPart <= (int) Math.ceil(this.data.length/23)) {
                this.generateAdvertisement();
            } else {
                this.mode = Mode.FINISHED;
                for (byte b :
                        this.data) {
                    System.out.print(b + ", ");
                }
                System.out.println();
                this.contentPart = 0;
            }
        }
    }

    @Override
    public void run() {
        while (this.mode != Mode.FINISHED) {
            switch (this.mode) {
                case SCAN:
                    this.scan();
                    break;
                case WAIT:
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case ADVERTISE:
                    this.advertise();
                    break;
            }
        }
    }

    void generateAdvertisement() {
        this.advertiseCounter = 0;
        int numberOfParts = (int) Math.ceil(this.data.length/23);
        ByteBuffer buffer;
        try {
            sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(this.contentPart == numberOfParts) {
            buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*23, this.data.length));
            this.message = new PrimaryLegacyMessage(this.rand.nextInt(),this.deviceID,buffer.array(), true);
        }
        else {
            buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*23, (this.contentPart+1)*23));
            this.message = new PrimaryLegacyMessage(this.rand.nextInt(),this.deviceID,buffer.array());
        }
        this.contentPart++;
    }
}
