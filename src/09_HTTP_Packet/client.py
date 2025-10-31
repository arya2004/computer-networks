#!/usr/bin/env python3
"""
Minimal HTTP client that crafts a raw HTTP/1.1 request and prints the raw response.
Used to demonstrate the packet-level view of HTTP.
"""

import socket

HOST = "127.0.0.1"
PORT = 8080
RECV_BUFFER = 4096


def build_raw_get(path="/", host="localhost", extra_headers=None) -> bytes:
    if extra_headers is None:
        extra_headers = {}
    request_line = f"GET {path} HTTP/1.1\r\n"
    headers = {"Host": host, "User-Agent": "python-http-packet-demo/1.0", "Accept": "*/*", **extra_headers}
    header_lines = "".join(f"{k}: {v}\r\n" for k, v in headers.items())
    raw = (request_line + header_lines + "\r\n").encode("iso-8859-1")
    return raw


def run_client(host=HOST, port=PORT):
    raw_request = build_raw_get("/", host=host)
    print("===== RAW REQUEST =====")
    print(raw_request.decode("iso-8859-1"))
    with socket.create_connection((host, port), timeout=5) as s:
        s.sendall(raw_request)
        data = b""
        while True:
            chunk = s.recv(RECV_BUFFER)
            if not chunk:
                break
            data += chunk

    print("===== RAW RESPONSE (first 2000 bytes) =====")
    print(data[:2000])
    print("\n===== RAW RESPONSE (decoded) =====")
    try:
        print(data.decode("utf-8"))
    except UnicodeDecodeError:
        print(data.decode("iso-8859-1"))


if __name__ == "__main__":
    run_client()
