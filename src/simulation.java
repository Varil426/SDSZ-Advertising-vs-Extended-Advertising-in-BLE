import java.sql.Time;
import java.util.ArrayList;

public class Simulation {
    static class World {
        static int channelNumber = 40;
        static Time time = new Time(0);
        static Channel[] channels = new Channel[channelNumber];
    }
    static ArrayList<Device> devices = new ArrayList<>();
    public static void main(String[] args) {
        for (int i = 0; i < World.channelNumber; i++) {
            World.channels[i] = new Channel(i);
        }
        devices.add(new LegacyAdvertisementDevice(0,0,0));
        devices.add(new LegacyAdvertisementDevice(0,0,1));
        devices.get(0).setMode(Device.Mode.SCAN);
        devices.get(1).generateContent();
        devices.get(1).setMode(Device.Mode.ADVERTISE);
        while (true) {
            for (Device d : devices) {
                d.doAction();
            }
            for (Channel c : World.channels) {
                if(!c.empty)c.clearPayload();
            }
            //TODO Zakładamy że krok symulacji to 20ms
            World.time.setTime(World.time.getTime() + 20);
        }
    }
}
