"""Python implementation of Go-Back-N Sender.
Simulates reliable frame transmission using UDP sockets and a sliding window.
"""

import socket
import time  # Used for timeout simulation

SERVER_IP = "127.0.0.1"
SERVER_PORT = 5001
WINDOW_SIZE = 4
TOTAL_FRAMES = 10
TIMEOUT = 2  # seconds


def send_frames():
    """Send frames to the receiver using Go-Back-N ARQ logic."""
    sender_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sender_socket.settimeout(TIMEOUT)

    base = 0
    next_seq_num = 0

    print("[Sender] Starting transmission...")

    while base < TOTAL_FRAMES:
        # Send frames within window
        while next_seq_num < base + WINDOW_SIZE and next_seq_num < TOTAL_FRAMES:
            msg = f"Frame {next_seq_num}"
            sender_socket.sendto(msg.encode(), (SERVER_IP, SERVER_PORT))
            print(f"[Sender] Sent: {msg}")
            next_seq_num += 1

        try:
            ack, _ = sender_socket.recvfrom(1024)
            ack_num = int(ack.decode().split()[1])
            print(f"[Sender] Received ACK: {ack_num}")
            base = ack_num + 1
        except socket.timeout:
            print("[Sender] Timeout! Resending window...")
            next_seq_num = base

        # Slight delay to simulate transmission timing
        time.sleep(0.5)

    print("[Sender] All frames transmitted successfully.")
    sender_socket.close()


if __name__ == "__main__":
    send_frames()
