import java.time.Instant;

public class Channel {
    int id;
    private boolean empty = true;
    Message payload;
    Instant payloadArrivalTime;
    Instant fullUntil;

    Channel(int id) {
        this.id = id;
    }

    void setPayload(Message payload, long TTLinNS) {
        //TODO Konflikt w powietrzu - jeżeli chcemy ustawić nową wiadomość, a stara jeszcze jest aktywna
        //TODO Synchronizacja na obiekcie
        this.payload = payload;
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

    Message getPayload() {
        return this.payload;
    }

    void clearPayload() {
        this.payload = null;
        this.empty = true;
    }
}
