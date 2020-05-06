import java.sql.Time;

public class Channel {
    int id;
    boolean empty = true;
    byte[] payload = new byte[255];
    Time payloadArrival;

    Channel(int id) {
        this.id = id;
    }

    void setPayload(byte[] payload) throws Exception {
        if(payload.length > 255) throw new Exception("Payload size too big");
        this.payload = payload;
        this.empty = false;
        this.payloadArrival = new Time(Simulation.World.time.getTime());
    }
    byte[] getPayload() {
        return payload;
    }
    void clearPayload() {
        if(Simulation.World.time.getTime() > this.payloadArrival.getTime() + 20) {
            this.payload = null;
            this.empty = true;
        }
    }
}
