"""
Hamming Code implementation in Python.
This script generates a Hamming codeword, allows introducing an error,
and detects/corrects it using the Hamming Code algorithm.
"""

import math


def generate_parity_bit(data, power):
    """
    Generate parity bit for a given power (position of parity bit).
    Args:
        data (list[int]): List containing data bits and parity placeholders.
        power (int): The position of the parity bit.
    Returns:
        int: The calculated parity bit (0 or 1).
    """
    parity_bit = 0
    for j in range(len(data)):
        if data[j] != 2:  # 2 indicates a parity placeholder
            k = j + 1
            binary_position = bin(k)[2:]  # binary string of index
            z = (int(binary_position) // (10 ** power)) % 10
            if z == 1 and data[j] == 1:
                parity_bit = (parity_bit + 1) % 2
    return parity_bit


def generate_codeword(data_word):
    """
    Generate the Hamming codeword by inserting parity bits.
    Args:
        data_word (list[int]): Original data bits.
    Returns:
        list[int]: Codeword including data and parity bits.
    """
    data_length = len(data_word)
    parity_count = 0

    # Find required number of parity bits
    while (2 ** parity_count) < (data_length + parity_count + 1):
        parity_count += 1

    codeword = [0] * (data_length + parity_count)
    j = 0
    k = 0

    # Insert data bits and parity placeholders (2)
    for position in range(1, len(codeword) + 1):
        if position == 2 ** j:
            codeword[position - 1] = 2
            j += 1
        else:
            codeword[position - 1] = data_word[k]
            k += 1

    # Calculate parity bits
    for position in range(parity_count):
        codeword[(2 ** position) - 1] = generate_parity_bit(codeword, position)

    return codeword


def received_code(codeword, parity_count):
    """
    Detect and correct errors in received Hamming code.
    Args:
        codeword (list[int]): Received codeword (possibly with error).
        parity_count (int): Number of parity bits.
    """
    parity = [0] * parity_count
    syndrome = ""

    # Verify parity bits
    for power in range(parity_count):
        for i in range(len(codeword)):
            k = i + 1
            binary_pos = bin(k)[2:]
            bit = (int(binary_pos) // (10 ** power)) % 10
            if bit == 1 and codeword[i] == 1:
                parity[power] = (parity[power] + 1) % 2
        syndrome = str(parity[power]) + syndrome

    error_location = int(syndrome, 2)
    if error_location != 0:
        print(f"\nError detected at position {error_location}")
        codeword[error_location - 1] = (codeword[error_location - 1] + 1) % 2
        print("Error corrected. New codeword:", "".join(map(str, reversed(codeword))))
    else:
        print("\nNo errors detected in the received codeword.")

    # Extract original data
    print("The original data was:", end=" ")
    power = parity_count - 1
    for i in range(len(codeword), 0, -1):
        if 2 ** power != i:
            print(codeword[i - 1], end="")
        else:
            power -= 1
    print()


def main():
    # Example data word (reversed for calculation, like Java version)
    data_word = [1, 0, 0, 1, 1, 0, 1]

    print("Original data word (in reverse):", "".join(map(str, reversed(data_word))))

    # Generate Hamming codeword
    codeword = generate_codeword(data_word)
    print("Generated Hamming codeword:", "".join(map(str, reversed(codeword))))

    # Introduce an error
    try:
        error_position = int(input("Enter the position to introduce error (0 for no error): "))
    except ValueError:
        print("Invalid input. Exiting.")
        return

    if error_position != 0 and error_position <= len(codeword):
        codeword[error_position - 1] = (codeword[error_position - 1] + 1) % 2
    elif error_position > len(codeword):
        print("Invalid position. Exiting.")
        return

    print("Sent codeword:", "".join(map(str, reversed(codeword))))

    # Detect and correct error
    received_code(codeword, len(codeword) - len(data_word))


if __name__ == "__main__":
    main()
