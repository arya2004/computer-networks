import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class SimpleHTTPServer {

    public static void main(String[] args) {
        // Start servers for different HTTP versions
        new Thread(() -> startServer(8000, "HTTP/1.0")).start();
        new Thread(() -> startServer(8001, "HTTP/1.1")).start();
        new Thread(() -> startServer(8002, "HTTP/2.0")).start();
    }

    public static void startServer(int port, String httpVersion) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[" + httpVersion + " Server] Listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket, httpVersion)).start();
            }
        } catch (IOException e) {
            System.err.println("[" + httpVersion + " Server] Error: " + e.getMessage());
        }
    }

    public static void handleClient(Socket clientSocket, String httpVersion) {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
            );
            BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(clientSocket.getOutputStream())
            )
        ) {
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }

            System.out.println("[" + httpVersion + " Server] Received request: " + requestLine);

            StringTokenizer tokenizer = new StringTokenizer(requestLine);
            String method = tokenizer.nextToken();
            String uri = tokenizer.nextToken();
            String protocol = tokenizer.nextToken();

            // Read headers (we won't process them here)
            String header;
            while ((header = in.readLine()) != null && !header.isEmpty()) {
                // For debugging purposes, print headers
                System.out.println("[" + httpVersion + " Server] Header: " + header);
            }

            // Simulate processing time
            Thread.sleep(100);

            // Generate response based on URI
            String responseStatus;
            String responseBody;

            if ("/success".equals(uri)) {
                responseStatus = "200 OK";
                responseBody = "Success!";
            } else if ("/error".equals(uri)) {
                responseStatus = "404 Not Found";
                responseBody = "Error: Not Found";
            } else {
                responseStatus = "400 Bad Request";
                responseBody = "Bad Request";
            }

            // For HTTP/1.0, close connection after response
            boolean keepAlive = false;

            // For HTTP/1.1, check for Connection header
            if ("HTTP/1.1".equals(httpVersion)) {
                keepAlive = true; // Default is keep-alive
            }

            // For HTTP/2.0, we simulate the behavior
            if ("HTTP/2.0".equals(httpVersion)) {
                // Simulate HTTP/2 response (Note: HTTP/2 uses binary framing in reality)
                responseStatus = "200 OK";
                responseBody = "Simulated HTTP/2 Response";
                keepAlive = true;
            }

            // Send response
            out.write(protocol + " " + responseStatus + "\r\n");
            out.write("Content-Type: text/plain\r\n");
            out.write("Content-Length: " + responseBody.length() + "\r\n");
            if (!keepAlive) {
                out.write("Connection: close\r\n");
            }
            out.write("\r\n");
            out.write(responseBody);
            out.flush();

            System.out.println("[" + httpVersion + " Server] Sent response: " + responseStatus);

            if (!keepAlive) {
                clientSocket.close();
            }
        } catch (Exception e) {
            System.err.println("[" + httpVersion + " Server] Error handling client: " + e.getMessage());
        }
    }
}
