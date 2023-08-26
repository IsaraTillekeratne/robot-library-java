package swarm.behaviours.atomicBehaviours;

import swarm.robot.helpers.MotionController;

public class AtomicBehaviours {

     public static void moveForward (MotionController motion, int speed, int duration){
        motion.move(speed, speed, duration);
    }

    public static void moveBack (MotionController motion, int speed, int duration){
        motion.move(-1*speed, -1*speed, duration);
    }

    public static void turn (MotionController motion, double defaultMoveSpeed, int sign, int duration){
        motion.rotate((int) (defaultMoveSpeed * sign), 1000);
    }

}
