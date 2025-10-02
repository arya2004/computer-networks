import socket

def udp_client():
    server_host = "127.0.0.1"
    server_port = 12345

    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    while True:
        msg = input("Enter message (or 'exit' to quit): ")
        if msg.lower() == "exit":
            break

        sock.sendto(msg.encode(), (server_host, server_port))
        data, _ = sock.recvfrom(1024)  # Receive reply
        print("Server reply:", data.decode())

    sock.close()

if __name__ == "__main__":
    udp_client()
