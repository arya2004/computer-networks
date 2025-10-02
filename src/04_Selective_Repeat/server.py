import socket

def receive_packets():
    s = socket.socket()
    s.bind(('localhost', 9999))
    s.listen(1)
    conn, addr = s.accept()
    print("Connected by", addr)

    expected = 0
    buffer = {}

    while True:
        data = conn.recv(1024).decode()
        if not data:
            break

        seq = int(data[1])
        print(f"Received: {data}")

        if seq == expected:
            print(f"Accepted: {data}")
            conn.send(str(seq).encode())
            expected += 1
        else:
            print(f"Buffered: {data}")
            buffer[seq] = data
            conn.send(str(expected - 1).encode())

    conn.close()
    s.close()

if __name__ == "__main__":
    receive_packets()
