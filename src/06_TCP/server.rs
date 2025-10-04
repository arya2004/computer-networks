
use std::io::{Result, Read, Write};
use std::net::{TcpListener, TcpStream, SocketAddr};
use std::thread;

// this function handles a client
// it mainly exists to make the code cleaner
// it accepts a TcpStream as it's only parameter
fn handle_client(mut stream: TcpStream) -> () {
    // get client's ip address
    let client_address: SocketAddr = stream.peer_addr().unwrap();
    println!("Client connection from {}", client_address);
    // assign a buffer for read
    let mut buffer: [u8; 512] = [0; 512];

    // listen loop
    loop {
        // read the message
        let read_result: Result<usize> = stream.read(&mut buffer);

        match read_result {
            // in case of client disconnecting
            Ok(0) => {
                println!("Client {} disconnected", client_address);
                break;
            }
            // in case of receiving a message
            Ok(n) => {
                // decode the message and print it out
                let msg: String = String::from_utf8_lossy(&buffer[..n]).to_string();
                println!("Client {} says:\r\n{}", client_address, msg.trim());
                
                // and send a response to client
                let _ = stream.write_all(b"Message received");
            }
            // in case of error print it out
            Err(ref e) => {
                println!("Error during read from client {}:\r\n{}", client_address, e)
            }
        }
    }
}
fn main() -> Result<()> {
    let ip: &str = "127.0.0.1";             // ip to listen on
    let port: u16 = 12345;                  // port to listen on
    let address: (&str, u16) = (ip, port);  // combine into address

    // setup server
    let listener: TcpListener = TcpListener::bind(address).unwrap();
    println!("listening started, ready to accept");

    // listen loop
    for stream in listener.incoming() {
        match stream {
            Ok(ok_stream) => {                 // if connection is ok
                thread::spawn(|| {             // create a new thread
                    handle_client(ok_stream);  // that handles the client            
                });
            }
            Err(e) => {    // in case of error print it out
                println!("Connection failed:\r\n{e}");
            }
        }
    };

    Ok(())
}