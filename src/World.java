class World {
    private long worldStartTime;
    int numberOfChannels;
    Channel[] channels;
    static World instance;
    private World() {
        this.worldStartTime = System.nanoTime();
        this.numberOfChannels = 40;
        this.channels = new Channel[numberOfChannels];
        World.instance = this;
    }
    static World getInstance() {
        if(World.instance == null) return new World();
        return instance;
    }
}