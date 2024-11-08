import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server started, waiting for clients...");
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Client connected: " + socket.getInetAddress());
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    // Reading choice of operation
                    String choice = in.readUTF();
                    switch (choice) {
                        case "1":
                            helloEachOther(out, in);
                            break;
                        case "2":
                            fileTransfer(socket, in);
                            break;
                        case "3":
                            calculatorArithmetic(out, in);
                            break;
                        case "4":
                            calculatorTrigonometry(out, in);
                            break;
                        default:
                            out.writeUTF("Invalid choice");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void helloEachOther(DataOutputStream out, DataInputStream in) throws IOException {
        out.writeUTF("Hello from Server!");
        String message = in.readUTF();  // Wait for the client to respond
        System.out.println("Client: " + message);
        out.writeUTF("Goodbye from Server!");
    }

    private static void fileTransfer(Socket socket, DataInputStream in) throws IOException {
        FileOutputStream fileOut = new FileOutputStream("rev.txt");
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) > 0) {
            fileOut.write(buffer, 0, bytesRead);
        }
        fileOut.close();
        System.out.println("File received.");
    }

    private static void calculatorArithmetic(DataOutputStream out, DataInputStream in) throws IOException {
        String operation = in.readUTF();
        double a = in.readDouble();
        double b = in.readDouble();
        double result;

        switch (operation) {
            case "+":
                result = a + b;
                break;
            case "-":
                result = a - b;
                break;
            case "*":
                result = a * b;
                break;
            case "/":
                result = (b != 0) ? a / b : 0;
                break;
            default:
                out.writeUTF("Invalid operation");
                return;
        }
        out.writeUTF("Result: " + result);
    }

    private static void calculatorTrigonometry(DataOutputStream out, DataInputStream in) throws IOException {
        String operation = in.readUTF();
        double value = in.readDouble();
        double result;

        switch (operation) {
            case "sin":
                result = Math.sin(Math.toRadians(value));
                break;
            case "cos":
                result = Math.cos(Math.toRadians(value));
                break;
            case "tan":
                result = Math.tan(Math.toRadians(value));
                break;
            default:
                out.writeUTF("Invalid operation");
                return;
        }
        out.writeUTF("Result: " + result);
    }
}