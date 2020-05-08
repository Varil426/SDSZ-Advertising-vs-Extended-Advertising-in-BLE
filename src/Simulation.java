public class Simulation {
    static class World {
        static long timeStep = 20;
        //static Time time = new Time(0);
        static private long time = 0;
        static int numberOfChannels = 40;
        static Channel[] channels = new Channel[numberOfChannels];
        static void clearChannels() {
            for (Channel c : World.channels) {
                c.clearPayload();
            }
        }
        static void moveTime() {
            //World.time.setTime(World.time.getTime() + World.timeStep);
            World.time = World.time + World.timeStep;
        }
        static long getTime() {
            return World.time;
        }
    }

    public static void main(String[] args) {
        //Set simulation stage
        for (int i = 0; i < World.numberOfChannels; i++) {
            World.channels[i] = new Channel(i);
        }
        /*LegacyDevice a = new LegacyDevice(1);
        LegacyDevice b = new LegacyDevice(2);
        a.generateContent();
        a.generateAdvertisement();
        a.mode = Device.Mode.ADVERTISE;
        b.mode = Device.Mode.SCAN;
        //Simulation running
        while (true) {
            System.out.println(World.time);
            b.scan();
            World.clearChannels();
            if(a.mode == Device.Mode.FINISHED)break;
            a.advertise();
            World.moveTime();
        }*/
        ExtendedDevice a = new ExtendedDevice(1);
        ExtendedDevice b = new ExtendedDevice(2);
        a.mode = Device.Mode.SCAN;
        b.mode = Device.Mode.ADVERTISE;
        b.generateContent();
        b.generatePrimaryAdvertisement();
        while (true) {

            //System.out.println(b.primary.channel + " " + b.primary.time + " " + World.channels[b.primary.channel].empty);

            System.out.println(World.time);
            if(a.mode == Device.Mode.SCAN)a.scan();
            else if (a.mode == Device.Mode.LISTEN)a.secondaryListen();
            if(b.mode == Device.Mode.FINISHED)break;
            World.clearChannels();
            if(b.mode == Device.Mode.ADVERTISE)b.advertise();
            else if (b.mode == Device.Mode.SECONDARY)b.secondaryAdvertise();
            World.moveTime();
        }
    }
}
