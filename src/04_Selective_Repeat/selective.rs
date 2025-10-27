

} else {
println!("[Receiver] Duplicate ACK for {} -> transmitted", ack_seq);
out.push(ChannelMsg::Ack(ack_seq));
}
}
}


out
}
}


fn main() {
// Simulation parameters (adjust as needed)
let total_packets = 20;
let window_size = 4;
let timeout_ticks = 6; // ticks before retransmit
let loss_data_prob = 0.15; // probability that a data packet is lost
let loss_ack_prob = 0.10; // probability that an ACK is lost
let mut tick_limit = 1000; // safety limit


println!("Selective Repeat simulation: total_packets={}, window_size={}, timeout={}, loss_data={:.2}, loss_ack={:.2}",
total_packets, window_size, timeout_ticks, loss_data_prob, loss_ack_prob);


let sender_config = SenderConfig {
window_size,
timeout: timeout_ticks,
max_seq: 256,
};


let mut sender = Sender::new(sender_config, total_packets, 0x12345678);
let mut receiver = Receiver::new(window_size, total_packets, 0x87654321);


// Channel: simple model with latency of 1 tick: messages sent this tick arrive next tick.
let mut channel_next: Vec<ChannelMsg> = Vec::new();


let mut tick: usize = 0;


while tick < tick_limit {
println!("\n=== Tick {} ===", tick);


// 1) Sender attempts to send new packets (within window)
let mut sent_msgs = sender.tick_send(loss_data_prob);
// Also get retransmissions due to timeout
let mut retrans = sender.tick_timers(loss_data_prob);
sent_msgs.append(&mut retrans);


// All messages sent this tick will arrive next tick
for m in sent_msgs {
channel_next.push(m);
}


// 2) Deliver messages that were sent previous tick
let mut arriving = std::mem::take(&mut channel_next);


// We'll collect ACKs generated this tick and they will arrive next tick as well
let mut next_round: Vec<ChannelMsg> = Vec::new();


for msg in arriving.drain(..) {
match msg {
ChannelMsg::Data(pkt) => {
// Receiver processes data and may generate ACKs
let acks = receiver.recv_data(pkt, loss_ack_prob);
next_round.extend(acks);
}
ChannelMsg::Ack(seq) => {
// Sender processes ACK immediately (we model ACKs arriving this tick)
sender.recv_ack(seq);
}
}
}


// Messages produced this tick (ACKs) will be put on channel to arrive next tick
channel_next.append(&mut next_round);


// Check termination
if sender.all_acked() {
println!("\nAll packets successfully acknowledged by tick {}. Simulation complete.", tick);
break;
}


tick += 1;
}


if tick >= tick_limit {
println!("Reached tick limit {} without finishing.", tick_limit);
}


// Summary
let acked = sender.delivered_acks.iter().filter(|&&v| v).count();
println!("\nSummary: {} / {} packets ACKed", acked, total_packets);
}
