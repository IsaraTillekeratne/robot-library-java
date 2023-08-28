package swarm.behaviours.clusterBehaviours;

import java.util.Queue;

public class Helpers {
    public static void printQueue(Queue<String> queue){
        for (String item: queue) {
            System.out.print(item + " ");
        }
        System.out.println();
    }
}
