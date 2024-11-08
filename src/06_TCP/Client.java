import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080)) {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);

            System.out.println("Select operation: ");
            System.out.println("1. Hello Each Other");
            System.out.println("2. File Transfer");
            System.out.println("3. Calculator (Arithmetic)");
            System.out.println("4. Calculator (Trigonometry)");

            String choice = scanner.nextLine();
            out.writeUTF(choice);

            switch (choice) {
                case "1":
                    // Hello message exchange
                    System.out.println(in.readUTF()); // Receive hello from server
                    out.writeUTF("Hello from Client!"); // Send response to server
                    System.out.println(in.readUTF()); // Receive goodbye from server
                    break;
                case "2":
                    fileTransfer(out);
                    break;
                case "3":
                    calculatorArithmetic(out, in, scanner);
                    break;
                case "4":
                    calculatorTrigonometry(out, in, scanner);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fileTransfer(DataOutputStream out) throws IOException {
        FileInputStream fileIn = new FileInputStream("ar.txt");
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileIn.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
        }
        fileIn.close();
        System.out.println("File sent.");
    }

    private static void calculatorArithmetic(DataOutputStream out, DataInputStream in, Scanner scanner) throws IOException {
        System.out.print("Enter operation (+, -, *, /): ");
        String operation = scanner.nextLine();
        out.writeUTF(operation);

        System.out.print("Enter first number: ");
        double a = scanner.nextDouble();
        System.out.print("Enter second number: ");
        double b = scanner.nextDouble();
        out.writeDouble(a);
        out.writeDouble(b);

        System.out.println(in.readUTF()); // Receive result
    }

    private static void calculatorTrigonometry(DataOutputStream out, DataInputStream in, Scanner scanner) throws IOException {
        System.out.print("Enter operation (sin, cos, tan): ");
        String operation = scanner.nextLine();
        out.writeUTF(operation);

        System.out.print("Enter angle in degrees: ");
        double value = scanner.nextDouble();
        out.writeDouble(value);

        System.out.println(in.readUTF()); // Receive result
    }
}