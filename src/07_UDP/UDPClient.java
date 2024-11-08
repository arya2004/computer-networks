import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 9876;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        String filePath = "main.c";
        try (DatagramSocket clientSocket = new DatagramSocket();
             FileInputStream fileInputStream = new FileInputStream(filePath)) {

            InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                DatagramPacket sendPacket = new DatagramPacket(buffer, bytesRead, serverAddress, SERVER_PORT);
                clientSocket.send(sendPacket);

                // Wait for acknowledgment
                byte[] ackBuffer = new byte[BUFFER_SIZE];
                DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
                clientSocket.receive(ackPacket);

                String ackMessage = new String(ackPacket.getData(), 0, ackPacket.getLength());
                if (!ackMessage.equals("ACK")) {
                    System.out.println("Failed to receive acknowledgment. Resending packet...");
                    clientSocket.send(sendPacket); // Resend packet if acknowledgment is not received
                }
            }

            // Send an "END" message to indicate the end of file transfer
            byte[] endMessage = "END".getBytes();
            DatagramPacket endPacket = new DatagramPacket(endMessage, endMessage.length, serverAddress, SERVER_PORT);
            clientSocket.send(endPacket);

            System.out.println("File sent successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}