import socket

def udp_server():
    host = "127.0.0.1"   # Localhost
    port = 12345         # Port to bind

    # Create UDP socket
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sock.bind((host, port))

    print(f"UDP server listening on {host}:{port}")

    while True:
        data, addr = sock.recvfrom(1024)  # Receive data (max 1024 bytes)
        print(f"Received from {addr}: {data.decode()}")
        sock.sendto(b"Message received", addr)  # Send response

if __name__ == "__main__":
    udp_server()
