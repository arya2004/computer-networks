import socket
import time

WINDOW_SIZE = 4
TIMEOUT = 2
PACKETS = ["P0", "P1", "P2", "P3", "P4", "P5", "P6", "P7"]

def send_packets():
    s = socket.socket()
    s.connect(('localhost', 9999))
    base = 0

    while base < len(PACKETS):
        for i in range(base, min(base + WINDOW_SIZE, len(PACKETS))):
            print(f"Sending: {PACKETS[i]}")
            s.send(PACKETS[i].encode())
            time.sleep(0.5)

        s.settimeout(TIMEOUT)
        try:
            ack = s.recv(1024).decode()
            print(f"Received ACK: {ack}")
            base = int(ack) + 1
        except socket.timeout:
            print("Timeout! Resending window...")

    s.close()

if __name__ == "__main__":
    send_packets()
