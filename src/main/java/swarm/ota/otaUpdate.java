package swarm.ota;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class otaUpdate {

    /*public void downloadBinFile(){
        System.out.println("Launching the application in the command prompt...");
        launchApplication();
    }*/
 
    public void downloadBinFile() {
        try {
            String updateJarEndpoint = "http://localhost:5001/updateJar"; 

            // Create a URL object
            URL url = new URL(updateJarEndpoint);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            connection.setRequestMethod("GET");

            // Get the input stream from the connection
            InputStream inputStream = connection.getInputStream();

            String jarFilePath = "src/main/java/swarm/App.java";
            // String jarFilePath = "recent_builds/java-robot-1.0.2.jar";

            // Create a File object for the target file
            File jarFile = new File(jarFilePath);

            // Create parent directories if they don't exist
        if (!jarFile.getParentFile().exists()) {
            jarFile.getParentFile().mkdirs();
        } 

            // Create a FileOutputStream to write the .jar file
            FileOutputStream outputStream = new FileOutputStream(jarFilePath);

            // Read data from the input stream and write it to the output stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close the output stream and input stream
            outputStream.close();
            inputStream.close();

            // Print a success message
            System.out.println("Downloaded .jar file successfully to: " + jarFilePath);

            // Launch the application
            launchApplication(jarFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to download .jar file.");
        }
    }

    private static void shutdownApplication() {
        // Perform cleanup, save state, close resources, etc.
        System.out.println("Shutting down the currently running application.");
        System.exit(0);
    }
    
    private static void launchApplication(String jarFilePath) {
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


