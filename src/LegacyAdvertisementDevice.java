import java.nio.ByteBuffer;
import java.util.Arrays;

public class LegacyAdvertisementDevice extends Device {
    int broadcastCounter = 0;
    LegacyAdvertisementDevice(double x, double y, int deviceID) {
        super(x, y, deviceID);
    }

    @Override
    boolean advertise() {
        int channel = Simulation.World.channelNumber-3;
        while (!Simulation.World.channels[channel].empty) {
            channel++;
            if(channel >= Simulation.World.channelNumber)return false;
        }
        //TODO czy dodac do advertising jaka wersja?
        //TODO end of message marker?
        ByteBuffer buffer = ByteBuffer.allocate(13);//1 channel, 8 time, 4 deviceID
        this.channelToBroadcast = rand.nextInt(37);
        //TODO +0 czas
        this.timeToBroadcast = Simulation.World.time.getTime()+20;
        buffer.put((byte) this.channelToBroadcast);
        buffer.putLong(this.timeToBroadcast);
        buffer.putInt(this.deviceID);
        try {
            Simulation.World.channels[channel].setPayload(buffer.array());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mode = Mode.BROADCAST;
        return true;
    }

    @Override
    void broadcast() {
        double transmissions = Math.ceil(this.content.length/31);
        if(transmissions <= this.broadcastCounter)return;
        byte[] slice;
        if(31*(this.broadcastCounter +1) > this.content.length && this.content.length > 31*this.broadcastCounter) {
            slice = Arrays.copyOfRange(this.content,0+31*this.broadcastCounter,this.content.length);
            this.mode = Mode.WAIT;
        } else {
            slice = Arrays.copyOfRange(this.content,0+31*this.broadcastCounter,31+31*this.broadcastCounter);
            this.mode = Mode.ADVERTISE;
        }
        try {
            Simulation.World.channels[this.channelToBroadcast].setPayload(slice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
