public class Channel {
    int id;
    boolean empty = true;
    Message payload;
    long payloadArrivalTime;
    long fullUntil;

    Channel(int id) {
        this.id = id;
    }

    void setPayload(Message payload, long TTLinNS) {
        //TODO Konflikt w powietrzu - jeżeli chcemy ustawić nową wiadomość, a stara jeszcze jest aktywna
        this.payload = payload;
        this.empty = false;
        this.payloadArrivalTime = System.nanoTime();
        this.fullUntil = this.payloadArrivalTime + TTLinNS;
    }

    Message getPayload() {
        return payload;
    }

    void clearPayload() {
        this.payload = null;
        this.empty = true;
    }
}
