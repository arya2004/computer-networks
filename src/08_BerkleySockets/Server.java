import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.lang.Math;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server started, waiting for client...");
        Socket socket = serverSocket.accept();
        System.out.println("Client connected!");

        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nWaiting for client request...");

            // Receive request type from client
            String request = dis.readUTF();
            if (request.equals("exit")) {
                System.out.println("Connection closed by client.");
                break;
            }

            switch (request) {
                case "hello":
                    // a. Say Hello
                    dos.writeUTF("Server says: Hello, Client!");
                    break;

                case "file":
                    // b. File Transfer
                    dos.writeUTF("Send me a file name to receive:");
                    String fileName = dis.readUTF();
                    receiveFile(fileName, socket);
                    break;

                case "arithmetic":
                    // c. Calculator (Arithmetic)
                    dos.writeUTF("Enter first number:");
                    double num1 = dis.readDouble();
                    dos.writeUTF("Enter second number:");
                    double num2 = dis.readDouble();
                    dos.writeUTF("Enter operator (+, -, *, /):");
                    String operator = dis.readUTF();
                    double result = calculateArithmetic(num1, num2, operator);
                    dos.writeUTF("Arithmetic result: " + result);
                    break;

                case "trigonometry":
                    // d. Calculator (Trigonometry)
                    dos.writeUTF("Enter angle in degrees:");
                    double angle = dis.readDouble();
                    dos.writeUTF("Enter trigonometric function (sin, cos, tan):");
                    String func = dis.readUTF();
                    double trigResult = calculateTrigonometry(angle, func);
                    dos.writeUTF("Trigonometric result: " + trigResult);
                    break;

                default:
                    dos.writeUTF("Invalid request!");
                    break;
            }
        }

        socket.close();
        serverSocket.close();
    }

    // File receiving
    private static void receiveFile(String fileName, Socket socket) throws IOException {
        byte[] buffer = new byte[4096];
        FileOutputStream fos = new FileOutputStream(fileName);
        InputStream is = socket.getInputStream();
        int bytesRead;
        while ((bytesRead = is.read(buffer, 0, buffer.length)) != -1) {
            fos.write(buffer, 0, bytesRead);
            if (bytesRead < buffer.length) break; // assuming end of file
        }
        fos.close();
        System.out.println("File " + fileName + " received successfully.");
    }

    // Arithmetic calculation
    private static double calculateArithmetic(double num1, double num2, String operator) {
        switch (operator) {
            case "+": return num1 + num2;
            case "-": return num1 - num2;
            case "*": return num1 * num2;
            case "/": return num1 / num2;
            default: return 0;
        }
    }

    // Trigonometric calculation
    private static double calculateTrigonometry(double angle, String func) {
        double radians = Math.toRadians(angle);
        switch (func) {
            case "sin": return Math.sin(radians);
            case "cos": return Math.cos(radians);
            case "tan": return Math.tan(radians);
            default: return 0;
        }
    }
}
