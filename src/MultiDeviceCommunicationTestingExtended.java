public class MultiDeviceCommunicationTestingExtended {
    public static void main(String[] args) {
        //Set simulation stage
        for (int i = 0; i < World.getInstance().numberOfChannels; i++) {
            World.getInstance().channels[i] = new Channel(i);
        }
        World world = World.getInstance();

        //Extended
        System.out.println("Extended");
        ExtendedDevice a = new ExtendedDevice(0, 20, -1, 1024);
        a.generateContent();
        a.generatePrimaryAdvertisement();
        a.mode = Device.Mode.ADVERTISE;

        ExtendedDevice c = new ExtendedDevice(1, 20, -1, 1024);
        c.generateContent();
        c.generatePrimaryAdvertisement();
        c.mode = Device.Mode.ADVERTISE;

        ExtendedDevice b = new ExtendedDevice(2, 20, 0, 1024);
        b.mode = Device.Mode.SCAN;

        ExtendedDevice d = new ExtendedDevice(3, 20, 1, 1024);
        d.mode = Device.Mode.SCAN;
        //Run threads
        b.start();
        d.start();
        a.start();
        c.start();
        //Join threads
        try {
            a.join();
            b.join();
            c.join();
            d.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(b.receivedData.size());
        System.out.println(d.receivedData.size());
        System.out.println("DONE");
    }
}
