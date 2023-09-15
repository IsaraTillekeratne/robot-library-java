package csvRecorder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CsvRecorder {

    public static void writeRecordToCSV(String filePath, double[] values, String selectedTask) {    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            // Join measurements with commas and write them as a row in the CSV file
            String row = joinArrayWithCommas(values);
            row += "," + selectedTask;
            writer.write(row + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String joinArrayWithCommas(double[] array) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            result.append(array[i]);
            if (i < array.length - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    // Write an empty row
    public static void addEmptyRowToCSV(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write("\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void recordInitialThresholdValues(String filePath, int robotID, float redThreshold, float blueThreshold){

        String[] columnNames = {"RobotID", "RedThreshold", "BlueThreshold"};

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(String.join(",", columnNames) + "\n");
            writer.write(robotID + "," + redThreshold + "," + blueThreshold + "\n");
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
