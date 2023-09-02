package swarm.ota;

import java.io.*;

public class otaUpdate {

    public void downloadBinFile(){
        System.out.println("Launching the application in the command prompt...");
        launchApplication();
    }

    private static void shutdownApplication() {
        // Perform cleanup, save state, close resources, etc.
        System.out.println("Shutting down the currently running application.");
        System.exit(0);
    }
    
    private static void launchApplication() {
        try {
            String[] command = {"cmd", "/c", "start", "cmd.exe", "/K", "java -cp java-robot-1.0.2.jar @C:\\Users\\USER\\AppData\\Local\\Temp\\cp_39mik0g6gb00qcc5u259cwthi.argfile swarm.App"};
            Process process = new ProcessBuilder(command).inheritIO().start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.err.println("Application launch failed with exit code: " + exitCode);
            }
            else{
                System.err.println("Application launched successfully in the command prompt.");
                shutdownApplication();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}


