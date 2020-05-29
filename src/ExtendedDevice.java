import java.nio.ByteBuffer;
import java.time.Instant;
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
    ExtendedDevice(int deviceID, long advertiseBreak, int deviceToListenTo, int dataSize, boolean randomDelay){ super(deviceID, advertiseBreak, deviceToListenTo, dataSize, randomDelay);}
    @Override
    public void run() {
        if(this.mode != Mode.SCAN) {
            if(this.randomDelay) {
                //Generate up to 1 ms delay to avoid cancelling out of signals
                try {
                    sleep(0,this.rand.nextInt(1000000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    this.removeOldMessages();
                    this.scan();
                    break;
                case LISTEN:
                    this.secondaryListen();
            }
            //System.out.println(deviceID + " " + mode);
        }
    }

    @Override
    void advertise() {
        //32 * 1000000000 - primary payload size is const 32 bytes, times 1000000000 to get time in nanoseconds
        long tmp = (long) Math.ceil(20000000/1048576) * 100;
        if(this.advertiseFor > this.advertiseCounter && this.contentPart <= (int) Math.ceil(this.data.length/247)) {
            int randChannel = this.rand.nextInt(3) + 37;
            while (this.advertisedOn.contains(randChannel)) {
                randChannel = this.rand.nextInt(3) + 37;
            }
            this.advertisedOn.add(randChannel);
            World.getInstance().channels[randChannel].setPayload(this.primary, tmp);
            try {
                Pair<Long, Integer> time = this.getMillisAndNanos(tmp);
                sleep(time.getL(), time.getR());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.advertiseCounter++;
        } else {
            if(this.contentPart <= (int) Math.ceil(this.data.length/246)) {
                this.generateSecondaryAdvertisement();
                this.mode = Mode.SECONDARY;
            } else {
                this.mode = Mode.FINISHED;
                this.contentPart = 0;
            }
        }
    }

    void secondaryAdvertise() {
        if(Instant.now().isAfter(this.primary.timeForSecondary) && this.advertiseCounter < 1) {
            //Secondary payload size times 1000000000 to get time in nanoseconds
            long tmp = (long) Math.ceil(((this.secondary.content.length + 4 + 4 + 1) * 1000000)/1048576)*100;
            World.getInstance().channels[this.primary.channel].setPayload(this.secondary, tmp);
            try {
                Pair<Long, Integer> time = this.getMillisAndNanos(tmp);
                sleep(time.getL(), time.getR());
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
        this.scanningCheck();
        for (int i = World.getInstance().numberOfChannels-3; i < World.getInstance().numberOfChannels; i++) {
            if(!World.getInstance().channels[i].isEmpty() && !World.getInstance().channels[i].inAirConflict) {
                PrimaryExtendedMessage currentMessage = (PrimaryExtendedMessage) World.getInstance().channels[i].getPayload();
                if(!(currentMessage instanceof PrimaryExtendedMessage) || (this.deviceToListenTo != null && currentMessage.senderID != this.deviceToListenTo))continue;
                if(this.isMessageNew(currentMessage)) {
                    this.scanningSince = null;
                    this.receivedMessages.add(new Pair<Message, Instant>(currentMessage, Instant.now()));
                    this.receivedAdvertisement = (PrimaryExtendedMessage) currentMessage;
                    this.mode = Mode.LISTEN;
                }
            }
        }
    }

    void secondaryListen() {
        if(!World.getInstance().channels[this.receivedAdvertisement.channel].isEmpty() && !World.getInstance().channels[this.receivedAdvertisement.channel].inAirConflict) {
            SecondaryMessage currentMessage = (SecondaryMessage) World.getInstance().channels[this.receivedAdvertisement.channel].getPayload();
            if(currentMessage != null && currentMessage.senderID == this.deviceToListenTo && this.isMessageNew(currentMessage)) {
                for (byte b : currentMessage.content) {
                    this.receivedData.add(b);
                }
                this.receivedMessages.add(new Pair<Message, Instant>(currentMessage, Instant.now()));
                this.mode = Mode.SCAN;
                if(currentMessage.lastMessage) {
                    this.mode = Mode.FINISHED;
                    //System.out.println(this.receivedData.toString());
                }
            }
            //return;
        }
        if(this.receivedAdvertisement.timeForSecondary.plusMillis(50).isBefore(Instant.now())) {
            this.mode = Mode.SCAN;
            //System.out.println("Secondary missed");
        }
    }

    void generatePrimaryAdvertisement() {
        this.advertiseCounter = 0;
        this.advertisedOn.clear();
        try {
            if(this.randomDelay) sleep(this.advertiseBreak, this.rand.nextInt(500000));
            else sleep(this.advertiseBreak);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.primary = new PrimaryExtendedMessage(this.rand.nextInt(), this.deviceID, this.rand.nextInt(37), Instant.now().plusMillis(1));
    }
    void generateSecondaryAdvertisement() {
        this.advertiseCounter = 0;
        int numberOfParts = (int) Math.ceil(this.data.length/246);
        ByteBuffer buffer;
        if(this.contentPart == numberOfParts) {
            buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*246, this.data.length));
            this.secondary = new SecondaryMessage(this.rand.nextInt(),this.deviceID,buffer.array(), true);
        }
        else {
            buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*246, (this.contentPart+1)*246));
            this.secondary = new SecondaryMessage(this.rand.nextInt(),this.deviceID,buffer.array());
        }
        this.contentPart++;
    }
}
