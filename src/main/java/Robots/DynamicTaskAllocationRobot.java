package Robots;

import swarm.robot.exception.SensorException;
import swarm.robot.types.RGBColorType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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
    }

    public void runTaskSelectionAlgorithm() throws SensorException {
        // dummy algorithm
//        if(colorSensor.getColor().compareTo(new RGBColorType(255,0,0))){
//            neoPixel.changeColor(255,0,0);
//        } else if (colorSensor.getColor().compareTo(new RGBColorType(0,0,255))) {
//            neoPixel.changeColor(0,0,255);
//        }

        // real algorithm starts here
        observe();
        evaluateTaskDemand();
        evaluateTaskSupply();
        selectTask();
        delay(2000);
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
    public void observe() throws SensorException {

        System.out.println();

        RGBColorType detectedColor = colorSensor.getColor();

        // if red detected, populate demand queue with "r", if blue detected with "b"
        if(detectedColor.compareTo(new RGBColorType(255,0,0))){
            addDemand("r");
            System.out.println(detectedColor+ " Object detected. Demand queue updated.");
        } else if (detectedColor.compareTo(new RGBColorType(0,0,255))) {
            addDemand("b");
            System.out.println(detectedColor+ " Object detected. Demand queue updated.");
        }

        // print task demand queue
        System.out.print("Task Demand Queue: ");
        printQueue(taskDemandQueue);

        // CHANGE
        Random random1 = new Random();
        Random random2 = new Random();
        String[] colour = {"r", "b"};

        // if a neighbouring robot sends task detail - code
        if(random1.nextBoolean()){
            // String colourOfNeighbourRobot = "r"; // hardcoded
            String colourOfNeighbourRobot = colour[random2.nextInt(2)]; // randomly choose r or b
            addSupply(colourOfNeighbourRobot);

            System.out.println(colourOfNeighbourRobot+ " Robot detected. Supply queue updated as below.");
            System.out.print("Task Supply Queue: ");
            for (String item: taskSupplyQueue) {
                System.out.print(item + " ");
            }
            System.out.println();
        }
    }

    public void printQueue(Queue<String> queue){
        for (String item: queue) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    public void evaluateTaskDemand(){

        if(!taskDemandQueue.isEmpty()){

            // Red Task
            estimatedTaskDemandForRed = (float) (Collections.frequency(taskDemandQueue, "r")) /(fixedQueueLength);
            System.out.println("Task demand for red calculated: "+ estimatedTaskDemandForRed);
            // Blue Task
            estimatedTaskDemandForBlue = (float) (Collections.frequency(taskDemandQueue, "b")) /(fixedQueueLength);
            System.out.println("Task demand for blue calculated: "+ estimatedTaskDemandForBlue);
        }

    }

    public void evaluateTaskSupply(){

        if(!taskSupplyQueue.isEmpty()){

            // Red Task
            estimatedTaskSupplyForRed = (float) (Collections.frequency(taskSupplyQueue, "r")) /(fixedQueueLength);
            System.out.println("Task supply for red calculated: "+ estimatedTaskSupplyForRed);
            // Blue Task
            estimatedTaskSupplyForBlue = (float) (Collections.frequency(taskSupplyQueue, "b")) /(fixedQueueLength);
            System.out.println("Task supply for blue calculated: "+ estimatedTaskSupplyForBlue);
        }

    }

    public void selectTask(){

        // calculate new response threshold for Red
        responseThresholdRedNext = (responseThresholdRed - (scalingFactor * (estimatedTaskDemandForRed - estimatedTaskSupplyForRed)));
        System.out.println("Next Response threshold for red calculated: "+ responseThresholdRed);
        // calculate new response threshold for Blue
        responseThresholdBlueNext = (responseThresholdBlue - (scalingFactor * (estimatedTaskDemandForBlue - estimatedTaskSupplyForBlue)));
        System.out.println("Next Response threshold for blue calculated: "+ responseThresholdBlue);

        // calculate task selection probability for Red
        taskSelectionProbabilityRed = (float) ((Math.pow(estimatedTaskDemandForRed, n))/(Math.pow(estimatedTaskDemandForRed, n) + Math.pow(responseThresholdRed, n)));
        System.out.println("Task selection probability for red calculated: "+ taskSelectionProbabilityRed);

        // calculate task selection probability for Blue
        taskSelectionProbabilityBlue = (float) ((Math.pow(estimatedTaskDemandForBlue, n))/(Math.pow(estimatedTaskDemandForBlue, n) + Math.pow(responseThresholdBlue, n)));
        System.out.println("Task selection probability for blue calculated: "+ taskSelectionProbabilityBlue);

        if(taskSelectionProbabilityRed < taskSelectionProbabilityBlue){
            selectedTask = "b";
        }else if(taskSelectionProbabilityBlue < taskSelectionProbabilityRed){
            selectedTask = "r";
        }

        showSelectedTask();

        // update response thresholds for next iteration
        responseThresholdRed = responseThresholdRedNext;
        responseThresholdBlue = responseThresholdBlueNext;

        System.out.println("Robot selected task: "+ selectedTask);
        System.out.println();
    }

    public void showSelectedTask(){
        if(selectedTask == "r"){
            // robot show color white (representing task red)
            neoPixel.changeColor(255,255,255);
        }else if(selectedTask == "b"){
            // robot show color green (representing task blue)
            neoPixel.changeColor(0,255,0);
        }
    }

}
