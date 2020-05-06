public class ExtendedAdvertisementDevice extends Device {
    ExtendedAdvertisementDevice(double x, double y, int deviceID) {
        super(x, y, deviceID);
    }

    @Override
    boolean advertise() {

        return true;
    }

    @Override
    void broadcast() {

    }
}
