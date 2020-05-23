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
    ExtendedDevice(int deviceID, long advertiseBreak) {
        super(deviceID, advertiseBreak);
    }
    ExtendedDevice(int deviceID, long advertiseBreak, int deviceToListenTo, int dataSize){ super(deviceID, advertiseBreak, deviceToListenTo, dataSize);}
    @Override
    public void run() {
        if(this.mode != Mode.SCAN) {
            try {
                sleep(0,this.rand.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (this.mode != Mode.FINISHED) {
            switch (this.mode) {
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
                case SECONDARY:
                    this.secondaryAdvertise();
                    break;
                case SCAN:
                    this.scan();
                    break;
                case LISTEN:
                    this.secondaryListen();
            }
        }
    }

    @Override
    void advertise() {
        //32 * 1000000 - primary payload size is const 32 bytes, times 1000000 to get time in nanoseconds
        long tmp = (long) Math.ceil(32000000/1048576);
        if(this.advertiseFor > this.advertiseCounter && this.contentPart <= (int) Math.ceil(this.data.length/247)) {
            int randChannel = this.rand.nextInt(3) + 37;
            while (this.advertisedOn.contains(randChannel)) {
                randChannel = this.rand.nextInt(3) + 37;
            }
            this.advertisedOn.add(randChannel);
            World.getInstance().channels[randChannel].setPayload(this.primary, tmp);
            try {
                sleep(0, (int) tmp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        if(System.nanoTime() >= this.primary.time && this.advertiseCounter < 1) {
            //Secondary payload size times 1000000 to get time in nanoseconds
            long tmp = (long) Math.ceil(((this.secondary.content.length + 4 + 4 + 1) * 1000000)/1048576);
            World.getInstance().channels[this.primary.channel].setPayload(this.secondary, tmp);
            System.out.println("Wrzuciłem sec");
            try {
                sleep(0, (int) tmp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.advertiseCounter++;
        } else {
            this.mode = Mode.ADVERTISE;
            this.generatePrimaryAdvertisement();
        }
    }

    @Override
    void scan() {
        for (int i = World.getInstance().numberOfChannels-3; i < World.getInstance().numberOfChannels; i++) {
            if(!World.getInstance().channels[i].isEmpty()) {
                Message currentMessage = World.getInstance().channels[i].getPayload();
                if(!(currentMessage instanceof PrimaryExtendedMessage) || (this.deviceToListenTo != null && currentMessage.senderID != this.deviceToListenTo))continue;
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
                    this.receivedAdvertisement = (PrimaryExtendedMessage) currentMessage;
                    System.out.println("\nOdebrana: " + currentMessage.toString());
                    this.mode = Mode.LISTEN;
                }
            }
        }
    }

    void secondaryListen() {
        if(System.nanoTime() >= this.receivedAdvertisement.time+50 && !World.getInstance().channels[this.receivedAdvertisement.channel].isEmpty()) {
            SecondaryMessage currentMessage = (SecondaryMessage) World.getInstance().channels[this.receivedAdvertisement.channel].getPayload();
            System.out.println("Odebrałem sec");
            System.out.print(this.receivedAdvertisement.channel + "\t\t");
            for (byte b : currentMessage.content) {
                this.receivedData.add(b);
                System.out.print(b+", ");
            }
            System.out.println();
            this.mode = Mode.SCAN;
            if(currentMessage.lastMessage) {
                this.mode = Mode.FINISHED;
                //System.out.println(this.receivedData.toString());
            }
        }
    }

    void generatePrimaryAdvertisement() {
        this.advertiseCounter = 0;
        this.advertisedOn.clear();
        try {
            sleep(this.advertiseBreak,this.rand.nextInt(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.primary = new PrimaryExtendedMessage(this.rand.nextInt(), this.deviceID, this.rand.nextInt(37), System.nanoTime()+1000);
        System.out.println("\nWygenerowana: " + primary.toString());
    }
    void generateSecondaryAdvertisement() {
        this.advertiseCounter = 0;
        int numberOfParts = (int) Math.ceil(this.data.length/247);
        ByteBuffer buffer;
        if(this.contentPart == numberOfParts) {
            buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*247, this.data.length));
            this.secondary = new SecondaryMessage(this.rand.nextInt(),this.deviceID,buffer.array(), true);
        }
        else {
            buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*247, (this.contentPart+1)*247));
            this.secondary = new SecondaryMessage(this.rand.nextInt(),this.deviceID,buffer.array());
        }
        this.contentPart++;
    }
}
