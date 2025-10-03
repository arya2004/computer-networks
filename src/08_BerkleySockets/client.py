import socket
import struct
import os


class Client:
    # Class constants variables
    """This class create a client connection to a socket server"""

    def __init__(self, host="localhost", port=5000):
        self.host = host
        self.port = port

    def send_utf(self, sock, message: str):
        """This method send a output message to the socket server"""
        data = message.encode("utf-8")
        length = len(data)
        sock.sendall(struct.pack(">H", length) + data)

    def read_utf(self, sock):
        """This method read the message the send by the socket server"""
        length_bytes = b""
        while len(length_bytes) < 2:
            more = sock.recv(2 - len(length_bytes))
            if not more:
                return None
            length_bytes += more
        length = struct.unpack(">H", length_bytes)[0]
        data = b""
        while len(data) < length:
            more = sock.recv(length - len(data))
            if not more:
                return None
            data += more
        return data.decode("utf-8")

    def send_float(self, sock, value: float):
        """This method send a float to the socket server"""
        sock.sendall(struct.pack(">d", value))

    def send_file(self, sock, filename: str):
        """This method send s a file to the socket server"""
        filesize = os.path.getsize(filename)
        sock.sendall(struct.pack(">Q", filesize))

        with open(filename, "rb") as file:
            while chunk := file.read(4096):
                sock.sendall(chunk)
        print("File sent successfully")

    def start(self):
        """This method start the client server"""
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
            sock.connect((self.host, self.port))
            print(f"Connected to {self.host}:{self.port}")

            while True:
                choice = input(
                    "\n Choose an option: (hello, file, arithmetic, trigonometry, exit)"
                )
                self.send_utf(sock, choice)
                if choice == "exit":
                    print("Exiting...")
                    break

                if choice == "hello":
                    print(self.read_utf(sock))

                elif choice == "file":
                    print(self.read_utf(sock))
                    file_name = input("> ")
                    # Handle the case when the file doesn't exist
                    if not os.path.exists(file_name):
                        print(f"file {file_name} doesn't exist")
                        self.send_utf(sock, "ERROR FILE NOT FOUND")
                        continue
                    self.send_utf(sock, file_name)
                    self.send_file(sock, file_name)

                elif choice == "arithmetic":
                    print(self.read_utf(sock))
                    num1 = float(input("> "))
                    self.send_float(sock, num1)
                    print(self.read_utf(sock))
                    num2 = float(input("> "))
                    self.send_float(sock, num2)
                    print(self.read_utf(sock))
                    operator = input("> ")
                    self.send_utf(sock, operator)
                    print(self.read_utf(sock))

                elif choice == "trigonometry":
                    print(self.read_utf(sock))
                    angle = float(input("> "))
                    self.send_float(sock, angle)
                    print(self.read_utf(sock))
                    trigonometry_func = input("> ")
                    self.send_utf(sock, trigonometry_func)
                    print(self.read_utf(sock))

                else:
                    print(self.read_utf(sock))


if __name__ == "__main__":
    client = Client()
    client.start()
