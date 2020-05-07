public class Simulation {
    static class World {
        static long timeStep = 20;
        //static Time time = new Time(0);
        static long time = 0;
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
    }

    public static void main(String[] args) {
        //Set simulation stage
        for (int i = 0; i < World.numberOfChannels; i++) {
            World.channels[i] = new Channel(i);
        }
        LegacyDevice a = new LegacyDevice(1);
        LegacyDevice b = new LegacyDevice(2);
        a.generateContent();
        a.generateMessage();
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
        }
    }
}
