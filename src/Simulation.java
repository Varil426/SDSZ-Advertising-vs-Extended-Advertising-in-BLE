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
        //Legacy
        System.out.println("Legacy");
        LegacyDevice a = new LegacyDevice(0);
        a.generateContent();
        a.generateAdvertisement();
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

        //Extended
        System.out.println("Extended");
        ExtendedDevice c = new ExtendedDevice(0);
        c.generateContent();
        c.generatePrimaryAdvertisement();
        System.out.println(c.data.length);
        c.mode = Device.Mode.ADVERTISE;
        ExtendedDevice d = new ExtendedDevice(1);
        d.mode = Device.Mode.SCAN;
        //Run threads
        c.start();
        d.start();
        //Join threads
        try {
            c.join();
            d.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("DONE");

    }
}
