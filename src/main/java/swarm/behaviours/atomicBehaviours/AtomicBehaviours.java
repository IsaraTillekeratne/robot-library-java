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

    public static void moveSquiggly(MotionController motion, int speed, int duration) {
    // Define variables for left and right wheel speeds
    int leftSpeed = speed;
    int rightSpeed = speed;

    // Define a variable for the angle change amount
    int angleChange = 50; // Adjust this value to control the squiggly behavior

    // Loop for half of the specified duration
    long startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTime < duration / 2) {
        // Alternate the wheel speeds to create a squiggly motion
        motion.move(leftSpeed, rightSpeed, 1000); // Adjust the duration as needed

        // Add an angle change to one of the wheels to make it squiggly
        leftSpeed += angleChange;
        // Ensure the wheel speeds stay within a reasonable range
        leftSpeed = Math.max(0, Math.min(100, leftSpeed)); // Assuming speed range is 0-100

        // Stop the other wheel to make a half circle
        rightSpeed = 0;
    }

    // Swap the direction by reversing the angle change
    angleChange = -angleChange;

    // Loop for the second half of the specified duration
    startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTime < duration / 2) {
        // Alternate the wheel speeds to create a squiggly motion
        motion.move(leftSpeed, rightSpeed, 100); // Adjust the duration as needed

        // Add an angle change to one of the wheels to make it squiggly
        leftSpeed += angleChange;
        // Ensure the wheel speeds stay within a reasonable range
        leftSpeed = Math.max(0, Math.min(100, leftSpeed)); // Assuming speed range is 0-100

        // Stop the other wheel to complete the circle in the opposite direction
        rightSpeed = 0;
    }

    // Stop the motion after completing the full half circle in each direction
    motion.move(0, 0, 100);
}

public static void moveHalfCircle(MotionController motion, double speed, int duration) {
    // Calculate the speeds for turning in a half-circle
    double leftSpeed = speed;
    double rightSpeed = -speed;  // Negative speed to make a left turn

   
        motion.move((int)leftSpeed, (int)rightSpeed, 200); // Adjust the duration as needed
  
    motion.move(200, 200, 200);
}

}
