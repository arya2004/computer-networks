use std::io::{self, prelude::*};
use std::net::TcpStream;

fn main() -> io::Result<()> {
    let host_ip: &str = "127.0.0.1";                            // ip to connect to
    let host_port: u16 = 12345;                                 // port to connect to
    let full_address: (&str, u16) = (host_ip, host_port);       // combine ip and port
    let mut response_buffer: [u8; 128] = [0; 128];              // assign response buffer

    // estabilish connection and print it's addresss
    let mut stream = TcpStream::connect(full_address)?;
    println!("Connected to server on {}:{}", host_ip, host_port);

    // sending loop
    loop {
        let mut input: String = String::new();      // assign space for message
        println!("Type in message to send:");
        io::stdout().flush()?;                      // make sure the console prompt appears
        io::stdin().read_line(&mut input)?;         // read input from user
        let msg: &str = input.trim();

        // if user types 'exit' the script will close
        if msg.eq_ignore_ascii_case("exit") {
            println!("Disconnecting...");
            break;
        }

        stream.write_all(msg.as_bytes())?;     // send message to server

        // read server's response
        let bytes_read: usize = stream.read(&mut response_buffer)?; 
        // decode response and print it out
        let response: String = String::from_utf8_lossy(&response_buffer[..bytes_read]).to_string();
        println!("Server's response: {}", response);
    }

    Ok(())
}