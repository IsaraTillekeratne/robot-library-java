package Robots;

import swarm.behaviours.atomicBehaviours.AtomicBehaviours;
import swarm.behaviours.clusterBehaviours.ClusterBehaviours;
import swarm.mqtt.MqttMsg;
import swarm.robot.exception.SensorException;
import swarm.robot.types.RGBColorType;

import csvRecorder.CsvRecorder;

import java.util.*;
import java.util.concurrent.Future;

import static swarm.behaviours.clusterBehaviours.Helpers.printQueue;

public class DynamicTaskAllocationRobot extends ObstacleAvoidanceRobot{

    ClusterBehaviours clusterBehaviours = new ClusterBehaviours();
    AtomicBehaviours atomicBehaviours = new AtomicBehaviours();
    Queue<String> taskDemandQueue = new LinkedList<>();
    Queue<String> taskSupplyQueue = new LinkedList<>();
    static int fixedQueueLength = 5;
    static float scalingFactor = 0.015f;
    static int n = 10; // steepness of task selection probability
    float responseThresholdRed;
    float responseThresholdBlue;
    float responseThresholdRedNext;
    float responseThresholdBlueNext;
    float estimatedTaskDemandForRed;
    float estimatedTaskDemandForBlue;
    float estimatedTaskSupplyForRed;
    float estimatedTaskSupplyForBlue;
    float taskSelectionProbabilityRed;
    float taskSelectionProbabilityBlue;
    String selectedTask;
    int timeStep = 0;

    long startTime;
    int robotId;

    public DynamicTaskAllocationRobot(int id, double x, double y, double heading, long time) {
        super(id, x, y, heading);
        robotId = id;
        this.startTime = time;
    }

    public void setup() {

        super.setup();

        // initially assign task Red
        selectedTask = "r";
        atomicBehaviours.showSelectedTask(selectedTask,this.neoPixel,this.simpleComm,robotId);

        // assign initial random thresholds
        Random rand = new Random();

        this.responseThresholdRed = rand.nextFloat();
        this.responseThresholdBlue = rand.nextFloat();

        CsvRecorder.recordInitialThresholdValues("src/main/java/csvRecorder/record.csv",this.getId(),this.responseThresholdRed,this.responseThresholdBlue);

        System.out.println("Robot "+this.id+" is running Dynamic Task Allocation Algorithm");
        System.out.println("Robot initially set to task: "+ selectedTask);
        System.out.println("Robot initially assigned random threshold values: r: "+ responseThresholdRed + "  b: "+ responseThresholdBlue);

    }

    public void loop() throws Exception {
        super.loop();
//        runTaskAllocationAlgorithm();
        action2Future = executor.submit(() -> {

            try {
//                i = i + 1;
//                System.out.println("Task allocation start: "+i);
                runTaskAllocationAlgorithm();
//                System.out.println("Task allocation end: "+i);
            } catch (SensorException e) {
                throw new RuntimeException(e);
            }

        });

    }

    public void runTaskAllocationAlgorithm() throws SensorException {

        // REAL ALGORITHM STARTS HERE
        if (state == robotState.RUN){

            RGBColorType detectedColor = colorSensor.getColor();

            clusterBehaviours.observe(detectedColor, this.taskDemandQueue, this.taskSupplyQueue, fixedQueueLength, robotId, this.robotMqttClient);

            float[] taskDemands = clusterBehaviours.evaluateTaskDemand(this.taskDemandQueue, fixedQueueLength, robotId);
            this.estimatedTaskDemandForRed = taskDemands[0];
            this.estimatedTaskDemandForBlue = taskDemands[1];

            float[] taskSupplies = clusterBehaviours.evaluateTaskSupply(this.taskSupplyQueue, fixedQueueLength, robotId);
            this.estimatedTaskSupplyForRed = taskSupplies[0];
            this.estimatedTaskSupplyForBlue = taskSupplies[1];


            List<Object> outputs = clusterBehaviours.selectTask(this.responseThresholdRed,this.responseThresholdBlue,scalingFactor,this.estimatedTaskDemandForRed,this.estimatedTaskSupplyForRed
            ,this.estimatedTaskDemandForBlue,this.estimatedTaskSupplyForBlue,n,robotId);

            selectedTask = (String) outputs.get(0);
            this.responseThresholdRedNext = (float) outputs.get(1);
            this.responseThresholdBlueNext = (float) outputs.get(2);
            this.taskSelectionProbabilityRed = (float) outputs.get(3);
            this.taskSelectionProbabilityBlue = (float) outputs.get(4);


            this.timeStep = this.timeStep + 1;
             long endTime = System.currentTimeMillis(); // Record the end time
             long elapsedTime = endTime - startTime; // Calculate the elapsed time in milliseconds
             double[] values = {
                 this.robotId,
                 elapsedTime,
//                     this.timeStep,
                 this.responseThresholdRed,
                 this.responseThresholdBlue,
                 this.estimatedTaskDemandForRed,
                 this.estimatedTaskDemandForBlue,
                 this.estimatedTaskSupplyForRed,
                 this.estimatedTaskSupplyForBlue,
                 this.taskSelectionProbabilityRed,
                 this.taskSelectionProbabilityBlue,
             };

             CsvRecorder.writeRecordToCSV("src/main/java/csvRecorder/record.csv", values, selectedTask);

            this.responseThresholdRed = this.responseThresholdRedNext;
            this.responseThresholdBlue = this.responseThresholdBlueNext;

            atomicBehaviours.showSelectedTask(selectedTask,this.neoPixel,this.simpleComm,robotId);
            delay(2000); // time interval

            
        }
    }
    public void communicationInterrupt(String msg) {
//        System.out.println("communicationInterrupt on " + id + " with msg: " + msg);
        // check if msg is NOT from the robot itself. Msg format is "r 0" - "taskColor sourceRobotID"
        String[] parts = msg.split("\\s+");
        int sourceRobotID = Integer.parseInt(parts[1]);
        if(!(sourceRobotID ==this.robotId)){
            // update supply queue
            clusterBehaviours.addSupply(parts[0],this.taskSupplyQueue,fixedQueueLength);
        }

    }

}
