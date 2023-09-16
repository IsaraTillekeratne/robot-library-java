package swarm.behaviours.clusterBehaviours;

import swarm.mqtt.MqttMsg;
import swarm.mqtt.RobotMqttClient;
import swarm.robot.exception.SensorException;
import swarm.robot.types.RGBColorType;

import java.util.*;

import static swarm.behaviours.clusterBehaviours.Helpers.printQueue;

public class ClusterBehaviours {

    public void addDemand(String colourOfObject, Queue<String> taskDemandQueue, int fixedQueueLength){

        int lengthOfDemandQueue = taskDemandQueue.size();
        if(lengthOfDemandQueue<fixedQueueLength){
            taskDemandQueue.add(colourOfObject);
        }else{
            taskDemandQueue.remove();
            taskDemandQueue.add(colourOfObject);
        }

    }

    public void addSupply(String colourOfRobot, Queue<String> taskSupplyQueue, int fixedQueueLength){

        int lengthOfSupplyQueue = taskSupplyQueue.size();
        if(lengthOfSupplyQueue<fixedQueueLength){
            taskSupplyQueue.add(colourOfRobot);
        }else{
            taskSupplyQueue.remove();
            taskSupplyQueue.add(colourOfRobot);
        }

    }

    // observe method will populate the taskDemand and taskSupply queues
    public void observe(RGBColorType detectedColor, Queue<String> taskDemandQueue, Queue<String> taskSupplyQueue, int fixedQueueLength, int robotID, RobotMqttClient robotMqttClient) throws SensorException { // Cluster Behaviour

        System.out.println();

        // OBSERVE TASK DEMAND

        // if red detected, populate demand queue with "r", if blue detected with "b"
        if(detectedColor.compareTo(new RGBColorType(255,0,0))){
            addDemand("r", taskDemandQueue, fixedQueueLength);
            System.out.println("Robot: "+robotID+" "+detectedColor+ " Object detected. Demand queue updated.");
        } else if (detectedColor.compareTo(new RGBColorType(0,0,255))) {
            addDemand("b", taskDemandQueue, fixedQueueLength);
            System.out.println("Robot: "+robotID+" "+detectedColor+ " Object detected. Demand queue updated.");
        }

        // print task demand queue
        System.out.print("Robot: "+robotID+" "+"Task Demand Queue: ");
        printQueue(taskDemandQueue);

        // OBSERVE TASK SUPPLY - handled in communicationInterrupt method
//        if(!robotMqttClient.inQueue.isEmpty()){
//            MqttMsg m = robotMqttClient.inQueue();
//            if(Objects.equals(m.message, "r")){
//                addSupply("r", taskSupplyQueue, fixedQueueLength);
//                System.out.println("Robot: "+robotID+" "+"Received: "+ m.message+" "+m.topic);
//            } else if (Objects.equals(m.message, "b")) {
//                addSupply("b", taskSupplyQueue, fixedQueueLength);
//                System.out.println("Robot: "+robotID+" "+"Received: "+ m.message+" "+m.topic);
//            }
//
//        }

        // print task supply queue
        System.out.print("Robot: "+robotID+" "+"Task Supply Queue: ");
        printQueue(taskSupplyQueue);
    }

    public float[] evaluateTaskDemand(Queue<String> taskDemandQueue, int fixedQueueLength, int robotID){ // Cluster Behaviour

        float[] taskDemands = new float[2];

        if(!taskDemandQueue.isEmpty()){

            // Red Task
            float estimatedTaskDemandForRed = (float) (Collections.frequency(taskDemandQueue, "r")) / (fixedQueueLength);
            taskDemands[0] = estimatedTaskDemandForRed;
            System.out.println("Robot: "+robotID+" "+"Task demand for red calculated: "+ estimatedTaskDemandForRed);
            // Blue Task
            float estimatedTaskDemandForBlue = (float) (Collections.frequency(taskDemandQueue, "b")) / (fixedQueueLength);
            taskDemands[1] = estimatedTaskDemandForBlue;
            System.out.println("Robot: "+robotID+" "+"Task demand for blue calculated: "+ estimatedTaskDemandForBlue);
        }
        return taskDemands;

    }

    public float[] evaluateTaskSupply(Queue<String> taskSupplyQueue, int fixedQueueLength, int robotID){ // Cluster Behaviour

        float[] taskSupplies = new float[2];
        if(!taskSupplyQueue.isEmpty()){

            // Red Task
            float estimatedTaskSupplyForRed = (float) (Collections.frequency(taskSupplyQueue, "r")) /(fixedQueueLength);
            taskSupplies[0] = estimatedTaskSupplyForRed;
            System.out.println("Robot: "+robotID+" "+"Task supply for red calculated: "+ estimatedTaskSupplyForRed);
            // Blue Task
            float estimatedTaskSupplyForBlue = (float) (Collections.frequency(taskSupplyQueue, "b")) /(fixedQueueLength);
            taskSupplies[1] = estimatedTaskSupplyForBlue;
            System.out.println("Robot: "+robotID+" "+"Task supply for blue calculated: "+ estimatedTaskSupplyForBlue);
        }
        return taskSupplies;

    }

    public List<Object> selectTask(float responseThresholdRed, float responseThresholdBlue, float scalingFactor, float estimatedTaskDemandForRed, float estimatedTaskSupplyForRed, float estimatedTaskDemandForBlue, float estimatedTaskSupplyForBlue, int n, int robotID, String currentTask){ // Cluster Behaviour

        List<Object> list = new ArrayList<>();

        // calculate new response threshold for Red
        float responseThresholdRedNext = (responseThresholdRed - (scalingFactor * (estimatedTaskDemandForRed - estimatedTaskSupplyForRed)));
        System.out.println("Robot: "+robotID+" "+"Next Response threshold for red calculated: "+ responseThresholdRedNext);
        // calculate new response threshold for Blue
        float responseThresholdBlueNext = (responseThresholdBlue - (scalingFactor * (estimatedTaskDemandForBlue - estimatedTaskSupplyForBlue)));
        System.out.println("Robot: "+robotID+" "+"Next Response threshold for blue calculated: "+ responseThresholdBlueNext);

        // calculate task selection probability for Red
        float taskSelectionProbabilityRed = (float) ((Math.pow(estimatedTaskDemandForRed, n))/(Math.pow(estimatedTaskDemandForRed, n) + Math.pow(responseThresholdRed, n)));
        System.out.println("Robot: "+robotID+" "+"Task selection probability for red calculated: "+ taskSelectionProbabilityRed);

        // calculate task selection probability for Blue
        float taskSelectionProbabilityBlue = (float) ((Math.pow(estimatedTaskDemandForBlue, n))/(Math.pow(estimatedTaskDemandForBlue, n) + Math.pow(responseThresholdBlue, n)));
        System.out.println("Robot: "+robotID+" "+"Task selection probability for blue calculated: "+ taskSelectionProbabilityBlue);

        String selectedTask;
        if(taskSelectionProbabilityRed < taskSelectionProbabilityBlue){
            selectedTask = "b";
            list.add(selectedTask);
        }else if(taskSelectionProbabilityBlue < taskSelectionProbabilityRed){ // even if equal, assign red | CHECK
            selectedTask = "r";
            list.add(selectedTask);
        }else{
            selectedTask = currentTask;
            list.add(selectedTask);
        }

        list.add(responseThresholdRedNext);
        list.add(responseThresholdBlueNext);
        list.add(taskSelectionProbabilityRed);
        list.add(taskSelectionProbabilityBlue);

        return list;
    }




}
