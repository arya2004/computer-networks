import socket

def tcp_server():
    host = "127.0.0.1"   # Localhost
    port = 12345         # Port to bind

    # Create TCP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind((host, port))
    sock.listen(5)  # Allow up to 5 queued connections

    print(f"TCP server listening on {host}:{port}")

    conn, addr = sock.accept()
    print(f"Connected by {addr}")

    while True:
        data = conn.recv(1024)  # Receive up to 1024 bytes
        if not data:
            break
        print("Client says:", data.decode())
        conn.sendall(b"Message received")  # Send response

    conn.close()

if __name__ == "__main__":
    tcp_server()
