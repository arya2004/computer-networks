import socket

def has_precedence(op1, op2):
    """Check if op2 has higher or equal precedence compared to op1."""
    if op2 in ('(', ')'):
        return False
    if (op1 in ('*', '/') and op2 in ('+', '-')):
        return False
    return True

def apply_operation(op, b, a):
    """Apply an arithmetic operation."""
    if op == '+':
        return a + b
    elif op == '-':
        return a - b
    elif op == '*':
        return a * b
    elif op == '/':
        if b == 0:
            raise ZeroDivisionError("Cannot divide by zero")
        return a / b
    return 0

def evaluate_expression(expression):
    """Evaluate mathematical expressions safely using stacks."""
    expression = expression.replace(" ", "")
    numbers = []
    operators = []
    i = 0
    while i < len(expression):
        ch = expression[i]

        if ch.isdigit() or ch == '.':
            num_str = []
            while i < len(expression) and (expression[i].isdigit() or expression[i] == '.'):
                num_str.append(expression[i])
                i += 1
            numbers.append(float("".join(num_str)))
            continue
        elif ch == '(':
            operators.append(ch)
        elif ch == ')':
            while operators and operators[-1] != '(':
                numbers.append(apply_operation(operators.pop(), numbers.pop(), numbers.pop()))
            operators.pop()
        elif ch in "+-*/":
            while operators and has_precedence(ch, operators[-1]):
                numbers.append(apply_operation(operators.pop(), numbers.pop(), numbers.pop()))
            operators.append(ch)
        i += 1

    while operators:
        numbers.append(apply_operation(operators.pop(), numbers.pop(), numbers.pop()))

    return numbers.pop()

def start_server():
    """Start the calculator server."""
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(("localhost", 5000))
    server_socket.listen(1)
    print("Server is running on port 5000...")

    conn, addr = server_socket.accept()
    print(f"Client connected from {addr}")

    with conn:
        while True:
            data = conn.recv(1024).decode()
            if not data:
                break
            print(f"Received expression: {data}")
            try:
                result = str(evaluate_expression(data))
            except Exception as e:
                result = f"Error: {str(e)}"
            conn.sendall(result.encode())

if __name__ == "__main__":
    start_server()
