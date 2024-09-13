package udp_connection;

// Java program to illustrate Server side 
// Implementation using DatagramSocket 
import java.io.IOException; 
import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class udpBaseServer_2 
{ 
    public static void main(String[] args) throws IOException 
    { 
        // Step 1 : Create a socket to listen at port 1234 
        DatagramSocket ds = new DatagramSocket(1234); 
        byte[] receive = new byte[65535]; 

        DatagramPacket DpReceive = null; 
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"); // Time formatting
        
        System.out.println("Server started. Waiting for clients...");

        while (true) 
        { 
            // Step 2 : create a DatagramPacket to receive the data. 
            DpReceive = new DatagramPacket(receive, receive.length); 

            // Step 3 : receive the data in byte buffer. 
            ds.receive(DpReceive); 

            // Get the current time of the message
            LocalDateTime now = LocalDateTime.now();
            String timeStamp = dtf.format(now);

            // Get the client's IP address
            String clientIP = DpReceive.getAddress().getHostAddress();

            // Get the client's message
            String clientMessage = data(receive).toString();

            // Display the chat message in a nice format for a group chat
            System.out.println("[" + timeStamp + "] " + "Client (" + clientIP + "): " + clientMessage);

            // Exit the server if the client sends "bye" 
            if (clientMessage.equals("bye")) 
            { 
                System.out.println("[" + timeStamp + "] Client (" + clientIP + ") sent 'bye'. Server is exiting..."); 
                break; 
            } 

            // Clear the buffer after every message. 
            receive = new byte[65535]; 
        } 
    } 

    // A utility method to convert the byte array 
    // data into a string representation. 
    public static StringBuilder data(byte[] a) 
    { 
        if (a == null) 
            return null; 
        StringBuilder ret = new StringBuilder(); 
        int i = 0; 
        while (a[i] != 0) 
        { 
            ret.append((char) a[i]); 
            i++; 
        } 
        return ret; 
    } 
} 
