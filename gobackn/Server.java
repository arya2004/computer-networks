package gobackn;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

    public static void main(String args[]) throws Exception {
        ServerSocket server = new ServerSocket(6262);
        System.out.println("Server established.");

        // Wait for the client to connect
        Socket client = server.accept();
        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
        System.out.println("Client is now connected.");

        // Read initial values sent by the client
        int x = (Integer) ois.readObject();
        int k = (Integer) ois.readObject();
        int j = 0;
        int i = (Integer) ois.readObject();
        boolean flag = true;
        
        Random r = new Random(6);
        int mod = r.nextInt(6);
        while (mod == 1 || mod == 0) {
            mod = r.nextInt(6);
        }

        while (true) {
            int c = k;

            // Display the frame numbers
            for (int h = 0; h <= x; h++) {
                System.out.print("|" + c + "|");
                c = (c + 1) % x;
            }

            System.out.println();
            System.out.println();

            if (k == j) {
                // Correct frame received
                System.out.println("Frame " + k + " received\nData: " + j);
                j++;
                System.out.println();
            } else {
                // Incorrect frame received
                System.out.println("Frames received not in correct order\nExpected frame: " + j + "\nReceived frame no: " + k);
                System.out.println();
            }

            // Simulate an error condition for certain frames
            if (j % mod == 0 && flag) {
                System.out.println("Error found. Acknowledgement not sent.");
                flag = !flag;
                j--;
            } else if (k == j - 1) {
                // Send acknowledgement
                oos.writeObject(k);
                System.out.println("Acknowledgement sent");
            }

            System.out.println();

            if (j % mod == 0) {
                flag = !flag;
            }

            // Read the next frame
            k = (Integer) ois.readObject();
            if (k == -1) {
                break;
            }

            i = (Integer) ois.readObject();
        }

        System.out.println("Client finished sending data. Exiting");

        // Send termination signal to the client
        oos.writeObject(-1);
    }
}
