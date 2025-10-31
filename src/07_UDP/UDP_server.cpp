#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <cstring>      // For memset, strncmp
#include <sys/socket.h> // For socket, bind, recvfrom, sendto
#include <netinet/in.h> // For sockaddr_in, htons, INADDR_ANY
#include <unistd.h>     // For close()

#define SERVER_PORT 9876
#define BUFFER_SIZE 1024

int main() {
    int server_socket_fd;
    struct sockaddr_in server_addr, client_addr;
    socklen_t client_len = sizeof(client_addr);

    // Create a UDP socket (AF_INET = IPv4, SOCK_DGRAM = UDP)
    server_socket_fd = socket(AF_INET, SOCK_DGRAM, 0);
    if (server_socket_fd < 0) {
        std::cerr << "Error: Could not create socket" << std::endl;
        return 1;
    }

    // Set up the server address structure
    std::memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(SERVER_PORT); // htons converts port to network byte order
    server_addr.sin_addr.s_addr = INADDR_ANY; // Listen on all available interfaces

    // Bind the socket to the server's address and port
    if (bind(server_socket_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        std::cerr << "Error: Could not bind socket" << std::endl;
        close(server_socket_fd);
        return 1;
    }

    std::cout << "Server is ready to receive files..." << std::endl;

    // Open the output file in binary write mode
    std::ofstream output_file("received.c", std::ios::binary);
    if (!output_file.is_open()) {
        std::cerr << "Error: Could not open output file" << std::endl;
        close(server_socket_fd);
        return 1;
    }

    std::vector<char> buffer(BUFFER_SIZE);
    bool receivingFile = true;
    const std::string end_message = "END";
    const std::string ack_message = "ACK";

    while (receivingFile) {
        // Wait for a packet (blocking call)
        ssize_t bytes_received = recvfrom(server_socket_fd, buffer.data(), BUFFER_SIZE, 0,
                                          (struct sockaddr *)&client_addr, &client_len);

        if (bytes_received < 0) {
            std::cerr << "Error: recvfrom failed" << std::endl;
            continue;
        }

        // Check if the packet contains the "END" message
        if (bytes_received == (ssize_t)end_message.length() && 
            std::strncmp(buffer.data(), end_message.c_str(), end_message.length()) == 0) 
        {
            std::cout << "File transfer completed." << std::endl;
            receivingFile = false;
        } else {
            // Write the received data chunk to the file
            output_file.write(buffer.data(), bytes_received);

            // Send "ACK" back to the client to confirm receipt
            sendto(server_socket_fd, ack_message.c_str(), ack_message.length(), 0,
                   (struct sockaddr *)&client_addr, client_len);
        }
    }

    // Clean up
    output_file.close();
    close(server_socket_fd);
    std::cout << "File saved as received.c" << std::endl;

    return 0;
}