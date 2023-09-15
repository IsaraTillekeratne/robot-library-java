package Robots;

import swarm.robot.VirtualRobot;
import swarm.robot.sensors.DistanceSensor;
import swarm.robot.types.ProximityReadingType;
import swarm.behaviours.atomicBehaviours.AtomicBehaviours;
import swarm.robot.indicator.NeoPixel;

public class RandomBehaviour extends VirtualRobot {

    // The minimum distance that robot tries to keep with the obstacles
    private int distanceThreshold = 10;
    private int moveBackDistanceThreshold = 35;
    private int sideDistanceThreshold = 25;

    // The default movement speed
    private int defaultMoveSpeed = 100;

    public RandomBehaviour(int id, double x, double y, double heading) {
        super(id, x, y, heading);
    }

    public void setup() {
        super.setup();
    }

    @Override
    public void loop() throws Exception {
        super.loop();

        if (state == robotState.RUN) {
AtomicBehaviours.moveForward(motion, defaultMoveSpeed, 2000);
AtomicBehaviours.turn(motion, defaultMoveSpeed*0.9, 1 , 1000);
neoPixel.changeColor(0, 0, 255);
AtomicBehaviours.moveForward(motion, defaultMoveSpeed, 2000);
neoPixel.changeColor(0, 255, 0);

        }
    }
}
