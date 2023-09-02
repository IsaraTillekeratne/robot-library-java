// This robot will move freely, avoiding obstacles 
// Written by Nuwan Jaliyagoda 

package Robots;

import swarm.robot.VirtualRobot;
import swarm.robot.sensors.DistanceSensor;
import swarm.robot.types.ProximityReadingType;
import swarm.behaviours.atomicBehaviours.AtomicBehaviours;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ObstacleAvoidanceRobot extends VirtualRobot {

    // The minimum distance that robot tries to keep with the obstacles
    private int distanceThreshold = 15;
    private int moveBackDistanceThreshold = 35;
    private int sideDistanceThreshold = 25;

    // The default movement speed
    private int defaultMoveSpeed = 100;
//    ExecutorService executor = Executors.newFixedThreadPool(1);

    int j = 0;

    public ObstacleAvoidanceRobot(int id, double x, double y, double heading) {
        super(id, x, y, heading);
    }

    public void setup() {

        super.setup();

    }

    @Override
    public void loop() throws Exception {
        super.loop();
//        runObstacleAvoidanceWithRandomMove();
        action1Future = executor.submit(() -> {
            try {
//                j = j + 1;
//                System.out.println("Obs avoid start: "+j);
                runObstacleAvoidanceWithRandomMove();
//                System.out.println("Obs avoidance end: "+j);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
//        action1Future.get();

    }

    public void runObstacleAvoidanceWithRandomMove() throws Exception{

        if (state == robotState.RUN) {
            int F_DIST , LB_DIST, L_DIST, R_DIST, RB_DIST;

            int[] readings = proximitySensor.getProximity().getReadings();
            F_DIST = readings[2];
            LB_DIST = readings[0];
            L_DIST = readings[1];
            R_DIST = readings[3];
            RB_DIST = readings[4];



            if (F_DIST < distanceThreshold || L_DIST < distanceThreshold || R_DIST<distanceThreshold) {

                // moveback if there is space in the back (right & left)
                if (RB_DIST > moveBackDistanceThreshold && LB_DIST > moveBackDistanceThreshold ) {
                    AtomicBehaviours.moveBack(motion, 100, 400);
                }

                ProximityReadingType prox = proximitySensor.getProximity();

                int Left = prox.getReadings()[1];
                int Right = prox.getReadings()[3];

                int sign ;
                int random = -1000 + ((int) ((Math.random() * 2000)));

                // determine turning side based on the distance on both sides
                if(Left > sideDistanceThreshold) sign = 1;
                else if(Right > sideDistanceThreshold) sign = -1;
                else sign = (random % 2 == 0) ? 1 : -1;

                // rotate
                int loopCount = 0;
                while (distSensor.getDistance() < distanceThreshold - 8 && loopCount < 5) {
                    // Maximum 5 tries to rotate and find a obstale free path
                    AtomicBehaviours.turn(motion,  defaultMoveSpeed* 0.65, sign, 1000);
                    loopCount++;
                }

                // rotate a little more
                AtomicBehaviours.turn(motion,  defaultMoveSpeed* 0.75, sign, 500);


            } else {
                AtomicBehaviours.moveForward(motion, defaultMoveSpeed, 1000);
            }
        }

    }
}