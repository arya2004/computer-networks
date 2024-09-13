

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP Server that listens for client connections and receives messages.
 * The server waits for a client to connect, reads messages from the client,
 * and terminates when the client sends the message "Over".
 */
public class Server {

    private Socket socket = null;            // Client socket
    private ServerSocket server = null;      // Server socket
    private DataInputStream in = null;       // Input stream to read data from the client

    /**
     * Constructor to start the server on a given port.
     * 
     * @param port the port number on which the server listens for connections
     */
    public Server(int port) {
        try {
            // Initialize the server socket to listen on the specified port
            server = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            // Wait for a client connection
            System.out.println("Waiting for a client ...");
            socket = server.accept();  // Accept the client connection
            System.out.println("Client connected.");

            // Set up the input stream to receive data from the client
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            String message = "";

            // Continuously read messages from the client until "Over" is received
            while (!message.equals("Over")) {
                try {
                    message = in.readUTF();  // Read UTF-encoded message from client
                    System.out.println("Received from client: " + message);
                } catch (IOException e) {
                    System.err.println("Error reading from client: " + e.getMessage());
                }
            }
            
            // Client has finished sending messages, so close the connection
            System.out.println("Closing connection.");

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            // Close resources to prevent resource leaks
            closeConnection();
        }
    }

    /**
     * Closes the server socket and input stream to release resources.
     */
    private void closeConnection() {
        try {
            if (in != null) {
                in.close();  // Close the input stream
            }
            if (socket != null) {
                socket.close();  // Close the client socket
            }
            if (server != null) {
                server.close();  // Close the server socket
            }
            System.out.println("Server connection closed.");
        } catch (IOException e) {
            System.err.println("Error closing connections: " + e.getMessage());
        }
    }

    /**
     * Main method to start the server on the specified port.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        int port = 12345;  // You can change this to any valid port number
        Server server = new Server(port);  // Start the server on the specified port
    }
}
