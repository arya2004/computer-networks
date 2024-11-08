import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {
    private static final int SERVER_PORT = 9876;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try (DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT)) {
            System.out.println("Server is ready to receive files...");
            
            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            boolean receivingFile = true;
            String fileName = "received.c"; // Name to save the received file

            try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
                while (receivingFile) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, BUFFER_SIZE);
                    serverSocket.receive(receivePacket);

                    String receivedData = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    
                    // Check if the client has finished sending the file
                    if (receivedData.equals("END")) {
                        System.out.println("File transfer completed.");
                        receivingFile = false;
                    } else {
                        // Write received data to the file
                        fileOutputStream.write(receivePacket.getData(), 0, receivePacket.getLength());

                        // Send acknowledgment to client
                        InetAddress clientAddress = receivePacket.getAddress();
                        int clientPort = receivePacket.getPort();
                        String ackMessage = "ACK";
                        byte[] ackBytes = ackMessage.getBytes();
                        DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, clientAddress, clientPort);
                        serverSocket.send(ackPacket);
                    }
                }
            }
            System.out.println("File saved as " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}