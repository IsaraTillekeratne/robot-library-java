package Robots;

import swarm.mqtt.MqttMsg;
import swarm.robot.exception.SensorException;
import swarm.robot.types.RGBColorType;

import java.util.*;

public class DynamicTaskAllocationRobot extends ObstacleAvoidanceRobot{

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
        showSelectedTask();

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
//        executor.execute(() -> {
//            try {
//                runTaskSelectionAlgorithm(); // Execute action2 in a separate thread
////                System.out.println("Dynamic task! Robot: " +this.getId()+" Thread name:  "+
////                        Thread.currentThread().getName());
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
    }

    public void runTaskSelectionAlgorithm() throws SensorException {

        // REAL ALGORITHM STARTS HERE
        if (state == robotState.RUN){

            observe();
            evaluateTaskDemand();
            evaluateTaskSupply();
            selectTask();
            delay(2000); // time interval
        }
    }

    public void addDemand(String colourOfObject){

        int lengthOfDemandQueue = taskDemandQueue.size();
        if(lengthOfDemandQueue<fixedQueueLength){
            taskDemandQueue.add(colourOfObject);
        }else{
            taskDemandQueue.remove();
            taskDemandQueue.add(colourOfObject);
        }

    }

    public void addSupply(String colourOfRobot){

        int lengthOfSupplyQueue = taskSupplyQueue.size();
        if(lengthOfSupplyQueue<fixedQueueLength){
            taskSupplyQueue.add(colourOfRobot);
        }else{
            taskSupplyQueue.remove();
            taskSupplyQueue.add(colourOfRobot);
        }

    }

    // observe method will populate the taskDemand and taskSupply queues
    public void observe() throws SensorException { // Cluster Behaviour

        System.out.println();

        // OBSERVE TASK DEMAND
        RGBColorType detectedColor = colorSensor.getColor();

        // if red detected, populate demand queue with "r", if blue detected with "b"
        if(detectedColor.compareTo(new RGBColorType(255,0,0))){
            addDemand("r");
//            System.out.println("Robot: "+this.getId()+" "+detectedColor+ " Object detected. Demand queue updated.");
        } else if (detectedColor.compareTo(new RGBColorType(0,0,255))) {
            addDemand("b");
//            System.out.println("Robot: "+this.getId()+" "+detectedColor+ " Object detected. Demand queue updated.");
        }

        // print task demand queue
        System.out.print("Robot: "+this.getId()+" "+"Task Demand Queue: ");
        printQueue(taskDemandQueue);

        // OBSERVE TASK SUPPLY
        if(!robotMqttClient.inQueue.isEmpty()){
            MqttMsg m = robotMqttClient.inQueue();
            if(Objects.equals(m.message, "r")){
                addSupply("r");
//                System.out.println("Robot: "+this.getId()+" "+"Received: "+ m.message+" "+m.topic);
            } else if (Objects.equals(m.message, "b")) {
                addSupply("b");
//                System.out.println("Robot: "+this.getId()+" "+"Received: "+ m.message+" "+m.topic);
            }

        }

        // print task supply queue
        System.out.print("Robot: "+this.getId()+" "+"Task Supply Queue: ");
        printQueue(taskSupplyQueue);
    }

    public void printQueue(Queue<String> queue){
        for (String item: queue) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    public void evaluateTaskDemand(){ // Cluster Behaviour

        if(!taskDemandQueue.isEmpty()){

            // Red Task
            estimatedTaskDemandForRed = (float) (Collections.frequency(taskDemandQueue, "r")) /(fixedQueueLength);
            System.out.println("Robot: "+this.getId()+" "+"Task demand for red calculated: "+ estimatedTaskDemandForRed);
            // Blue Task
            estimatedTaskDemandForBlue = (float) (Collections.frequency(taskDemandQueue, "b")) /(fixedQueueLength);
            System.out.println("Robot: "+this.getId()+" "+"Task demand for blue calculated: "+ estimatedTaskDemandForBlue);
        }

    }

    public void evaluateTaskSupply(){ // Cluster Behaviour

        if(!taskSupplyQueue.isEmpty()){

            // Red Task
            estimatedTaskSupplyForRed = (float) (Collections.frequency(taskSupplyQueue, "r")) /(fixedQueueLength);
            System.out.println("Robot: "+this.getId()+" "+"Task supply for red calculated: "+ estimatedTaskSupplyForRed);
            // Blue Task
            estimatedTaskSupplyForBlue = (float) (Collections.frequency(taskSupplyQueue, "b")) /(fixedQueueLength);
            System.out.println("Robot: "+this.getId()+" "+"Task supply for blue calculated: "+ estimatedTaskSupplyForBlue);
        }

    }

    public void selectTask(){ // Cluster Behaviour

        // calculate new response threshold for Red
        responseThresholdRedNext = (responseThresholdRed - (scalingFactor * (estimatedTaskDemandForRed - estimatedTaskSupplyForRed)));
        System.out.println("Robot: "+this.getId()+" "+"Next Response threshold for red calculated: "+ responseThresholdRed);
        // calculate new response threshold for Blue
        responseThresholdBlueNext = (responseThresholdBlue - (scalingFactor * (estimatedTaskDemandForBlue - estimatedTaskSupplyForBlue)));
        System.out.println("Robot: "+this.getId()+" "+"Next Response threshold for blue calculated: "+ responseThresholdBlue);

        // calculate task selection probability for Red
        taskSelectionProbabilityRed = (float) ((Math.pow(estimatedTaskDemandForRed, n))/(Math.pow(estimatedTaskDemandForRed, n) + Math.pow(responseThresholdRed, n)));
        System.out.println("Robot: "+this.getId()+" "+"Task selection probability for red calculated: "+ taskSelectionProbabilityRed);

        // calculate task selection probability for Blue
        taskSelectionProbabilityBlue = (float) ((Math.pow(estimatedTaskDemandForBlue, n))/(Math.pow(estimatedTaskDemandForBlue, n) + Math.pow(responseThresholdBlue, n)));
        System.out.println("Robot: "+this.getId()+" "+"Task selection probability for blue calculated: "+ taskSelectionProbabilityBlue);

        if(taskSelectionProbabilityRed < taskSelectionProbabilityBlue){
            selectedTask = "b";
        }else if(taskSelectionProbabilityBlue < taskSelectionProbabilityRed){
            selectedTask = "r";
        }

        showSelectedTask();

        // update response thresholds for next iteration
        responseThresholdRed = responseThresholdRedNext;
        responseThresholdBlue = responseThresholdBlueNext;

        System.out.println("Robot: "+this.getId()+" "+"selected task: "+ selectedTask);
        System.out.println();
    }

    public void showSelectedTask(){ // Atomic Behaviour
        if(selectedTask == "r"){
            // robot show color white (representing task red)
            neoPixel.changeColor(255,255,255);
            // robot sends r msg to neighbouring robots within 50 radius
            simpleComm.sendMessage("r",50);
        }else if(selectedTask == "b"){
            // robot show color green (representing task blue)
            neoPixel.changeColor(0,255,0);
            // robot sends b msg to neighbouring robots within 50 radius
            simpleComm.sendMessage("b",50);
        }
    }

}
