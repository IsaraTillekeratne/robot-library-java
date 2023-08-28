package Robots;

import swarm.behaviours.atomicBehaviours.AtomicBehaviours;
import swarm.behaviours.clusterBehaviours.ClusterBehaviours;
import swarm.robot.exception.SensorException;
import swarm.robot.types.RGBColorType;

import java.util.*;

public class DynamicTaskAllocationRobot extends ObstacleAvoidanceRobot{

    ClusterBehaviours clusterBehaviours = new ClusterBehaviours();
    AtomicBehaviours atomicBehaviours = new AtomicBehaviours();
    Queue<String> taskDemandQueue = new LinkedList<>();
    Queue<String> taskSupplyQueue = new LinkedList<>();
    static int fixedQueueLength = 20;
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

    public DynamicTaskAllocationRobot(int id, double x, double y, double heading) {
        super(id, x, y, heading);
    }

    public void setup() {

        super.setup();

        // initially assign task Red
        selectedTask = "r";
//        showSelectedTask();
        atomicBehaviours.showSelectedTask(selectedTask,this.neoPixel,this.simpleComm,this.getId());

        // assign initial random thresholds
        Random rand = new Random();

        this.responseThresholdRed = rand.nextFloat();
        this.responseThresholdBlue = rand.nextFloat();

        System.out.println("Robot "+this.id+" is running Dynamic Task Allocation Algorithm");
        System.out.println("Robot initially set to task: "+ selectedTask);
        System.out.println("Robot initially assigned random threshold values: r: "+ responseThresholdRed + "  b: "+ responseThresholdBlue);
    }

    public void loop() throws Exception {
        super.loop();
        runTaskSelectionAlgorithm();
    }

    public void runTaskSelectionAlgorithm() throws SensorException {

        // REAL ALGORITHM STARTS HERE
        if (state == robotState.RUN){

//            observe();
            RGBColorType detectedColor = colorSensor.getColor();

//            System.out.print("Robot: "+this.getId()+" | task demand before observe: ");
//            printQueue(taskDemandQueue);

            clusterBehaviours.observe(detectedColor, this.taskDemandQueue, this.taskSupplyQueue, fixedQueueLength, this.getId(), this.robotMqttClient);

//            System.out.print("Robot: "+this.getId()+" | task demand after observe: ");
//            printQueue(taskDemandQueue);

//            evaluateTaskDemand();
//            System.out.println("Robot: "+this.getId()+" "+"Task demand for blue before: "+ estimatedTaskDemandForRed);
            float[] taskDemands = clusterBehaviours.evaluateTaskDemand(this.taskDemandQueue, fixedQueueLength, this.getId());
            this.estimatedTaskDemandForRed = taskDemands[0];
            this.estimatedTaskDemandForBlue = taskDemands[1];
//            System.out.println("Robot: "+this.getId()+" "+"Task demand for blue after: "+ estimatedTaskDemandForRed);
//            evaluateTaskSupply();
            float[] taskSupplies = clusterBehaviours.evaluateTaskSupply(this.taskSupplyQueue, fixedQueueLength, this.getId());
            this.estimatedTaskSupplyForRed = taskSupplies[0];
            this.estimatedTaskSupplyForBlue = taskSupplies[1];

//            System.out.println("Robot: "+this.getId()+" Selected task: "+selectedTask+" Current Red Threshold: "
//                    +this.responseThresholdRed+" Current Blue Threshold: "+this.responseThresholdBlue
//                    +" Red Probability: "+this.taskSelectionProbabilityRed+" Blue Probability: "+this.taskSelectionProbabilityBlue);

            // selectTask();
            List<Object> outputs = clusterBehaviours.selectTask(this.responseThresholdRed,this.responseThresholdBlue,scalingFactor,this.estimatedTaskDemandForRed,this.estimatedTaskSupplyForRed
            ,this.estimatedTaskDemandForBlue,this.estimatedTaskSupplyForBlue,n,this.getId());

            selectedTask = (String) outputs.get(0);
            this.responseThresholdRedNext = (float) outputs.get(1);
            this.responseThresholdBlueNext = (float) outputs.get(2);
            this.taskSelectionProbabilityRed = (float) outputs.get(3);
            this.taskSelectionProbabilityBlue = (float) outputs.get(4);

//            System.out.println("Robot: "+this.getId()+" Selected task: "+selectedTask+" Next Red Threshold: "
//            +this.responseThresholdRedNext+" Next Blue Threshold: "+this.responseThresholdBlueNext
//            +" Red Probability: "+this.taskSelectionProbabilityRed+" Blue Probability: "+this.taskSelectionProbabilityBlue);

            this.responseThresholdRed = this.responseThresholdRedNext;
            this.responseThresholdBlue = this.responseThresholdBlueNext;

//            showSelectedTask();
            atomicBehaviours.showSelectedTask(selectedTask,this.neoPixel,this.simpleComm,this.getId());
            delay(2000); // time interval
        }
    }

}
