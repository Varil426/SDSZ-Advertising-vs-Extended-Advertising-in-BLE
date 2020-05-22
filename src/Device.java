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
    ArrayList<Byte> receivedData = new ArrayList<>();
    ArrayList<Pair<Message, Long>> receivedMessages;
    ArrayList<Integer> advertisedOn = new ArrayList<>();
    //Number of advertise actions
    int advertiseCounter = 0;
    int advertiseFor = 3;
    //Advertising break period from 20 ms to 10 min
    long advertiseBreak;
    Integer deviceToListenTo;
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
        this.receivedMessages = new ArrayList<Pair<Message,Long>>();
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
    abstract void scan();
    abstract void advertise();
    abstract public void run();
    void generateContent() {
        this.data = new byte[this.dataSize];
        rand.nextBytes(this.data);
        for (byte b : this.data) {
            System.out.print(b + ", ");
        }
        System.out.println();
    }
    //TODO Put removeOldMessages to use
    void removeOldMessages() {
        for (int i = 0; i < this.receivedMessages.size(); i++) {
            if(this.receivedMessages.get(i).getR() + 1000000 <= System.nanoTime()) {
                this.receivedMessages.remove(i);
                i--;
            }
        }
    }
}
