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
    byte[] data;
    //TODO receivedData zastanowić się
    ArrayList<Byte> receivedData = new ArrayList<>();
    //ArrayList<Message> receivedMessages;
    ArrayList<Pair<Message, Long>> receivedMessages;
    ArrayList<Integer> advertisedOn = new ArrayList<>();
    //Number of advertise actions
    int advertiseCounter = 0;
    int advertiseFor = 3;
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
    }
    Device(int deviceID) {
        this();
        this.deviceID = deviceID;
    }
    abstract void scan();
    abstract void advertise();
    abstract public void run();
    void generateContent() {
        this.data = new byte[rand.nextInt(2550)];
        //this.data = new byte[343];
        rand.nextBytes(this.data);
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
