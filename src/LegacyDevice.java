import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;

public class LegacyDevice extends Device {
    PrimaryLegacyMessage message;
    int contentPart = 0;
    LegacyDevice(int deviceID) {
        super(deviceID);
    }
    LegacyDevice(int deviceID, long advertiseBreak) {
        super(deviceID, advertiseBreak);
    }
    LegacyDevice(int deviceID, long advertiseBreak, int deviceToListenTo, int dataSize){ super(deviceID, advertiseBreak, deviceToListenTo, dataSize);}
    LegacyDevice(int deviceID, long advertiseBreak, int deviceToListenTo, int dataSize, boolean randomDelay) { super(deviceID, advertiseBreak, deviceToListenTo, dataSize, randomDelay);}
    @Override
    void scan() {
        this.scanningCheck();
        for (int i = World.getInstance().numberOfChannels-3; i < World.getInstance().numberOfChannels; i++) {
            if(!World.getInstance().channels[i].isEmpty() && !World.getInstance().channels[i].inAirConflict) {
                Message currentMessage = World.getInstance().channels[i].getPayload();
                if(!(currentMessage instanceof PrimaryLegacyMessage) || (this.deviceToListenTo != null && currentMessage.senderID != this.deviceToListenTo))continue;
                if(this.isMessageNew(currentMessage)) {
                    this.scanningSince = null;
                    this.receivedMessages.add(new Pair<Message, Instant>(currentMessage, Instant.now()));
                    for (byte b : ((PrimaryLegacyMessage) currentMessage).content) {
                        //System.out.print(b + " ");
                        this.receivedData.add(b);
                    }
                    //System.out.println();
                    if(currentMessage.lastMessage) {
                        this.mode = Mode.FINISHED;
                        //System.out.println(this.receivedData.toString());
                    }
                }
            }
        }
    }

    @Override
    void advertise() {
        //Multiply by 1000000000 to get time to sent in nanoseconds
        long tmp = (long) Math.ceil(((this.message.content.length + 4 + 4 + 1)*10000000)/1048576)*100;
        if(this.advertiseFor > this.advertiseCounter) {
            int randChannel = this.rand.nextInt(3) + 37;
            while (this.advertisedOn.contains(randChannel)) {
                randChannel = this.rand.nextInt(3) + 37;
            }
            this.advertisedOn.add(randChannel);
            World.getInstance().channels[randChannel].setPayload(this.message, tmp);
            try {
                Pair<Long, Integer> time = this.getMillisAndNanos(tmp);
                sleep(time.getL(), time.getR());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.advertiseCounter++;
        } else {
            if(this.contentPart <= (int) Math.ceil(this.data.length/22)) {
                this.generateAdvertisement();
            } else {
                this.mode = Mode.FINISHED;
                this.contentPart = 0;
            }
        }
    }

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
                case SCAN:
                    this.removeOldMessages();
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
            }
        }
    }

    void generateAdvertisement() {
        this.advertiseCounter = 0;
        this.advertisedOn.clear();
        int numberOfParts = (int) Math.ceil(this.data.length/22);
        ByteBuffer buffer;
        try {
            if(this.randomDelay) sleep(this.advertiseBreak, this.rand.nextInt(500000));
            else sleep(this.advertiseBreak);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(this.contentPart == numberOfParts) {
            buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*22, this.data.length));
            this.message = new PrimaryLegacyMessage(this.rand.nextInt(),this.deviceID,buffer.array(), true);
        }
        else {
            buffer = ByteBuffer.wrap(Arrays.copyOfRange(this.data,this.contentPart*22, (this.contentPart+1)*22));
            this.message = new PrimaryLegacyMessage(this.rand.nextInt(),this.deviceID,buffer.array());
        }
        this.contentPart++;
    }
}
