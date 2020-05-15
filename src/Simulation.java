public class Simulation {
    static class World {
        static private long worldStartTime = System.nanoTime();
        static int numberOfChannels = 40;
        static Channel[] channels = new Channel[numberOfChannels];
    }

    public static void main(String[] args) {
        //Set simulation stage
        for (int i = 0; i < World.numberOfChannels; i++) {
            World.channels[i] = new Channel(i);
        }
        //TODO
        LegacyDevice a = new LegacyDevice(0);
        a.generateContent();
        System.out.println(a.data.length);
        a.mode = Device.Mode.ADVERTISE;
        LegacyDevice b = new LegacyDevice(1);
        b.mode = Device.Mode.SCAN;
        //Run threads
        a.start();
        b.start();
        //Join threads
        try {
            a.join();
            b.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("DONE");
    }
}
