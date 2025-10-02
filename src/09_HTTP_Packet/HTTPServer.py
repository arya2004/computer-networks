from http.server import BaseHTTPRequestHandler, HTTPServer

class SimpleHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        # Send response status code
        self.send_response(200)

        # Send headers
        self.send_header('Content-type', 'text/html')
        self.end_headers()

        # Write content
        self.wfile.write(b"Hello, this is a basic HTTP server!")

def run(server_class=HTTPServer, handler_class=SimpleHandler):
    server_address = ('', 8000)  # Listen on all interfaces, port 8000
    httpd = server_class(server_address, handler_class)
    print("Starting HTTP server on port 8000...")
    httpd.serve_forever()

if __name__ == "__main__":
    run()
