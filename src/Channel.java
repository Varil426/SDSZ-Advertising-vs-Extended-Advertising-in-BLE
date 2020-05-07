import java.sql.Time;

public class Channel {
    int payload_size = 255;
    int id;
    boolean empty = true;
    Message payload;
    //Time payloadArrival;

    Channel(int id) {
        this.id = id;
    }

    void setPayload(Message payload) throws Exception {
        //TODO coś sprawjące czy istnieje jakaś wiaodmość, czy nie jest spierdolona
        this.payload = payload;
        this.empty = false;
        //this.payloadArrival = new Time(Simulation.World.time.getTime());
    }

    Message getPayload() {// to chyba na chuj
        return payload;
    }

    void clearPayload()  throws Exception {
        //if (payload.empty) throw new Exception("Kurwa to nie ma szans że będzie puste");
        this.payload = null;
        this.empty = true;
    }
}
