import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5000);
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nChoose an option: (hello, file, arithmetic, trigonometry, exit)");
            String choice = scanner.nextLine();
            dos.writeUTF(choice);

            if (choice.equals("exit")) {
                System.out.println("Exiting...");
                break;
            }

            switch (choice) {
                case "hello":
                    // a. Say Hello
                    System.out.println(dis.readUTF());
                    break;

                case "file":
                    // b. File Transfer
                    System.out.println(dis.readUTF());
                    String fileName = scanner.nextLine();
                    dos.writeUTF(fileName);
                    sendFile(fileName, socket);
                    break;

                case "arithmetic":
                    // c. Calculator (Arithmetic)
                    System.out.println(dis.readUTF());
                    double num1 = scanner.nextDouble();
                    dos.writeDouble(num1);

                    System.out.println(dis.readUTF());
                    double num2 = scanner.nextDouble();
                    dos.writeDouble(num2);

                    scanner.nextLine(); // consume newline

                    System.out.println(dis.readUTF());
                    String operator = scanner.nextLine();
                    dos.writeUTF(operator);

                    System.out.println(dis.readUTF());
                    break;

                case "trigonometry":
                    // d. Calculator (Trigonometry)
                    System.out.println(dis.readUTF());
                    double angle = scanner.nextDouble();
                    dos.writeDouble(angle);

                    scanner.nextLine(); // consume newline

                    System.out.println(dis.readUTF());
                    String func = scanner.nextLine();
                    dos.writeUTF(func);

                    System.out.println(dis.readUTF());
                    break;

                default:
                    System.out.println(dis.readUTF());
                    break;
            }
        }

        socket.close();
    }

    private static void sendFile(String fileName, Socket socket) throws IOException {
        File file = new File(fileName);
        byte[] buffer = new byte[4096];
        FileInputStream fis = new FileInputStream(file);
        OutputStream os = socket.getOutputStream();

        int bytesRead;
        while ((bytesRead = fis.read(buffer, 0, buffer.length)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        fis.close();
        System.out.println("File sent successfully.");
    }
}
