public class Channel {
    int id;
    boolean empty = true;
    Message payload;

    Channel(int id) {
        this.id = id;
    }

    void setPayload(Message payload) {
        //TODO coś sprawjące czy istnieje jakaś wiaodmość, czy nie jest spierdolona
        this.payload = payload;
        this.empty = false;
        //this.payloadArrival = new Time(Simulation.World.time.getTime());
    }

    Message getPayload() {
        return payload;
    }

    void clearPayload() {
        this.payload = null;
        this.empty = true;
    }
}
