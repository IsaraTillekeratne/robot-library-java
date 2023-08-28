package swarm.behaviours.atomicBehaviours;

import swarm.robot.communication.SimpleCommunication;
import swarm.robot.helpers.MotionController;
import swarm.robot.indicator.NeoPixel;

import java.util.Objects;

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

    public void showSelectedTask(String selectedTask, NeoPixel neoPixel, SimpleCommunication simpleComm, int robotID){ // Atomic Behaviour
        if(Objects.equals(selectedTask, "r")){
            // robot show color white (representing task red)
            neoPixel.changeColor(255,255,255);
            // robot sends r msg to neighbouring robots within 50 radius
            simpleComm.sendMessage("r",50);
        }else if(Objects.equals(selectedTask, "b")){
            // robot show color green (representing task blue)
            neoPixel.changeColor(0,255,0);
            // robot sends b msg to neighbouring robots within 50 radius
            simpleComm.sendMessage("b",50);
        }
        System.out.println("Robot: "+robotID+" "+"selected task: "+ selectedTask);
        System.out.println();
    }

}
