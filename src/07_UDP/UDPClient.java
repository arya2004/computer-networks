

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * UDP Client that sends messages to a UDP Server running on the same machine.
 * The client continues to send messages until the user inputs "bye".
 */
public class UDPClient {

    private static final int SERVER_PORT = 1234; // The port on which the server is listening
    private static final String SERVER_ADDRESS = "localhost"; // Server IP address (localhost for testing)

    public static void main(String[] args) {
        DatagramSocket socket = null; // Socket to send data
        Scanner scanner = new Scanner(System.in); // Scanner to read user input

        try {
            // Step 1: Create a DatagramSocket to send the data
            socket = new DatagramSocket();

            // Get the IP address of the server (in this case, localhost)
            InetAddress ip = InetAddress.getByName(SERVER_ADDRESS);

            System.out.println("Client started. Type messages to send to the server (type 'bye' to exit):");

            byte[] buffer; // Buffer to store message in bytes

            // Continuously send messages until the user types "bye"
            while (true) {
                // Get the user input
                String message = scanner.nextLine();

                // Convert the message to bytes
                buffer = message.getBytes();

                // Step 2: Create the DatagramPacket to send the data
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, ip, SERVER_PORT);

                // Step 3: Send the packet
                socket.send(packet);

                // Exit the loop if the user types "bye"
                if (message.equalsIgnoreCase("bye")) {
                    System.out.println("Client exiting...");
                    break;
                }
            }

        } catch (UnknownHostException e) {
            System.err.println("Host unknown: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error occurred: " + e.getMessage());
        } finally {
            // Step 4: Close the socket and scanner to release resources
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Socket closed.");
            }
            if (scanner != null) {
                scanner.close();
                System.out.println("Scanner closed.");
            }
        }
    }
}
