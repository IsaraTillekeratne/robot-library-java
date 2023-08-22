package swarm.robot.types;

import swarm.robot.exception.ProximityException;

public class ProximityReadingType {
    int[] readings = new int[5];

    public ProximityReadingType(String str) throws ProximityException {
        String[] distance = str.split(" ");

        if (distance.length != 5) {
            throw new ProximityException("length != 5");
        }

        for (int i = 0; i < 5; i++) {
            if (distance[1].compareTo("Infinity") == 0) {
                // -1 will be returned as a fail-proof option. Should throw an exception
                System.out.println("Proximity: Infinity reading received for " + i);
                readings[i] = -1;
            } else {
                readings[i] = Integer.parseInt(distance[i]);
            }
        }
    }

    public ProximityReadingType(int LB, int L, int F, int R, int RB) {
        readings[0] = LB;
        readings[1] = L;
        readings[2] = F;
        readings[3] = R;
        readings[4] = RB;
    }

    public String toString() {
        return readings[0] + " " + readings[1] + " " + readings[2] + " " + readings[3] + " " + readings[4];
    }

    public int[] getReadings() {
        return readings;
    }
}
