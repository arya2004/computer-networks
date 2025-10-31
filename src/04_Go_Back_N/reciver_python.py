"""Python implementation of Go-Back-N Receiver.
Listens for frames via UDP and sends acknowledgments (ACKs) for correctly received frames.
"""

import socket
import random  # Used for simulating packet loss

HOST = "127.0.0.1"
PORT = 5001
LOSS_PROBABILITY = 0.2  # 20% frame loss simulation


def receiver():
    """Receive frames from the sender and send ACKs according to Go-Back-N ARQ rules."""
    receiver_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    receiver_socket.bind((HOST, PORT))
    expected_frame = 0
    print("[Receiver] Ready to receive frames...")

    while True:
        data, addr = receiver_socket.recvfrom(1024)
        frame = int(data.decode().split()[1])

        # Simulate packet loss
        if random.random() < LOSS_PROBABILITY:
            print(f"[Receiver] Frame {frame} lost!")
            continue

        if frame == expected_frame:
            print(f"[Receiver] Received Frame {frame}")
            ack_msg = f"ACK {frame}"
            receiver_socket.sendto(ack_msg.encode(), addr)
            expected_frame += 1
        else:
            print(f"[Receiver] Out of order frame {frame}, discarding...")
            ack_msg = f"ACK {expected_frame - 1}"
            receiver_socket.sendto(ack_msg.encode(), addr)


if __name__ == "__main__":
    receiver()
