import java.io.*;
import java.net.*;
import java.util.*;

public class CalculatorServer {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        BufferedReader input = null;
        PrintWriter output = null;

        try {
          
            serverSocket = new ServerSocket(5000);
            System.out.println("Server is running...");

            socket = serverSocket.accept();
            System.out.println("Client connected.");

            
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            
            String expression;
            while ((expression = input.readLine()) != null) {
                System.out.println("Received expression: " + expression);

            
                String result = evaluateExpression(expression);
                output.println(result);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                if (input != null) input.close();
                if (output != null) output.close();
                if (socket != null) socket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    // Method to evaluate simple mathematical expressions
    private static String evaluateExpression(String expression) {
        try {
            // Remove all spaces from the expression
            expression = expression.replaceAll("\\s+", "");

            // Use Java's built-in scripting engine to evaluate basic expressions (e.g., +, -, *, /)
            double result = evaluateBasicExpression(expression);
            return String.valueOf(result);
        } catch (Exception e) {
            return "Invalid Expression";
        }
    }

    // Basic expression evaluator
    private static double evaluateBasicExpression(String expression) throws Exception {
        // Stack to store numbers and operators
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        int n = expression.length();
        for (int i = 0; i < n; i++) {
            char ch = expression.charAt(i);

            // If the character is a number, parse it
            if (Character.isDigit(ch)) {
                StringBuilder sb = new StringBuilder();
                // Handle multi-digit numbers
                while (i < n && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--; // Go back by one position
                numbers.push(Double.parseDouble(sb.toString()));
            } else if (ch == '(') {
                // Push the opening bracket to the operators stack
                operators.push(ch);
            } else if (ch == ')') {
                // Solve the entire sub-expression within brackets
                while (operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop(); // Remove the '('
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                // While the top of the operator stack has the same or greater precedence, apply the operator
                while (!operators.isEmpty() && hasPrecedence(ch, operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            }
        }

        // Apply the remaining operators in the stack
        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        // The result is the last number in the stack
        return numbers.pop();
    }

    // Check if the current operator has precedence over the top of the stack
    private static boolean hasPrecedence(char currentOp, char topOp) {
        if (topOp == '(' || topOp == ')') {
            return false;
        }
        if ((currentOp == '*' || currentOp == '/') && (topOp == '+' || topOp == '-')) {
            return false;
        }
        return true;
    }

    // Apply the operation to two operands
    private static double applyOperation(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }
}
