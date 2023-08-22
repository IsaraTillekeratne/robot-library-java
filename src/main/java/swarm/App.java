package swarm;

import swarm.configs.MQTTSettings;
import swarm.robot.Robot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import Robots.*;
import swarm.robot.VirtualRobot;
import swarm.robot.exception.SensorException;
import swarm.robot.sensors.ColorSensor;

public class App extends Thread {

    public static void main(String[] args) {

        try {
            // COMPLETE THIS BEFORE RUN
            // Read config properties from the file, src/resources/config/mqtt.properties
            // If it isn't there, please make one, as given sample in the
            // 'mqtt.properties' file

            File configFile = new File("src/resources/config/mqtt.properties");
            FileReader reader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(reader);

            MQTTSettings.server = props.getProperty("server");
            MQTTSettings.port = Integer.parseInt(props.getProperty("port", "1883"));
            MQTTSettings.userName = props.getProperty("username");
            MQTTSettings.password = props.getProperty("password");
            MQTTSettings.channel = props.getProperty("channel", "v1");
            reader.close();

            // Start a single robot
            Robot robot0 = new DynamicTaskAllocationRobot(0, 0, 0, 120);
            new Thread(robot0).start();

            Robot robot1 = new DynamicTaskAllocationRobot(1,75,0,120);
            new Thread(robot1).start();

            Robot robot2 = new DynamicTaskAllocationRobot(2,-75,0,120);
            new Thread(robot2).start();

            Robot robot3 = new DynamicTaskAllocationRobot(3,-75,50,120);
            new Thread(robot3).start();

            Robot robot4 = new DynamicTaskAllocationRobot(4,75,50,120);
            new Thread(robot4).start();

            Robot robot5 = new DynamicTaskAllocationRobot(5,50,100,120);
            new Thread(robot5).start();

            Robot robot6 = new DynamicTaskAllocationRobot(6,-50,100,120);
            new Thread(robot6).start();

            Robot robot7 = new DynamicTaskAllocationRobot(7,-75,-25,120);
            new Thread(robot7).start();

            Robot robot8 = new DynamicTaskAllocationRobot(8,100,-50,120);
            new Thread(robot8).start();



            // Start a swarm of robots
//            int[] robotList = { 0, 1, 2, 3, 4 };
//
//            int startX = 0;
//            int startY = 0;
//            int startHeading = 90;
//
//            Robot[] vr = new VirtualRobot[robotList.length];
//
//            for (int i = 0; i < robotList.length; i++) {
//
//                startX = startX + 10 * i;
//                startY = startY + 11 * i;
//                startHeading = startHeading + 10 * i;
//                vr[i] = new DynamicTaskAllocationRobot(robotList[i], startX, startY, startHeading);
//                new Thread(vr[i]).start();
//            }


        } catch (FileNotFoundException ex) {
            // file does not exist
            System.out.println("Config file, `resources/config/mqtt.properties` Not Found !!!");

        } catch (IOException ex) {
            // I/O error
            System.out.println("IO Error !!!");
        }
    }
}
