import java.time.Instant;

public class Channel {
    int id;
    private boolean empty = true;
    Message payload;
    Instant payloadArrivalTime;
    Instant fullUntil = Instant.now();
    boolean inAirConflict = false;

    Channel(int id) {
        this.id = id;
    }

    synchronized void setPayload(Message payload, long TTLinNS) {
        if(this.fullUntil.isBefore(Instant.now())) {
            this.payload = payload;
            this.inAirConflict = false;
        } else {
            System.out.println("In Air Conflict");
            this.inAirConflict = true;
            this.payload = null;
        }
        this.empty = false;
        this.payloadArrivalTime = Instant.now();
        this.fullUntil = this.payloadArrivalTime.plusNanos(TTLinNS);
    }

    boolean isEmpty() {
        //TODO Usuwanie danych od razu powoduje problemy, dużo paczek jest pomijanych
        /*if(!this.empty && this.fullUntil.isBefore(Instant.now())) {
            this.clearPayload();
            this.empty = true;
        }*/
        return this.empty;
    }

    synchronized Message getPayload() {
        return this.payload;
    }

    void clearPayload() {
        this.payload = null;
        this.empty = true;
    }
}
