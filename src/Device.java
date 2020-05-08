import java.util.ArrayList;
import java.util.Random;

public abstract class Device {
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
    //ArrayList<Message> receivedMessages;
    ArrayList<Pair<Message, Integer>> receivedMessages;
    //Number of advertise actions
    int advertiseCounter = 0;
    int advertiseFor = 5;
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
        this.receivedMessages = new ArrayList<Pair<Message,Integer>>();
    }
    Device(int deviceID) {
        this();
        this.deviceID = deviceID;
    }
    abstract void scan();
    abstract void advertise();
    void generateContent() {
        this.data = new byte[rand.nextInt(2550)];
        rand.nextBytes(this.data);
    }
    //TODO czy nie zmienić TTL na czasowe (long)
    void decreaseTTL() {
        for (int i = 0; i < this.receivedMessages.size(); i++) {
            this.receivedMessages.get(i).setR(this.receivedMessages.get(i).getR()-1);
            if(this.receivedMessages.get(i).getR()==0) {
                this.receivedMessages.remove(i);
                i--;
            }
        }
    }
}
