// This robot will move freely, avoiding obstacles 
// Written by Nuwan Jaliyagoda 

package Robots;

import swarm.robot.VirtualRobot;
import swarm.robot.sensors.DistanceSensor;
import swarm.robot.types.ProximityReadingType;

public class ObstacleAvoidanceRobot extends VirtualRobot {

    // The minimum distance that robot tries to keep with the obstacles
    private int distanceThreshold = 43;
    private int moveBackDistanceThreshold = 35;

    // The default movement speed
    private int defaultMoveSpeed = 100;

    public ObstacleAvoidanceRobot(int id, double x, double y, double heading) {
        super(id, x, y, heading);
    }

    public void setup() {
        super.setup();
    }

    @Override
    public void loop() throws Exception {
        super.loop();

        if (state == robotState.RUN) {
            // double dist = distSensor.getDistance();
            ProximityReadingType proximity = proximitySensor.getProximity();
            int F_DIST = proximity.getReadings()[2];
            int LB_DIST = proximity.getReadings()[0];
            int L_DIST = proximity.getReadings()[1];
            int R_DIST = proximity.getReadings()[3];
            int RB_DIST = proximity.getReadings()[4];

                System.out.println("\t pros: " + proximity + " dist:  " + F_DIST);


            if (F_DIST < distanceThreshold || L_DIST < distanceThreshold || R_DIST<distanceThreshold) {
                // Generate a random number in [-1000,1000] range
                // if even, rotate CW, otherwise rotate CCW an angle depends on the random
                // number
                int random = -1000 + ((int) ((Math.random() * 2000)));
                // int sign = (random % 2 == 0) ? 1 : -1;

                // System.out.println("\t Wall detected, go back and rotate " + ((sign > 0) ? "CW" : "CCW"));

                if (RB_DIST > moveBackDistanceThreshold && LB_DIST > moveBackDistanceThreshold ) {
                    // Go back a little
                    motion.move(-100, -100, 400);
                }

                ProximityReadingType prox = proximitySensor.getProximity();
            
                int L = prox.getReadings()[1];
                int R = prox.getReadings()[3];

                int sign ;
                if(L > 25) sign = 1;
                else if(R > 25) sign = -1;
                else sign = (random % 2 == 0) ? 1 : -1;

                // rotate
                int loopCount = 0;
                // while (distSensor.getDistance() < distanceThreshold - 8 && loopCount < 5) {
                while (distSensor.getDistance() < distanceThreshold - 8 && loopCount < 5) {
                    // Maximum 5 tries to rotate and find a obstale free path
                    motion.rotate((int) (defaultMoveSpeed * 0.65 * sign), 1000);
                    loopCount++;
                }

                // rotate a little more
                motion.rotate((int) (defaultMoveSpeed * 0.75 * sign), 500);

            } else {
                motion.move(defaultMoveSpeed, defaultMoveSpeed, 900);
            }
        }
    }
}
