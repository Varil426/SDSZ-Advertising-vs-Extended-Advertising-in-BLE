public class MultiDeviceCommunicationTestingLegacy {
    public static void main(String[] args) {
        //Set simulation stage
        for (int i = 0; i < World.getInstance().numberOfChannels; i++) {
            World.getInstance().channels[i] = new Channel(i);
        }
        World world = World.getInstance();

        //Legacy
        System.out.println("Legacy");
        LegacyDevice a = new LegacyDevice(0, 20, -1, 5096);
        a.generateContent();
        a.generateAdvertisement();
        a.mode = Device.Mode.ADVERTISE;

        LegacyDevice c = new LegacyDevice(1, 20, -1, 5096);
        c.generateContent();
        c.generateAdvertisement();
        c.mode = Device.Mode.ADVERTISE;

        LegacyDevice b = new LegacyDevice(2, 20, 0, 1024);
        b.mode = Device.Mode.SCAN;

        LegacyDevice d = new LegacyDevice(3, 20, 1, 1024);
        d.mode = Device.Mode.SCAN;

        //Run threads
        long start = System.nanoTime();
        a.start();
        c.start();
        b.start();
        d.start();
        //Join threads
        try {
            a.join();
            b.join();
            c.join();
            d.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Received payload size: " + b.receivedData.size());
        System.out.println("Received payload size: " + d.receivedData.size());
        System.out.println("DONE");
        System.out.println("Time: " + Math.abs(start - System.nanoTime())/1000000 + " ms");
    }
}
