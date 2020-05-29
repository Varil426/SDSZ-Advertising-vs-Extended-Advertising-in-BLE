import java.util.ArrayList;

public class Simulation {
    public static void main(String[] args) {
        //Set simulation stage
        World world = World.getInstance();
        for (int i = 0; i < world.numberOfChannels; i++) {
            World.getInstance().channels[i] = new Channel(i);
        }
        int loops = 100;
        ArrayList<Device> devices = new ArrayList<>();

        int dataSize = 0;
        for (int i = 0; i < loops; i++) {
            if(i%10==0)dataSize+=1024;
            devices.add(setUpExtendedDeviceForAdvertising(20,dataSize,true));
            devices.add(setUpExtendedDeviceForAdvertising(20,dataSize,true));
            devices.add(setUpExtendedDeviceForListening(0));
            devices.add(setUpExtendedDeviceForListening(1));
            Simulation.deviceIDs = 0;

            long start = System.nanoTime();
            for (Device device : devices) {
                device.start();
            }
            for (Device device : devices) {
                try {
                    device.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long finish = System.nanoTime();

            //Get data
            int sentData = 0;
            int receivedData = 0;
            for (Device device : devices) {
                sentData += device.dataSize;
                receivedData += device.receivedData.size();
            }

            devices.clear();
            for (Channel channel : World.getInstance().channels) {
                channel.clearPayload();
            }

            Result.getInstance().setResults(i, sentData, receivedData, Math.abs(start - finish)/1000000);
            Result.getInstance().print();
            Result.getInstance().reset();
        }
    }
    static int deviceIDs = 0;
    static LegacyDevice setUpLegacyDeviceForAdvertising(int advertiseBreak, int dataSize, boolean randomDelay) {
        LegacyDevice device = new LegacyDevice(Simulation.deviceIDs, advertiseBreak, -1, dataSize, randomDelay);
        Simulation.deviceIDs++;
        device.generateContent();
        device.generateAdvertisement();
        device.mode = Device.Mode.ADVERTISE;
        return device;
    }

    static LegacyDevice setUpLegacyDeviceForListening(int deviceToListenTo) {
        LegacyDevice device = new LegacyDevice(Simulation.deviceIDs, 20, deviceToListenTo, 0);
        Simulation.deviceIDs++;
        device.mode = Device.Mode.SCAN;
        return device;
    }

    static ExtendedDevice setUpExtendedDeviceForAdvertising(int advertiseBreak, int dataSize, boolean randomDelay) {
        ExtendedDevice device = new ExtendedDevice(Simulation.deviceIDs, advertiseBreak, -1, dataSize, randomDelay);
        Simulation.deviceIDs++;
        device.generateContent();
        device.generatePrimaryAdvertisement();
        device.mode = Device.Mode.ADVERTISE;
        return device;
    }
    static ExtendedDevice setUpExtendedDeviceForListening(int deviceToListenTo){
        ExtendedDevice device = new ExtendedDevice(Simulation.deviceIDs, 20, deviceToListenTo, 0);
        Simulation.deviceIDs++;
        device.mode = Device.Mode.SCAN;
        return device;
    }
}
