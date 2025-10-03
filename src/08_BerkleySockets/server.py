import socket
import threading
import math
import struct
import os


class Server:
    """This class create a socket server"""

    def __init__(self, host="0.0.0.0", port=5000):
        self.host = host
        self.port = port
        self.socket_server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    # ---- methods for UTF messages ----
    def send_utf(self, socket, message: str):
        data = message.encode("utf-8")
        length = len(data)
        socket.sendall(struct.pack(">H", length) + data)

    def read_utf(self, socket):
        length_bytes = b""
        while len(length_bytes) < 2:
            more = socket.recv(2 - len(length_bytes))
            if not more:
                raise ConnectionAbortedError("Connection closed while reading length")
            length_bytes += more
        length = struct.unpack(">H", length_bytes)[0]
        data = b""
        while len(data) < length:
            more = socket.recv(length - len(data))
            if not more:
                raise ConnectionAbortedError("Connection closed whil reading data")
            data += more
        return data.decode("utf-8")

    # ------ method for float -------
    def read_float(self, sock):
        data = sock.recv(8)
        return struct.unpack(">d", data)[0]

    # ------ main server methods -------
    def start(self):
        """This method start the server and makes it wait for cient connection"""
        self.socket_server.bind((self.host, self.port))
        self.socket_server.settimeout(
            3
        )  # telling socket server to wait for 3s for subsequent operations
        self.socket_server.listen(5)

        print(
            f"server started listening on {self.host}:{self.port}, waiting for client connection..."
        )
        try:
            while True:
                try:
                    client_socket, address = self.socket_server.accept()
                    print("client connected")
                    threading.Thread(
                        target=self.handle_client, args=(client_socket,)
                    ).start()
                except socket.timeout:
                    continue
        except KeyboardInterrupt:
            print("Server shutting down...")
            self.shutdown()

    def handle_client(self, client_socket):
        """This method handle client connection with the socket server"""
        with client_socket:
            while True:
                try:
                    request = self.read_utf(client_socket)
                    if not request:
                        print("Client disconnected")
                        break
                    if request == "exit":
                        print("connection closed by client")
                        break
                    self.process_request(request, client_socket)
                except ConnectionResetError:
                    print("client disconnected abruptly")
                    break
            client_socket.close()

    def process_request(self, request, client_socket):
        """This method handle the client request to the server"""
        if request == "hello":
            self.send_utf(socket=client_socket, message="server says: Hello client!")

        elif request == "file":
            self.send_utf(
                socket=client_socket, message="Send me a filename to receive: "
            )
            file_name = self.read_utf(socket=client_socket)
            # Handle the case when the file send to the server doesn't exist on the client side
            if file_name.startswith("ERROR"):
                print(f"Client Error: {file_name} not found")
                return
            self.receive_file(file_name, client_socket)
            print(f"file {file_name} successfully received")

        elif request == "arithmetic":
            self.send_utf(client_socket, "Enter first number:")
            first_number = self.read_float(client_socket)
            self.send_utf(client_socket, "Enter second number:")
            second_number = self.read_float(client_socket)
            self.send_utf(client_socket, "Enter operator(+, -, *, /):")
            operator = self.read_utf(client_socket)
            result = self.calculateArithmetic(first_number, second_number, operator)
            self.send_utf(client_socket, f"Arithmetic resut: {result}")

        elif request == "trigonometry":
            self.send_utf(client_socket, "Enter angles in degrees:")
            angle = float(self.read_float(client_socket))
            self.send_utf(client_socket, "Enter trigonometric function (sin, cos, tan)")
            trigonometric_func = self.read_utf(client_socket)
            trigonometric_result = self.calculateTrigonometric(
                angle, trigonometric_func
            )
            self.send_utf(
                client_socket, f"Trigonometric result: {trigonometric_result}"
            )

        else:
            self.send_utf(client_socket, "invalid request")

    def receive_file(self, filename, client_socket, save_dir="received_files"):
        """This method receives the file from the client"""
        # Getting project directory and save_dir paths
        project_dir = os.path.dirname(os.path.abspath(__file__))
        save_dir_path = os.path.join(project_dir, save_dir)

        # Create the directory if it doesn't exist
        os.makedirs(save_dir_path, exist_ok=True)
        # Getting complete path of the file
        filepath = os.path.join(save_dir_path, filename)

        # Reading the file sent by the client
        filesize_bits = client_socket.recv(8)
        filesize = struct.unpack(">Q", filesize_bits)[0]
        
        mode = (
            "ab" if os.path.exists(filepath) else "wb"
        )  # just check if file exist append data

        with open(filepath, mode) as file:
            # Making sure file every new append start with a new line
            if mode =="ab":
                file.write(b"\n")
            remaining = filesize
            while remaining > 0:
                data = client_socket.recv(min(4096, remaining))
                if not data:
                    break
                file.write(data)
                remaining -= len(data)
        print(f"file {filename} received successfully ({filesize} bytes)")

    def shutdown(self):
        """This method shut down the socket server"""
        try:
            self.socket_server.close()
            print("socket server closed")
        except Exception as e:
            print(f"Error when closing socket: {e}")

    @staticmethod
    def calculateArithmetic(first_number, second_number, operator):
        """This static method do an arithmetic operation"""
        if operator == "+":
            return first_number + second_number
        if operator == "-":
            return first_number - second_number
        if operator == "*":
            return first_number * second_number
        if operator == "/":
            return first_number / second_number

    @staticmethod
    def calculateTrigonometric(angle, trigonometric_func):
        if trigonometric_func == "sin":
            return math.sin(angle)
        if trigonometric_func == "cos":
            return math.cos(angle)
        if trigonometric_func == "tan":
            return math.tan(angle)


if __name__ == "__main__":
    server = Server()
    server.start()
