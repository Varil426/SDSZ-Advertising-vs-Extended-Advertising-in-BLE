import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public abstract class Device {
    double[] position = {0d,0d};
    int deviceID;
    enum Mode {
        WAIT,
        LISTEN,
        SCAN,
        BROADCAST,
        ADVERTISE
    }
    Mode mode = Mode.WAIT;
    Random rand = new Random();
    //TODO boolean shouldListen = false;
    byte[] content;
    int channelToBroadcast;
    long timeToBroadcast;
    int channelToListen;
    long timeToListen;
    Device() {
        this.deviceID = 0;
    }
    Device(double x, double y, int deviceID){
        this.position[0] = x;
        this.position[1] = y;
        this.deviceID = deviceID;
    }
    abstract boolean advertise();
    abstract void broadcast();
    void scan() {
        ByteBuffer buffer = null;
        for (int i = 37; i < 40; i++) {
            if(!Simulation.World.channels[i].empty) {
                buffer = ByteBuffer.wrap(Simulation.World.channels[i].payload);
                break;
            }
        }
        if(buffer == null) {
            //TODO
            return;
        }
        /*for (byte b: buffer.array()) {
            System.out.print(b + " ");
        }
        System.out.println();*/
        byte[] message = buffer.array();
        this.channelToListen = message[0];
        this.timeToListen = Simulation.World.time.getTime() + ByteBuffer.wrap(Arrays.copyOfRange(message, 1, 9)).getLong();
        //System.out.println(channelToListen + " " + timeToListen);
        this.mode = Mode.LISTEN;
    }
    void setMode(Mode mode) {
        this.mode = mode;
    }
    Mode getMode() {
        return this.mode;
    }
    void generateContent(){
        this.content = new byte[rand.nextInt(2550)];
        rand.nextBytes(this.content);
        /*for (byte b: content) {
            System.out.print(b + " ");
        }*/
    }
    void listen() {
        //System.out.println(this.timeToListen + " " + Simulation.World.time.getTime());
        if(this.timeToListen <= Simulation.World.time.getTime() + 20 && timeToListen >= Simulation.World.time.getTime()) {
            ByteBuffer buffer = ByteBuffer.wrap(Simulation.World.channels[this.channelToListen].payload);

            byte[] c = buffer.array();
            for(byte b:c){
                System.out.print(b + " ");
            }
            System.out.println();

            this.mode = Mode.SCAN;
        }
    }

    void doAction(){
        switch (this.mode) {
            case WAIT:
                break;
            case LISTEN:
                this.listen();
                break;
            case SCAN:
                this.scan();
                break;
            case BROADCAST:
                this.broadcast();
                break;
            case ADVERTISE:
                this.advertise();
                break;
        }
    }
}
