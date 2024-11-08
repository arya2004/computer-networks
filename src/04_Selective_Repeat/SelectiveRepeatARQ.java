
import java.util.*;


public class SelectiveRepeatARQ {
    public static void main(String[] args) {
        Receiver receiver = new Receiver(null);
        Sender sender = new Sender(receiver);
        receiver = new Receiver(sender);
        sender.sendPackets();
    }
}


class Packet {
    int sequenceNumber;
    String data;

    Packet(int sequenceNumber, String data) {
        this.sequenceNumber = sequenceNumber;
        this.data = data;
    }
}

class Sender {
    private static final int WINDOW_SIZE = 4;
    private static final int TOTAL_PACKETS = 10;
    private static final double LOSS_PROBABILITY = 0.2;
    private Receiver receiver;
    private Set<Integer> packetsToResend;
    private Random random;

    Sender(Receiver receiver) {
        this.receiver = receiver;
        this.packetsToResend = new HashSet<>();
        this.random = new Random();
    }

    void sendPackets() {
        int base = 0;
        while (base < TOTAL_PACKETS) {
            int windowEnd = Math.min(base + WINDOW_SIZE, TOTAL_PACKETS);
            for (int i = base; i < windowEnd; i++) {
                Packet packet = new Packet(i, "Data " + i);
                if (random.nextDouble() > LOSS_PROBABILITY) {
                    System.out.println("Sender: Sending packet " + packet.sequenceNumber);
                    receiver.receivePacket(packet);
                } else {
                    System.out.println("Sender: Packet " + packet.sequenceNumber + " lost");
                    packetsToResend.add(packet.sequenceNumber);
                }
            }
            resendPackets();
            base += WINDOW_SIZE;
        }
    }

    void resendPackets() {
        for (int seqNum : packetsToResend) {
            Packet packet = new Packet(seqNum, "Data " + seqNum);
            System.out.println("Sender: Resending packet " + packet.sequenceNumber);
            receiver.receivePacket(packet);
        }
        packetsToResend.clear();
    }
}

class Receiver {
    private static final int WINDOW_SIZE = 4;
    private Set<Integer> receivedPackets;
    private Sender sender;

    Receiver(Sender sender) {
        this.sender = sender;
        this.receivedPackets = new HashSet<>();
    }

    void receivePacket(Packet packet) {
        if (packet.sequenceNumber >= 0 && packet.sequenceNumber < WINDOW_SIZE) {
            System.out.println("Receiver: Received packet " + packet.sequenceNumber);
            receivedPackets.add(packet.sequenceNumber);
            sendAcknowledgment(packet.sequenceNumber);
        } else {
            System.out.println("Receiver: Out of window packet " + packet.sequenceNumber);
        }
    }

    private void sendAcknowledgment(int packetNumber) {
        System.out.println("Receiver: Sending ACK for packet " + packetNumber);
  
    }

    void requestResend(int packetNumber) {
        if (!receivedPackets.contains(packetNumber)) {
            System.out.println("Receiver: Requesting retransmission for packet " + packetNumber);
            sender.sendPackets(); 
        }
    }
}
