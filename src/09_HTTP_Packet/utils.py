"""
Small HTTP utilities for parsing/building raw HTTP messages.
"""

from typing import Tuple, Dict


def parse_http_request(raw: bytes) -> Tuple[str, str, str, Dict[str, str], bytes]:
    """
    Parse a raw HTTP request (bytes) into:
    - method (e.g., 'GET')
    - path (e.g., '/index.html')
    - version (e.g., 'HTTP/1.1')
    - headers (dict)
    - body (bytes)
    """
    try:
        header_part, body = raw.split(b"\r\n\r\n", 1)
    except ValueError:
        header_part = raw
        body = b""

    lines = header_part.split(b"\r\n")
    if not lines:
        return "", "", "", {}, b""

    request_line = lines[0].decode("iso-8859-1")
    parts = request_line.split(" ", 2)
    if len(parts) != 3:
        # Malformed request-line fallback
        method = parts[0] if parts else ""
        path = parts[1] if len(parts) > 1 else ""
        version = parts[2] if len(parts) > 2 else ""
    else:
        method, path, version = parts

    headers = {}
    for h in lines[1:]:
        if not h:
            continue
        parts = h.decode("iso-8859-1").split(":", 1)
        if len(parts) == 2:
            name, value = parts
            headers[name.strip()] = value.strip()

    return method, path, version, headers, body


def build_http_response(status_code: int = 200,
                        reason: str = "OK",
                        headers: Dict[str, str] = None,
                        body: bytes = b"") -> bytes:
    """
    Build a raw HTTP response bytes object given status, headers and body.
    """
    if headers is None:
        headers = {}
    if body is None:
        body = b""
    if isinstance(body, str):
        body = body.encode("utf-8")

    headers_out = {k: v for k, v in headers.items()}

    # normalize keys for checks (simple)
    norm_keys = {k.lower(): k for k in headers_out.keys()}

    if "content-length" not in norm_keys:
        headers_out["Content-Length"] = str(len(body))
    if "connection" not in norm_keys:
        headers_out["Connection"] = "close"
    if "content-type" not in norm_keys:
        headers_out["Content-Type"] = "text/html; charset=utf-8"

    status_line = f"HTTP/1.1 {status_code} {reason}\r\n"
    header_lines = "".join(f"{k}: {v}\r\n" for k, v in headers_out.items())
    return (status_line + header_lines + "\r\n").encode("iso-8859-1") + body
