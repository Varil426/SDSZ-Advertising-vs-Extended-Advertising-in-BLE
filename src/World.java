class World {
    private long worldStartTime;
    int numberOfChannels;
    Channel[] channels;
    static World instance;
    private World() {
        this.worldStartTime = System.nanoTime();
        this.numberOfChannels = 40;
        this.channels = new Channel[numberOfChannels];
        this.instance = this;
    }
    static World getInstance() {
        if(instance == null) return new World();
        else return instance;
    }
}