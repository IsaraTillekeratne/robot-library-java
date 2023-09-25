package swarm.ota;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class otaUpdate {

    /*public void downloadBinFile(){
        System.out.println("Launching the application in the command prompt...");
        launchApplication();
    }*/

    public void downloadBinFile() {
        try {

            // Create an array to store the URLs
            String[] endpoints = { "http://localhost:5001/updateAppJava", "http://localhost:5001/updateAlgorithm" };

            // Create an array to store the downloaded file paths
            String[] filePaths = {"src/main/java/swarm/", "src/main/java/Robots/"};

            String[] _filePaths = new String[2];


            for (int i = 0; i < 2; i++) {
                // Create a URL object for the current endpoint
                URL url = new URL(endpoints[i]);

                // Open a connection to the URL
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set the request method to GET
                connection.setRequestMethod("GET");

                // Get the input stream from the connection
                InputStream inputStream = connection.getInputStream();

                // Get the file name from the content disposition header
                String contentDisposition = connection.getHeaderField("Content-Disposition");
                String fileName = "downloaded" + i + ".jar"; // Default name if header is not present

                if (contentDisposition != null && contentDisposition.contains("filename=")) {
                    int index = contentDisposition.indexOf("filename=");
                    fileName = contentDisposition.substring(index + 9); // 9 is the length of "filename="
                }

                String filePath = filePaths[i] + fileName.replace("\"", "");

                System.out.println("filePath:"+filePath);

                // Create a File object for the target file
                File jarFile = new File(filePath);

                // Create parent directories if they don't exist
                if (!jarFile.getParentFile().exists()) {
                    jarFile.getParentFile().mkdirs();
                }

                // Create a FileOutputStream to write the .jar file
                FileOutputStream outputStream = new FileOutputStream(filePath);

                // Read data from the input stream and write it to the output stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Close the output stream and input stream
                outputStream.close();
                inputStream.close();

                // Store the file path in the array
                _filePaths[i] = filePath;

                // Print a success message for each file
                System.out.println("Downloaded file successfully to: " + filePath);
            }

            // If both downloads were successful, then launch the application
            if (_filePaths[0] != null && _filePaths[1] != null) {
                launchApplication();
            }
            // }
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

    private static void launchApplication() {
        try {
            String[] command = {"cmd", "/c", "start", "cmd.exe", "/K",
                    "java -cp java-robot-1.0.2.jar @C:\\Users\\USER\\AppData\\Local\\Temp\\" + findArgFileName() + " swarm.App"};
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

    private static String findArgFileName(){
        String directoryPath = "C:/Users/USER/AppData/Local/Temp/";

        // Specify the target file extension you want to find
        String targetFileExtension = ".argfile";

        String fileName = "";

        try {
            // Create a Path object for the directory
            Path directory = Paths.get(directoryPath);

            // Initialize a variable to store the filename
            String firstMatchingFileName = null;

            // Create a DirectoryStream to list files with the target extension
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*" + targetFileExtension)) {
                for (Path entry : stream) {
                    firstMatchingFileName = entry.getFileName().toString();
                    break; // Stop searching after the first matching file is found
                }
            }

            // Print the name of the first matching file
            if (firstMatchingFileName != null) {
                System.out.println("First matching file: " + firstMatchingFileName);
                fileName = firstMatchingFileName;
            } else {
                System.out.println("No matching file found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

}

