class Result {
    static Result instance;
    int id;
    boolean randomDelay=true;
    int sentData=0;
    int receivedData=0;
    long timeMS=0;
    private int primaryConflicts=0;
    private int secondaryConflicts=0;

    private Result(){
        this.randomDelay=true;
        this.sentData=0;
        this.receivedData=0;
        this.timeMS=0;
        this.primaryConflicts=0;
        this.secondaryConflicts=0;
        Result.instance = this;
    }

    static Result getInstance() {
        if (Result.instance == null) return new Result();
        return Result.instance;
    }

    void reset() {
        this.randomDelay=true;
        this.sentData=0;
        this.receivedData=0;
        this.timeMS=0;
        this.primaryConflicts=0;
        this.secondaryConflicts=0;
    }
    void setResults(int id, int sentData, int receivedData, long timeMS) {
        this.id = id;
        this.sentData = sentData;
        this.receivedData = receivedData;
        this.timeMS = timeMS;
    }
    synchronized void increasePrimary(){
        this.primaryConflicts++;
    }
    synchronized void increaseSecondary(){
        this.secondaryConflicts++;
    }
    void print() {
        System.out.println(this.id + "\t" + this.sentData + "\t" + this.receivedData + "\t" + this.timeMS + "\t" + this.primaryConflicts + "\t" + this.secondaryConflicts);
    }
}