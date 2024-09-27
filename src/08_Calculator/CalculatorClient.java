import java.io.*;
import java.net.*;

public class CalculatorClient {
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader input = null;
        PrintWriter output = null;
        BufferedReader console = null;

        try {
            // Connect to the server at localhost on port 5000
            socket = new Socket("localhost", 5000);
            System.out.println("Connected to the server.");

            // Setup input/output streams for communication
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            console = new BufferedReader(new InputStreamReader(System.in));

            String expression;
            String result;

            // Continuously send expressions to the server and receive results
            while (true) {
                System.out.print("Enter an expression (or type 'exit' to quit): ");
                expression = console.readLine();
                
                if (expression.equalsIgnoreCase("exit")) {
                    break;
                }

                // Send the expression to the server
                output.println(expression);

                // Receive the result from the server
                result = input.readLine();
                System.out.println("Result: " + result);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (console != null) console.close();
                if (input != null) input.close();
                if (output != null) output.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
