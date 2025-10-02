import socket

def tcp_client():
    server_host = "127.0.0.1"
    server_port = 12345

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((server_host, server_port))

    while True:
        msg = input("Enter message (or 'exit' to quit): ")
        if msg.lower() == "exit":
            break

        sock.sendall(msg.encode())
        data = sock.recv(1024)
        print("Server reply:", data.decode())

    sock.close()

if __name__ == "__main__":
    tcp_client()
