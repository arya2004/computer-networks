

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    // Port number for the server
    private static final int SERVER_PORT = 6262;
    
    public static void main(String[] args) {
        ServerSocket server = null;
        Socket client = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        
        try {
            // Initialize server socket on specified port
            server = new ServerSocket(SERVER_PORT);
            System.out.println("Server established on port " + SERVER_PORT);

            // Wait for the client to connect
            client = server.accept();
            System.out.println("Client connected.");

            // Setup input and output streams
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());

            // Read initial values sent by the client
            int x = (Integer) ois.readObject();  // Window size
            int k = (Integer) ois.readObject();  // Initial frame number
            int j = 0;                           // Expected frame number
            int i = (Integer) ois.readObject();  // Number of frames to be sent
            boolean flag = true;

            // Create a random generator for simulating error conditions
            Random r = new Random();
            int mod = generateValidMod(r);

            // Main loop to process frames from the client
            while (true) {
                displayFrameWindow(x, k);

                // Frame validation and acknowledgement logic
                if (k == j) {
                    // Correct frame received
                    System.out.println("Frame " + k + " received\nData: " + j);
                    j++;
                } else {
                    // Incorrect frame received
                    System.out.println("Frames received not in correct order\nExpected frame: " + j + "\nReceived frame no: " + k);
                }

                // Simulate error condition based on random mod value
                if (j % mod == 0 && flag) {
                    System.out.println("Error found. Acknowledgement not sent.");
                    flag = !flag;
                    j--;  // Revert frame count to simulate error
                } else if (k == j - 1) {
                    // Send acknowledgement for the correct frame
                    oos.writeObject(k);
                    System.out.println("Acknowledgement sent for frame " + k);
                }

                if (j % mod == 0) {
                    flag = !flag;  // Toggle flag for error simulation
                }

                // Read the next frame number from the client
                k = (Integer) ois.readObject();
                if (k == -1) {
                    // Termination signal from client
                    break;
                }

                // Read the next value of `i` (total frames) - Not used further in the logic
                i = (Integer) ois.readObject();
            }

            System.out.println("Client finished sending data. Exiting...");

            // Send termination signal to the client
            oos.writeObject(-1);

        } catch (IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid object received: " + e.getMessage());
        } finally {
            // Close resources to prevent resource leakage
            closeResources(server, client, oos, ois);
        }
    }

    /**
     * Generates a valid random mod value that is neither 0 nor 1.
     * 
     * @param r Random object
     * @return valid mod value
     */
    private static int generateValidMod(Random r) {
        int mod = r.nextInt(6);
        while (mod == 0 || mod == 1) {
            mod = r.nextInt(6);
        }
        return mod;
    }

    /**
     * Displays the current window of frames based on the Go-Back-N ARQ algorithm.
     *
     * @param windowSize Size of the sliding window
     * @param currentFrame Starting frame number in the window
     */
    private static void displayFrameWindow(int windowSize, int currentFrame) {
        int frameNumber = currentFrame;
        System.out.print("Current window: ");
        for (int h = 0; h < windowSize; h++) {
            System.out.print("|" + frameNumber + "|");
            frameNumber = (frameNumber + 1) % windowSize;
        }
        System.out.println("\n");
    }

    /**
     * Closes all resources safely.
     *
     * @param server The server socket
     * @param client The client socket
     * @param oos    The ObjectOutputStream
     * @param ois    The ObjectInputStream
     */
    private static void closeResources(ServerSocket server, Socket client, ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (client != null) client.close();
            if (server != null) server.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}
