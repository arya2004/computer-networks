// Simple UDP client: send a file in fixed-size chunks to a UDP server and wait for ACKs
#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <cstring>      // For memset, strncmp, strerror
#include <sys/socket.h> // For socket, sendto, recvfrom
#include <netinet/in.h> // For sockaddr_in, htons
#include <arpa/inet.h>  // For inet_pton
#include <unistd.h>     // For close()
#include <sys/time.h>   // For struct timeval

#define SERVER_PORT 9876
#define BUFFER_SIZE 1024

int main(int argc, char *argv[]) {
    if (argc < 2) {
        std::cerr << "Usage: " << argv[0] << " <file-to-send> [server-ip]" << std::endl;
        return 1;
    }

    const char *filename = argv[1];
    const char *server_ip = (argc >= 3) ? argv[2] : "127.0.0.1";

    std::ifstream input_file(filename, std::ios::binary);
    if (!input_file.is_open()) {
        std::cerr << "Error: Could not open file '" << filename << "' for reading" << std::endl;
        return 1;
    }

    int sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0) {
        std::cerr << "Error: socket() failed: " << strerror(errno) << std::endl;
        return 1;
    }

    struct sockaddr_in server_addr;
    std::memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(SERVER_PORT);
    if (inet_pton(AF_INET, server_ip, &server_addr.sin_addr) != 1) {
        std::cerr << "Error: invalid server IP '" << server_ip << "'" << std::endl;
        close(sockfd);
        return 1;
    }

    std::vector<char> buffer(BUFFER_SIZE);
    const std::string end_message = "END";
    const std::string ack_message = "ACK";

    // Set receive timeout for ACKs
    struct timeval tv;
    tv.tv_sec = 2;  // 2 seconds
    tv.tv_usec = 0;
    if (setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv)) < 0) {
        std::cerr << "Warning: setsockopt SO_RCVTIMEO failed: " << strerror(errno) << std::endl;
    }

    socklen_t server_len = sizeof(server_addr);

    const int max_retries = 5;

    // Read file and send in chunks
    while (input_file) {
        input_file.read(buffer.data(), BUFFER_SIZE);
        std::streamsize n = input_file.gcount();
        if (n <= 0) break;

        int retries = 0;
        bool acked = false;
        while (retries < max_retries && !acked) {
            ssize_t sent = sendto(sockfd, buffer.data(), (size_t)n, 0,
                                  (struct sockaddr *)&server_addr, server_len);
            if (sent < 0) {
                std::cerr << "Error: sendto failed: " << strerror(errno) << std::endl;
                close(sockfd);
                return 1;
            }

            // Wait for ACK
            char ack_buf[16];
            ssize_t r = recvfrom(sockfd, ack_buf, sizeof(ack_buf), 0, nullptr, nullptr);
            if (r > 0) {
                if (r == (ssize_t)ack_message.length() &&
                    std::strncmp(ack_buf, ack_message.c_str(), ack_message.length()) == 0) {
                    acked = true;
                    break;
                }
            } else {
                // timeout or error
                if (errno == EWOULDBLOCK || errno == EAGAIN) {
                    retries++;
                    std::cerr << "Warning: ACK timeout, retry " << retries << " for chunk" << std::endl;
                    continue;
                } else {
                    std::cerr << "Warning: recvfrom error: " << strerror(errno) << std::endl;
                    retries++;
                    continue;
                }
            }
        }

        if (!acked) {
            std::cerr << "Error: failed to receive ACK after " << max_retries << " retries" << std::endl;
            close(sockfd);
            return 1;
        }
    }

    // Send END message
    int end_retries = 0;
    bool end_acked = false;
    while (end_retries < max_retries && !end_acked) {
        ssize_t sent = sendto(sockfd, end_message.c_str(), end_message.length(), 0,
                              (struct sockaddr *)&server_addr, server_len);
        if (sent < 0) {
            std::cerr << "Error: sendto END failed: " << strerror(errno) << std::endl;
            close(sockfd);
            return 1;
        }

        char ack_buf[16];
        ssize_t r = recvfrom(sockfd, ack_buf, sizeof(ack_buf), 0, nullptr, nullptr);
        if (r > 0) {
            if (r == (ssize_t)ack_message.length() &&
                std::strncmp(ack_buf, ack_message.c_str(), ack_message.length()) == 0) {
                end_acked = true;
                break;
            }
        } else {
            if (errno == EWOULDBLOCK || errno == EAGAIN) {
                end_retries++;
                std::cerr << "Warning: END ACK timeout, retry " << end_retries << std::endl;
                continue;
            } else {
                end_retries++;
                std::cerr << "Warning: recvfrom error on END: " << strerror(errno) << std::endl;
                continue;
            }
        }
    }

    if (!end_acked) {
        std::cerr << "Warning: no ACK for END after " << max_retries << " retries; continuing." << std::endl;
    }

    std::cout << "File '" << filename << "' sent." << std::endl;

    close(sockfd);
    return 0;
}