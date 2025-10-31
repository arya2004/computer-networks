"""Python implementation of the Go-Back-N Sender Protocol."""

import socket
import time

HOST = "127.0.0.1"
PORT = 8080


def sender():
    """Simulate Go-Back-N sender behavior."""
    sender_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    sender_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

    window_size = 4
    base = 0
    next_seq_num = 0
    total_frames = 10

    print(f"[SENDER] Sending {total_frames} frames with window size = {window_size}")

    try:
        while base < total_frames:
            # Send frames within window
            while next_seq_num < base + window_size and next_seq_num < total_frames:
                message = f"Frame {next_seq_num}"
                sender_socket.sendto(message.encode(), (HOST, PORT))
                print(f"[SENDER] Sent: {message}")
                next_seq_num += 1
                time.sleep(0.5)

            # Wait for ACK
            sender_socket.settimeout(3)
            try:
                ack, _ = sender_socket.recvfrom(1024)
                ack_num = int(ack.decode().split()[1])
                print(f"[SENDER] Received: {ack.decode()}")
                base = ack_num + 1
            except socket.timeout:
                print("[SENDER] Timeout! Resending window...")
                next_seq_num = base

    except KeyboardInterrupt:
        print("\n[SENDER] Interrupted. Shutting down...")
    finally:
        sender_socket.close()


if __name__ == "__main__":
    sender()


