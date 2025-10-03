import socket

def start_client():
    """Start the calculator client."""
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect(("localhost", 5000))
    print("Connected to the server.")

    try:
        while True:
            expression = input("Enter an expression (or type 'exit' to quit): ")
            if expression.lower() == "exit":
                break

            client_socket.sendall(expression.encode())
            result = client_socket.recv(1024).decode()
            print("Result:", result)
    finally:
        client_socket.close()

if __name__ == "__main__":
    start_client()