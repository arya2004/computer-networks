

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * TCP Client that connects to a server and sends messages.
 * The client reads user input from the console and sends it to the server.
 * The connection is terminated when the user inputs "Over".
 */
public class Client {

    private Socket socket = null;             // Client socket to connect to the server
    private DataInputStream input = null;     // Input stream to read from the console
    private DataOutputStream out = null;      // Output stream to send data to the server

    /**
     * Constructor to create a client connection to a server.
     *
     * @param address The IP address of the server
     * @param port The port number on which the server is listening
     */
    public Client(String address, int port) {
        try {
            // Establish the connection to the server at the given address and port
            socket = new Socket(address, port);
            System.out.println("Connected to server at " + address + " on port " + port);

            // Initialize input stream to read from the console (user input)
            input = new DataInputStream(System.in);

            // Initialize output stream to send data to the server
            out = new DataOutputStream(socket.getOutputStream());

        } catch (UnknownHostException u) {
            System.err.println("Unknown host: " + u.getMessage());
            return;
        } catch (IOException i) {
            System.err.println("I/O error: " + i.getMessage());
            return;
        }

        // String to hold the message from the console input
        String message = "";

        // Read messages from the console and send to the server until "Over" is typed
        while (!message.equals("Over")) {
            try {
                message = input.readLine();    // Read user input
                out.writeUTF(message);         // Send message to server
            } catch (IOException i) {
                System.err.println("Error sending message: " + i.getMessage());
            }
        }

        // Close the connection and release resources
        closeConnection();
    }

    /**
     * Closes the socket and streams to release resources.
     */
    private void closeConnection() {
        try {
            if (input != null) {
                input.close();    // Close the input stream
            }
            if (out != null) {
                out.close();      // Close the output stream
            }
            if (socket != null) {
                socket.close();   // Close the client socket
            }
            System.out.println("Connection closed.");
        } catch (IOException i) {
            System.err.println("Error closing connection: " + i.getMessage());
        }
    }

    /**
     * Main method to run the client and establish the connection.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Replace with actual server IP and port
        String serverAddress = "192.168.22.47"; 
        int serverPort = 12345;
        
        Client client = new Client(serverAddress, serverPort);
    }
}
