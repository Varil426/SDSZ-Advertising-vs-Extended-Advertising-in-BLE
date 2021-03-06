import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

public abstract class Device extends Thread {
    public class Pair<L,R> {
        private L l;
        private R r;
        public Pair(L l, R r){
            this.l = l;
            this.r = r;
        }
        public L getL(){ return l; }
        public R getR(){ return r; }
        public void setL(L l){ this.l = l; }
        public void setR(R r){ this.r = r; }
    }
    double[] position;
    int deviceID;
    Random rand = new Random();
    int dataSize;
    byte[] data;
    boolean randomDelay = true;
    ArrayList<Byte> receivedData = new ArrayList<>();
    ArrayList<Pair<Message, Instant>> receivedMessages;
    ArrayList<Integer> advertisedOn = new ArrayList<>();
    //Number of advertise actions
    int advertiseCounter = 0;
    int advertiseFor = 3;
    //Advertising break period from 20 ms to 10 min
    long advertiseBreak;
    Integer deviceToListenTo;
    Instant scanningSince;
    enum Mode {
        WAIT,
        SCAN,
        LISTEN,
        SECONDARY,
        ADVERTISE,
        FINISHED
    }
    Mode mode = Mode.WAIT;
    Device(){
        this.deviceID = 0;
        this.position = new double[]{0.0, 0.0};
        this.receivedMessages = new ArrayList<Pair<Message, Instant>>();
        this.advertiseBreak = 20;
        this.dataSize = 1024;
    }
    Device(int deviceID) {
        this();
        this.deviceID = deviceID;
    }
    Device(int deviceID, long advertiseBreak) {
        this();
        this.deviceID = deviceID;
        if(advertiseBreak < 20) this.advertiseBreak = 20;
        else if (advertiseBreak > 10*60*1000) this.advertiseBreak = 10*60*1000;
        else this.advertiseBreak = advertiseBreak;
    }
    Device(int deviceID, long advertiseBreak, int deviceToListenTo, int dataSize) {
        this(deviceID, advertiseBreak);
        this.deviceToListenTo = deviceToListenTo;
        this.dataSize = dataSize;
    }
    Device(int deviceID, long advertiseBreak, int deviceToListenTo, int dataSize, boolean randomDelay) {
        this(deviceID, advertiseBreak, deviceToListenTo, dataSize);
        this.randomDelay = randomDelay;
    }
    abstract void scan();
    abstract void advertise();
    abstract public void run();
    void generateContent() {
        this.data = new byte[this.dataSize];
        rand.nextBytes(this.data);
        /*for (byte b : this.data) {
            System.out.print(b + ", ");
        }
        System.out.println();*/
    }
    boolean isMessageNew(Message receivedMessage) {
        if(!this.receivedMessages.isEmpty()) {
            for (Pair m : this.receivedMessages) {
                Message myMessage = (Message) m.getL();
                if(myMessage.messageID == receivedMessage.messageID && myMessage.senderID == receivedMessage.senderID) {
                    return false;
                }
            }
        }
        return true;
    }
    Pair<Long, Integer> getMillisAndNanos(long number) {
        long millis = (long) Math.floor(number/1000000);
        int nanos = (int) number%1000000;
        return new Pair<>(millis, nanos);
    }
    void removeOldMessages() {
        for (int i = 0; i < this.receivedMessages.size(); i++) {
            //Remove messages older than 500 ms
            if(this.receivedMessages.get(i).getR().isAfter(Instant.now().plusMillis(500))) {
                this.receivedMessages.remove(i);
                i--;
            }
        }
    }
    void scanningCheck() {
        if(this.scanningSince == null)this.scanningSince = Instant.now();
        else if(this.scanningSince.plusMillis(1000).isBefore(Instant.now())) {
            this.mode = Mode.FINISHED;
            //System.out.println("Scanning for nothing");
        }
    }
}
