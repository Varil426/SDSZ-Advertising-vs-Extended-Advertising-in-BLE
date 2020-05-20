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
    public void run() {
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
        //TODO print wrócił i dalej pomaga, ale czemu
        System.out.print("");
        //TODO tmp zmienić żeby brało pod uwagę rozmiar wiadomości a nie stałe
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
            long tmp = (long) Math.ceil((255 * 1000000)/1048576);
            World.getInstance().channels[this.primary.channel].setPayload(this.secondary, tmp);
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
            if(!World.getInstance().channels[i].empty) {
                Message currentMessage = World.getInstance().channels[i].getPayload();
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
                    this.receivedMessages.add(new Pair<Message, Long>(currentMessage, System.nanoTime()));
                    this.receivedAdvertisement = (PrimaryExtendedMessage) currentMessage;
                    this.mode = Mode.LISTEN;
                }
            }
        }
    }

    void secondaryListen() {
        if(!World.getInstance().channels[this.receivedAdvertisement.channel].empty) {
            SecondaryMessage currentMessage = (SecondaryMessage) World.getInstance().channels[this.receivedAdvertisement.channel].getPayload();
            for (byte b : currentMessage.content) {
                this.receivedData.add(b);
            }
            this.mode = Mode.SCAN;
            if(currentMessage.lastMessage) {
                this.mode = Mode.FINISHED;
                System.out.println(this.receivedData.toString());
            }
        }
    }

    void generatePrimaryAdvertisement() {
        this.advertiseCounter = 0;
        this.advertisedOn.clear();
        try {
            sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.primary = new PrimaryExtendedMessage(this.rand.nextInt(), this.deviceID, this.rand.nextInt(37), System.nanoTime()+1000);
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
