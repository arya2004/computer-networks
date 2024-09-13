## Go-Back-N ARQ Implementation (Client-Server)

### Overview

This project implements a **Go-Back-N Automatic Repeat Request (ARQ)** protocol using Java, which is a reliable data transmission protocol in computer networks. It uses both the client-server model and sliding window mechanism to handle frame transmissions and acknowledgments. The project consists of a **server** and a **client** that exchange frames (data) using the Go-Back-N protocol.

### Concepts

#### 1. **Go-Back-N ARQ Protocol:**
Go-Back-N ARQ is a type of sliding window protocol for error control in data transmission. The sender can send several frames before needing an acknowledgment, but the receiver only sends acknowledgments for the last correctly received frame. If any frame is found to be erroneous, the receiver requests retransmission starting from that erroneous frame, and the sender "goes back" to retransmit all subsequent frames from that point onward.

#### 2. **Sliding Window:**
The sliding window concept allows a sender to send multiple frames before needing an acknowledgment. The window size determines the number of frames that can be sent before pausing to wait for an acknowledgment from the receiver. 

#### 3. **Acknowledgments:**
In Go-Back-N ARQ, acknowledgments are cumulative. This means that if a frame is acknowledged, it implies all previous frames have been successfully received.

#### 4. **Error Handling:**
The protocol assumes there may be transmission errors (e.g., lost frames, corrupted data), and it handles such scenarios by retransmitting frames starting from the erroneous frame once detected.

---

### Project Structure

#### 1. **Client:**
The `Client` class is responsible for sending data frames to the server. It implements the following features:
- Reads the number of frames to send and data for each frame from the user.
- Implements a sliding window mechanism to send multiple frames without waiting for acknowledgments for each one.
- Starts a separate thread to listen for acknowledgments from the server.
- Handles retransmissions when an acknowledgment is not received within the specified timeout.

**Key Operations:**
- Establishes a connection with the server.
- Sends frames in batches based on the window size.
- Waits for acknowledgments and retransmits frames if necessary.
- Terminates the connection once all frames are successfully acknowledged.

**Classes:**
- `Client`: Manages the data transmission, acknowledgment handling, and retransmission logic.
- `GoBackNListener`: A thread that listens for acknowledgments from the server and updates the last acknowledged frame.

#### 2. **Server:**
The `Server` class is responsible for receiving frames, verifying their order, and sending acknowledgments back to the client. It also simulates errors in data transmission.

**Key Operations:**
- Listens for incoming client connections.
- Receives frames from the client and checks if they are in the correct order.
- Sends cumulative acknowledgments for correctly received frames.
- Simulates errors by intentionally not sending acknowledgments for specific frames.
- Terminates the connection once the transmission is complete.

**Classes:**
- `Server`: Manages the frame reception, error simulation, and acknowledgment logic.

---

### How the Code Works

#### 1. **Client Code Workflow:**
1. The client first prompts the user to enter a value for `m` (window size) and the number of frames to send.
2. It then reads the data for each frame from the user.
3. The client establishes a connection with the server using a socket.
4. It sends the window size (`x`) to the server.
5. The client sends frames in batches, using the sliding window approach, waiting for acknowledgments.
6. If an acknowledgment is not received within a timeout (3.5 seconds), it retransmits from the last unacknowledged frame.
7. The process repeats until all frames are acknowledged, and then the client sends a termination signal.

#### 2. **Server Code Workflow:**
1. The server listens on a specific port for client connections.
2. Upon connection, the server reads the window size and frames sent by the client.
3. It verifies if frames are received in order, simulating errors where needed.
4. The server sends acknowledgments for correctly received frames.
5. If a frame is received out of order or an error occurs, it does not send an acknowledgment and expects the client to retransmit.
6. The server closes the connection once all frames are successfully acknowledged, and the client sends a termination signal.

---

### Example of Data Transmission:

1. **Client sends frames 0 to 3.**
2. **Server acknowledges frame 2** (simulating that frame 3 was not received properly).
3. **Client retransmits from frame 3** onwards.
4. **Server acknowledges frame 3**, and the process continues until all frames are successfully sent.

---

### Running the Program

1. **Compile the Code:**
   ```
   javac -d . Server.java Client.java
   ```

2. **Run the Server:**
   ```
   java gobackn.Server
   ```

3. **Run the Client:**
   ```
   java gobackn.Client
   ```

4. **Follow the prompts on the client side:**
   - Enter the value of `m` (to define the window size).
   - Enter the number of frames and the data for each frame.

---

### Notes:
- This implementation is for educational purposes and demonstrates how Go-Back-N ARQ works in a simplified scenario.
- The client and server run on `localhost` by default, and the connection port is `6262`.
- Error handling is simulated for demonstration purposes, and various scenarios such as lost acknowledgments or out-of-order frames can be tested.

### Future Improvements:
- Implement support for handling multiple clients simultaneously.
- Enhance the error simulation to be more realistic, with configurable error rates.
- Add support for running over real networks with potential packet loss.

---

### Authors
This Go-Back-N ARQ implementation is a sample project to demonstrate sliding window protocols and reliable data transmission in Java using sockets.