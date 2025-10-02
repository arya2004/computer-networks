#include <iostream>
#include <string>
#include <winsock2.h>
#include <ws2tcpip.h>

#pragma comment(lib, "ws2_32.lib")

using namespace std;

const int PORT = 5000;
const int BUFFER_SIZE = 1024;

int main() {
    // Initialize Winsock
    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        cerr << "WSAStartup failed.\n";
        return 1;
    }

    SOCKET sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock == INVALID_SOCKET) {
        cerr << "Socket creation failed\n";
        WSACleanup();
        return 1;
    }

    sockaddr_in serverAddr;
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port = htons(PORT);
    serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");

    if (connect(sock, (sockaddr*)&serverAddr, sizeof(serverAddr)) == SOCKET_ERROR) {
        cerr << "Connection failed\n";
        closesocket(sock);
        WSACleanup();
        return 1;
    }

    cout << "Connected to the server." << endl;
    string input;
    char buffer[BUFFER_SIZE];

    while (true) {
        cout << "Enter an expression (or 'exit' to quit): ";
        getline(cin, input);
        if (input == "exit") break;

        send(sock, input.c_str(), input.length(), 0);

        int bytesRead = recv(sock, buffer, sizeof(buffer) - 1, 0);
        if (bytesRead <= 0) {
            cout << "Server disconnected." << endl;
            break;
        }
        buffer[bytesRead] = '\0';
        cout << "Result: " << buffer;
    }

    closesocket(sock);
    WSACleanup();
    return 0;
}
