package Robots;

public class DynamicTaskAllocationRobot extends ObstacleAvoidanceRobot{
    public DynamicTaskAllocationRobot(int id, double x, double y, double heading) {
        super(id, x, y, heading);
    }

    public void setup() {
        super.setup();
    }

    public void loop() throws Exception {
        super.loop();
        neoPixel.changeColor(255,0,0);

    }
}
