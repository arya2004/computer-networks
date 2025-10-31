#!/usr/bin/env python3
"""
Simple multithreaded HTTP server that parses raw HTTP requests and
returns a handcrafted HTTP response. Educational: shows request contents.
"""

import socket
import threading
import os
from utils import parse_http_request, build_http_response

HOST = "0.0.0.0"
PORT = 8080
BASE_DIR = os.path.dirname(__file__)
WWW_ROOT = os.path.join(BASE_DIR, "www")
RECV_BUFFER = 4096


def handle_client(conn: socket.socket, addr):
    try:
        print(f"[connection] {addr} connected")
        data = b""
        conn.settimeout(1.0)
        while True:
            try:
                chunk = conn.recv(RECV_BUFFER)
            except socket.timeout:
                break
            if not chunk:
                break
            data += chunk
            if b"\r\n\r\n" in data:
                method, path, version, headers, body = parse_http_request(data)
                if "Content-Length" in headers:
                    try:
                        expected = int(headers["Content-Length"])
                    except Exception:
                        expected = 0
                    if len(body) < expected:
                        continue
                break

        if not data:
            return

        method, path, version, headers, body = parse_http_request(data)
        print(f">>> Request: {method} {path} {version}")
        for k, v in headers.items():
            print(f"> {k}: {v}")
        if body:
            print(f"> body ({len(body)} bytes): {body[:200]!r}")

        # Serve static file if exists
        if path == "/":
            path = "/index.html"
        target = os.path.normpath(os.path.join(WWW_ROOT, path.lstrip("/")))
        if target.startswith(WWW_ROOT) and os.path.isfile(target):
            with open(target, "rb") as f:
                file_bytes = f.read()
            content_type = "text/html; charset=utf-8" if target.endswith((".html", ".htm")) else "application/octet-stream"
            response = build_http_response(200, "OK", headers={"Content-Type": content_type}, body=file_bytes)
        else:
            html = "<html><body>"
            html += f"<h1>Simple HTTP Packet Demo (Python)</h1>"
            html += f"<p><strong>Request:</strong> {method} {path} {version}</p>"
            html += "<h2>Headers</h2><ul>"
            for k, v in headers.items():
                html += f"<li><strong>{k}</strong>: {v}</li>"
            html += "</ul>"
            if body:
                try:
                    preview = body.decode("utf-8", errors="replace")
                except Exception:
                    preview = repr(body)
                html += "<h2>Body</h2><pre>" + preview + "</pre>"
            html += "</body></html>"
            response = build_http_response(200, "OK", body=html.encode("utf-8"))

        conn.sendall(response)
    except Exception as e:
        print("[error]", e)
        try:
            resp = build_http_response(500, "Internal Server Error",
                                       body=f"<html><body><h1>500</h1><pre>{e}</pre></body></html>".encode("utf-8"))
            conn.sendall(resp)
        except Exception:
            pass
    finally:
        try:
            conn.shutdown(socket.SHUT_RDWR)
        except Exception:
            pass
        conn.close()
        print(f"[connection] {addr} closed")


def run_server(host=HOST, port=PORT):
    os.makedirs(WWW_ROOT, exist_ok=True)
    index_path = os.path.join(WWW_ROOT, "index.html")
    if not os.path.exists(index_path):
        with open(index_path, "w", encoding="utf-8") as f:
            f.write("<html><body><h1>Welcome to Python HTTP Packet Demo</h1>"
                    "<p>Try the client (client.py) or curl.</p></body></html>")

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        s.bind((host, port))
        s.listen(50)
        print(f"[server] Listening on {host}:{port}. Serving files from {WWW_ROOT}")
        try:
            while True:
                conn, addr = s.accept()
                t = threading.Thread(target=handle_client, args=(conn, addr), daemon=True)
                t.start()
        except KeyboardInterrupt:
            print("\n[server] KeyboardInterrupt — shutting down.")


if __name__ == "__main__":
    run_server()
