import java.sql.Time;

public class Simulation {
    static class World {
        static long timeStep = 20;
        static Time time = new Time(0);
        static Channel[] channels = new Channel[40];
        static void moveTime() {
            World.time.setTime(World.time.getTime() + World.timeStep);
        }
    }

    public static void main(String[] args) {
        //Set simulation stage

        //Simulation running
        while (true) {

            World.moveTime();
        }
    }
}
