# 09_HTTP_Packet (Python)

This folder contains a Python implementation demonstrating how HTTP requests and responses look at the socket / packet level.

Files:
- server.py     : simple multithreaded HTTP server that parses raw requests and serves files (or dynamic response).
- client.py     : minimal HTTP client that crafts raw HTTP requests and prints the raw response.
- utils.py      : small helpers for parsing/building HTTP messages.
- www/          : optional folder created automatically to hold static files.

Run:
1. Start server:
   python3 src/09_HTTP_Packet/server.py

   Server listens by default on 0.0.0.0:8080

2. In another terminal, run client:
   python3 src/09_HTTP_Packet/client.py

   Or use curl:
   curl http://localhost:8080/
