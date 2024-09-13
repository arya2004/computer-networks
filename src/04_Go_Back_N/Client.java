
import java.util.*;
import java.net.*;
import java.io.*;

public class Client {
    private static final int SERVER_PORT = 6262;
    private static final String SERVER_HOST = "localhost";
    
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Socket client = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        try {
            // Get the value of m from the user
            System.out.print("Enter the value of m: ");
            int m = Integer.parseInt(br.readLine());
            int windowSize = (int) (Math.pow(2, m) - 1);  // Max frame number (2^m - 1)

            // Get the number of frames to send
            System.out.print("Enter the number of frames to be sent: ");
            int frameCount = Integer.parseInt(br.readLine());

            // Store the data for each frame
            int[] data = new int[frameCount];
            int h = 0;
            for (int i = 0; i < frameCount; i++) {
                System.out.print("Enter data for frame no " + h + " => ");
                data[i] = Integer.parseInt(br.readLine());
                h = (h + 1) % windowSize;
            }

            // Establish a connection to the server
            client = new Socket(SERVER_HOST, SERVER_PORT);
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());

            System.out.println("Connected to the server.");

            boolean transmissionComplete = false;

            // Start a listener thread for receiving acknowledgements
            GoBackNListener listener = new GoBackNListener(ois, windowSize);
            listener.startListening();

            int startFrame = 0;
            h = 0;

            // Send window size (x) to the server
            oos.writeObject(windowSize);

            // Main transmission loop
            do {
                int frame = h;

                // Display frame window
                displayFrameWindow(windowSize, frame, frameCount);

                h = startFrame;

                // Send frames to the server
                for (int i = startFrame; i < windowSize; i++) {
                    if (i >= frameCount) break;  // Stop if all frames are sent
                    System.out.println("Sending frame: " + h);
                    oos.writeObject(i);           // Send frame number
                    oos.writeObject(data[i]);     // Send frame data
                    h = (h + 1) % windowSize;
                    Thread.sleep(100);            // Simulate network delay
                }

                // Wait for an acknowledgment or timeout
                listener.getListenerThread().join(3500);

                // Handle retransmission if no acknowledgment is received
                if (listener.getLastAcknowledgedFrame() != windowSize - 1) {
                    System.out.println("No reply from server in 3.5 seconds. Resending from frame no " + (listener.getLastAcknowledgedFrame() + 1));
                    startFrame = listener.getLastAcknowledgedFrame() + 1;
                } else {
                    System.out.println("All frames sent successfully. Exiting.");
                    transmissionComplete = true;
                }
            } while (!transmissionComplete);

            // Notify the server of termination
            oos.writeObject(-1);

        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid input. Please enter a valid number.");
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        } finally {
            // Close resources safely
            closeResources(client, oos, ois);
        }
    }

    /**
     * Displays the current window of frames being transmitted.
     *
     * @param windowSize The size of the window (number of frames)
     * @param currentFrame The starting frame number
     * @param frameCount The total number of frames to be sent
     */
    private static void displayFrameWindow(int windowSize, int currentFrame, int frameCount) {
        int frame = currentFrame;
        System.out.print("Current window: ");
        for (int i = currentFrame; i < frameCount && i < windowSize; i++) {
            System.out.print("|" + frame + "|");
            frame = (frame + 1) % windowSize;
        }
        System.out.println("\n");
    }

    /**
     * Closes client resources such as sockets and streams.
     *
     * @param client The client socket
     * @param oos The ObjectOutputStream
     * @param ois The ObjectInputStream
     */
    private static void closeResources(Socket client, ObjectOutputStream oos, ObjectInputStream ois) {
        try {
            if (oos != null) oos.close();
            if (ois != null) ois.close();
            if (client != null) client.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}

/**
 * GoBackNListener is responsible for listening to acknowledgments from the server
 * and updating the last acknowledged frame.
 */
class GoBackNListener implements Runnable {
    private Thread listenerThread;
    private ObjectInputStream ois;
    private int lastAcknowledgedFrame;
    private int windowSize;

    GoBackNListener(ObjectInputStream ois, int windowSize) {
        this.ois = ois;
        this.windowSize = windowSize;
        this.lastAcknowledgedFrame = -2;  // Initialize to an invalid state
    }

    /**
     * Starts the listener thread to listen for acknowledgments.
     */
    public void startListening() {
        listenerThread = new Thread(this);
        listenerThread.start();
    }

    /**
     * Returns the listener thread object.
     *
     * @return The thread running the listener
     */
    public Thread getListenerThread() {
        return listenerThread;
    }

    /**
     * Returns the last acknowledged frame from the server.
     *
     * @return The last acknowledged frame number
     */
    public int getLastAcknowledgedFrame() {
        return lastAcknowledgedFrame;
    }

    /**
     * Main method for the listener thread that listens for acknowledgments.
     */
    @Override
    public void run() {
        try {
            int temp = 0;
            while (lastAcknowledgedFrame != -1) {
                lastAcknowledgedFrame = (Integer) ois.readObject();
                if (lastAcknowledgedFrame != -1 && lastAcknowledgedFrame != temp + 1) {
                    lastAcknowledgedFrame = temp;
                }
                if (lastAcknowledgedFrame != -1) {
                    temp = lastAcknowledgedFrame;
                    System.out.println("Acknowledgment for frame " + (lastAcknowledgedFrame % windowSize) + " received.");
                }
            }
            lastAcknowledgedFrame = temp;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error in listener: " + e.getMessage());
        }
    }
}
