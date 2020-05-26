import java.time.Instant;

public class Channel {
    int id;
    World world = World.getInstance();
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
            //if(this.id >= world.numberOfChannels-3)System.out.println("Primary: In Air Conflict");
            //else System.out.println("Secondary: In Air Conflict");
            if(this.id >= world.numberOfChannels-3)Result.getInstance().increasePrimary();
            else Result.getInstance().increaseSecondary();
            this.inAirConflict = true;
            this.payload = null;
        }
        this.empty = false;
        this.payloadArrivalTime = Instant.now();
        this.fullUntil = this.payloadArrivalTime.plusNanos(TTLinNS);
    }

    synchronized boolean isEmpty() {
        //Data is being deleted too fast
        /*if(!this.empty && this.fullUntil.isBefore(Instant.now())) {
            this.clearPayload();
            this.empty = true;
        }*/
        /*if(this.payload == null) return true;
        else return false;*/
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
