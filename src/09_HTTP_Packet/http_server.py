import socket
import threading
import time


def start_server(port: int, http_version: str):
    """Start an HTTP server on the given port with the given simulated HTTP version."""
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
        server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        server_socket.bind(("0.0.0.0", port))
        server_socket.listen(5)
        print(f"[{http_version} Server] Listening on port {port}")

        while True:
            client_socket, addr = server_socket.accept()
            threading.Thread(
                target=handle_client, args=(client_socket, http_version)
            ).start()


def handle_client(client_socket: socket.socket, http_version: str):
    try:
        with client_socket:
            # Read request
            request_data = client_socket.recv(4096).decode("iso-8859-1")
            if not request_data:
                return

            # First line of HTTP request
            request_line = request_data.splitlines()[0]
            print(f"[{http_version} Server] Received request: {request_line}")

            parts = request_line.split()
            if len(parts) < 3:
                return
            method, uri, protocol = parts

            # Print headers (don’t process)
            headers = request_data.split("\r\n")[1:]
            for h in headers:
                if not h.strip():
                    break
                print(f"[{http_version} Server] Header: {h}")

            # Simulate processing time
            time.sleep(0.1)

            # Generate response
            if uri == "/success":
                response_status = "200 OK"
                response_body = "Success!"
            elif uri == "/error":
                response_status = "404 Not Found"
                response_body = "Error: Not Found"
            else:
                response_status = "400 Bad Request"
                response_body = "Bad Request"

            keep_alive = False
            if http_version == "HTTP/1.1":
                keep_alive = True  # default keep-alive
            elif http_version == "HTTP/2.0":
                # Simulate HTTP/2 (real HTTP/2 is binary, but here text-based simulation)
                response_status = "200 OK"
                response_body = "Simulated HTTP/2 Response"
                keep_alive = True

            # Build response
            response_lines = [
                f"{protocol} {response_status}",
                "Content-Type: text/plain",
                f"Content-Length: {len(response_body)}",
            ]
            if not keep_alive:
                response_lines.append("Connection: close")
            response_lines.append("")  # empty line
            response = "\r\n".join(response_lines) + "\r\n" + response_body

            # Send
            client_socket.sendall(response.encode("iso-8859-1"))
            print(f"[{http_version} Server] Sent response: {response_status}")

            if not keep_alive:
                client_socket.close()

    except Exception as e:
        print(f"[{http_version} Server] Error handling client: {e}")


if __name__ == "__main__":
    threading.Thread(target=start_server, args=(8000, "HTTP/1.0"), daemon=True).start()
    threading.Thread(target=start_server, args=(8001, "HTTP/1.1"), daemon=True).start()
    threading.Thread(target=start_server, args=(8002, "HTTP/2.0"), daemon=True).start()

    print("Servers running. Press Ctrl+C to stop.")
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("Shutting down servers...")
