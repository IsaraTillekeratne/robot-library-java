package swarm.behaviours.intermediateBehaviours;

import swarm.behaviours.atomicBehaviours.AtomicBehaviours;
import swarm.robot.communication.SimpleCommunication;
import swarm.robot.helpers.MotionController;
import swarm.robot.indicator.NeoPixel;
import swarm.robot.sensors.DistanceSensor;
import swarm.robot.types.RGBColorType;
import swarm.robot.sensors.ColorSensor;
import java.util.Objects;

public class IntermediateBehaviours {

     public static void moveForwardWithObstacleAvoidance (
        MotionController motion, 
        int speed, 
        int duration, 
        String avoidanceAction, 
        double distanceThreshold, 
        DistanceSensor distSensor ) throws Exception{

        if (distSensor.getDistance() < distanceThreshold ) {
        if(avoidanceAction == "turn_right")
            AtomicBehaviours.turn(motion,  speed* 0.65, 1, 1000);
        else if(avoidanceAction == "turn_left")
            AtomicBehaviours.turn(motion,  speed* 0.65, -1, 1000);
        else if(avoidanceAction == "stop")
            AtomicBehaviours.moveForward(motion, 0, 1000);
        else if(avoidanceAction == "random_turn"){
            int random = -1000 + ((int) ((Math.random() * 2000)));
            int sign = (random % 2 == 0) ? 1 : -1;
            AtomicBehaviours.turn(motion,  speed* 0.65, sign, 1000);
        }
    }
    else 
        AtomicBehaviours.moveForward(motion, speed, 1000);
    }


     public static void detectObjectWithObstacleAvoidance (
        MotionController motion, 
        int speed, 
        int duration, 
        String avoidanceAction, 
        double distanceThreshold, 
        DistanceSensor distSensor,
        ColorSensor colorSensor,
        String color,
        NeoPixel neoPixel
         ) throws Exception{

        if (distSensor.getDistance() < distanceThreshold ) {
            RGBColorType detectedColor = colorSensor.getColor();
            int R=0, G=0, B=0;
        switch (color){
            case "r":
            R = 255;
            G = 0;
            B = 0;
            case "b":
            R = 0;
            G = 0;
            B = 255;
            case "g":
            R = 0;
            G = 0;
            B = 255;
        }
        
        if(detectedColor.compareTo(new RGBColorType(R, G, B))){
            neoPixel.changeColor(R, G, B);  
        } 

        else if(avoidanceAction == "turn_right")
            AtomicBehaviours.turn(motion,  speed* 0.65, 1, 1000);
        else if(avoidanceAction == "turn_left")
            AtomicBehaviours.turn(motion,  speed* 0.65, -1, 1000);
        else if(avoidanceAction == "stop")
            AtomicBehaviours.moveForward(motion, 0, 1000);
        else if(avoidanceAction == "random_turn"){
            int random = -1000 + ((int) ((Math.random() * 2000)));
            int sign = (random % 2 == 0) ? 1 : -1;
            AtomicBehaviours.turn(motion,  speed* 0.65, sign, 1000);
        }
    }
    else 
        AtomicBehaviours.moveForward(motion, speed, 1000);
    }

}  