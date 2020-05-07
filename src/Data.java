import java.nio.ByteBuffer;

public abstract class Data extends Message {
    byte[] payload;
    void setPayload(byte[] payload) {
        //Used to copy message payload, not just copy reference
        this.payload = ByteBuffer.wrap(payload).array();
        //this.payload = payload;
    }
    byte[] getPayload() {
        return ByteBuffer.wrap(this.payload).array();
    }
}
