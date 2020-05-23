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
    @Override
    void scan() {
        for (int i = World.getInstance().numberOfChannels-3; i < World.getInstance().numberOfChannels; i++) {
            if(!World.getInstance().channels[i].isEmpty()) {
                Message currentMessage = World.getInstance().channels[i].getPayload();
                if(!(currentMessage instanceof PrimaryLegacyMessage) || (this.deviceToListenTo != null && currentMessage.senderID != this.deviceToListenTo))continue;
                if(this.isMessageNew(currentMessage)) {
                    this.receivedMessages.add(new Pair<Message, Instant>(currentMessage, Instant.now()));
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
        //TODO tmp zmienić żeby brało pod uwagę rozmiar wiadomości a nie stałe
        //Multiply by 1000000000 to get time to sent in nanoseconds
        long tmp = (long) Math.ceil((32*1000)/1048576)*1000000;
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
            if(this.contentPart <= (int) Math.ceil(this.data.length/23)) {
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
            try {
                sleep(0,this.rand.nextInt(1000000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
            }
        }
    }

    void generateAdvertisement() {
        this.advertiseCounter = 0;
        this.advertisedOn.clear();
        int numberOfParts = (int) Math.ceil(this.data.length/23);
        ByteBuffer buffer;
        try {
            sleep(this.advertiseBreak, this.rand.nextInt(500000));
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
