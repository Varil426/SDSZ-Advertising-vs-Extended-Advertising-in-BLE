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
        //TODO Synchronizacja na obiekcie
        this.payload = payload;
        this.empty = false;
        this.payloadArrivalTime = System.nanoTime();
        this.fullUntil = this.payloadArrivalTime + TTLinNS;
    }

    boolean isEmpty() {
        //TODO Usuwanie danych od razu powoduje problemy, dużo paczek jest pomijanych
        /*if(!this.empty && this.fullUntil - System.nanoTime() < 0) {
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
