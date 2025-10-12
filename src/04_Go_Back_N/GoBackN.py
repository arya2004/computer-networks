import threading
import time
import random

# Example Go-Back-N parameters
WINDOW_SIZE = 4
TOTAL_FRAMES = 10

class GoBackN:
    def __init__(self):
        self.base = 0
        self.next_seq = 0
        self.lock = threading.Lock()

    def send_frame(self, frame_id):
        print(f"Sending frame {frame_id}")
        # simulate transmission delay
        time.sleep(random.uniform(0.1, 0.5))
        if random.random() < 0.8:
            print(f"Ack received for frame {frame_id}")
            self.base += 1
        else:
            print(f"Frame {frame_id} lost. Retransmitting from {self.base}")

    def run(self):
        while self.base < TOTAL_FRAMES:
            self.lock.acquire()
            while self.next_seq < self.base + WINDOW_SIZE and self.next_seq < TOTAL_FRAMES:
                threading.Thread(target=self.send_frame, args=(self.next_seq,)).start()
                self.next_seq += 1
            self.lock.release()
            time.sleep(1)

if __name__ == "__main__":
    gbn = GoBackN()
    gbn.run()
