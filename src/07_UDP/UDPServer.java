
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * UDP Server that listens for client messages on port 1234.
 * The server continuously receives messages and logs them with timestamps.
 * The server terminates when it receives a "bye" message from any client.
 */
public class UDPServer {

    private static final int PORT = 1234; // Port on which the server listens

    public static void main(String[] args) {
        DatagramSocket socket = null;
        byte[] receiveBuffer = new byte[65535]; // Buffer to store incoming messages

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); // Date and time formatter

        try {
            // Step 1: Create a DatagramSocket to listen on the specified port
            socket = new DatagramSocket(PORT);
            System.out.println("Server started. Waiting for clients on port " + PORT + "...");

            while (true) {
                // Step 2: Create a DatagramPacket to receive incoming data
                DatagramPacket receivedPacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                // Step 3: Receive the data from the client
                socket.receive(receivedPacket);

                // Get the current timestamp
                LocalDateTime now = LocalDateTime.now();
                String timeStamp = dtf.format(now);

                // Get the client's IP address
                String clientIP = receivedPacket.getAddress().getHostAddress();

                // Convert the received byte data into a readable string
                String clientMessage = dataToString(receiveBuffer).toString();

                // Log the message with the timestamp and client information
                System.out.println("[" + timeStamp + "] " + "Client (" + clientIP + "): " + clientMessage);

                // Exit the server if the client sends "bye"
                if (clientMessage.equals("bye")) {
                    System.out.println("[" + timeStamp + "] Client (" + clientIP + ") sent 'bye'. Server is exiting...");
                    break;
                }

                // Clear the buffer for the next message
                receiveBuffer = new byte[65535];
            }

        } catch (SocketException e) {
            System.err.println("Socket error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } finally {
            // Step 4: Close the DatagramSocket to release resources
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Server socket closed.");
            }
        }
    }

    /**
     * Utility method to convert byte array data to a string.
     *
     * @param data the byte array containing the message
     * @return A StringBuilder containing the converted string
     */
    public static StringBuilder dataToString(byte[] data) {
        if (data == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < data.length && data[i] != 0) {
            result.append((char) data[i]);
            i++;
        }
        return result;
    }
}
