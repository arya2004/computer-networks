import numpy as np
import matplotlib.pyplot as plt
from abc import ABC, abstractmethod, ABCMeta


class Encode(ABC):
    """
    This class serves as a base class for encoders
    and draws the encoding graph

    Args:
        data_stream (str): Original data bits.
    """

    __metaclass__ = ABCMeta

    def __init__(self, data_stream):
        # Ensure that data_stream contains only '0' and '1'. Raises ValueError otherwise.
        if any(bit not in ("0", "1") for bit in data_stream):
            raise ValueError("Data must be a binary strings cntaining '0' & '1' only")
        self.data_stream = data_stream

    @abstractmethod
    def encode(self):  # Abstract method destined to be override by subclass
        pass

    def plot(self, encoded_signal, title):  # draws a step plot of the encoded signal
        time = np.arange(0, len(encoded_signal), 1)
        plt.figure(figsize=(10, 4))
        plt.step(time, encoded_signal, where="post", color="red")
        plt.title(title)
        plt.xlabel("Bit Period")
        plt.ylabel("Voltage Period")
        plt.yticks([-1, 0, 1])
        plt.grid(True)
        plt.show()


class UnipolarNRZEncoder(Encode):
    """
    This class implements the Unipolar NRZ Encoding.
    It inherits from the base class Encode and overide the encode method
    Args:
        data_stream (str): Original data bits.
    Returns:
        signal (list[int]): Containing encoded bits.
    """

    def encode(self):
        return [1 if bit == "1" else 0 for bit in self.data_stream]


class BiPolarNRZLEncoder(Encode):
    """
    This class implements the Bipolar NRZ Encoding.
    It inherits from the base class Encode and overide the encode method
    Args:
        data_stream (str): Original data bits.
    Returns:
        signal (list[int]): Containing encoded bits.

    """

    def encode(self):
        return [1 if bit == "1" else -1 for bit in self.data_stream]


class BiPolarNRZIEncoder(Encode):
    """
    This class implement the BiPolar NRZ Encoding.
    It inherits from the base class Encode and overide the encode method
    Args:
        data_stream (str): Original data bits.
    Returns:
        signal (list[int]): Containing encoded bits.
    """

    def encode(self):
        signal = []
        prev_level = 1  # Setting the level to +1 from start
        for bit in self.data_stream:
            if bit == "1":  # Inverting the level if the bit is 1
                prev_level *= -1
            signal.append(prev_level)
        return signal


class PolarNRZ(Encode):
    """
    This class implement the Polar NRZ Encoding.
    It inherits from the base class Encode and overide the encode method
    Args:
        data_stream (str): Original data bits.
    Returns:
        signal (list[int]): Containing encoded bits.
    """

    def encode(self):
        signal = []
        for bit in self.data_stream:
            # If bit is 1: signal goes high for first half of bit period, then returns to 0
            if bit == "1":
                signal.extend([1, 0])
            # If bit is 0: signal goes low for first half, then returns to 0
            else:
                signal.extend([-1, 0])
        return signal


class BiPolarAMI(Encode):
    """
    This class implement the BiPolar AMI Encoding.
    It inherits from the base class Encode and overide the encode method
    Args:
        data_stream (str): Original data bits.
    Returns:
        signal (list[int]): Containing encoded bits.
    """

    def encode(self):
        signal = []
        current_level = 1
        for bit in self.data_stream:
            if bit == "1":
                signal.append(current_level)
                current_level *= -1  # Inverting level between high(+1) and low (-1) at each 1 Bit
            else:
                signal.append(0)

        return signal


class PseudoTernary(Encode):
    """
    This class implement the Pseudo-Ternary Encoding.
    It inherits from the base class Encode and overide the encode method
    Args:
        data_stream (str): Original data bits.
    Returns:
        signal (list[int]): Containing encoded bits.
    """

    def encode(self):
        signal = []
        current_level = 1
        for bit in self.data_stream:
            if bit == "0":
                signal.append(current_level)
                current_level *= -1 # Inverting level between high(+1) and low (-1) at each 0 Bit
            else:
                signal.append(0)
        return signal


class MLT3(Encode):
    def encode(self):
        """
        This class implement the M.L.T-3 Encoding.
        It inherits from the base class Encode and overide the encode method
        Args:
            data_stream (str): Original data bits.
        Returns:
            signal (list[int]): Containing encoded bits.

        """
        signal = []
        levels = [0, 1, 0, -1]  # Indicate the three levels sequence each 1 bit passed through
        index = 0
        for bit in self.data_stream:
            if bit == "1":
                index = (index + 1) % len(levels)  # index of the next level sequence
            signal.append(levels[index])
        return signal


class ManchesterNRZEncoder(Encode):
    """This class implement the Manchester NRZ Encoding.
    It inherits from the base class Encode and overide the encode method
    Args:
        data_stream (str): Original data bits.
    Returns:
        signal (list[int]): Containing encoded bits."""

    def encode(self):
        signal = []
        for bit in self.data_stream:
            if bit == "1":
                signal.extend([1, 0])  # Going from high to low level in first half
            elif bit == "0":
                signal.extend([0, 1])  # Going from low to high level in first half
        return signal


class DifferentialManchesterEncoder(Encode):
    """
    This class implement the Differential Manchester NRZ Encoding.
      It inherits from the base class Encode and overide the encode method
      Args:
          data_stream (str): Original data bits.
      Returns:
          signal (list[int]): Containing encoded bits.
    """

    def encode(self):
        signal = []
        prev_level = 1
        for bit in self.data_stream:
            # If bit is 0, invert the level at the start of the bit period
            if bit == "0":
                prev_level *= -1
            # Append the two halves of the differential Manchester signal
            signal.extend([prev_level, -prev_level])
            prev_level = -prev_level  # Update prev_level for next bit
        return signal


def main():
    print("Welcome to my encoding script")
    while True:
        try:
            # Getting data_streams from user
            data = input("Enter binary data (e.g: 1011100): ").strip()

            print("Choose encoding type : \n")
            print("0. Exit script")
            print("1. Unipolar NRZ")
            print("2. Polar NRZ")
            print("3. Manchester NRZ")
            print("4. BiPolar NRZL")
            print("5. BiPolar NRZI")
            print("6. BiPolar AMI")
            print("7. Pseudo Ternary")
            print("8. MLT3")
            print("9. Differential Manchester")

            choice = input("Enter your choice (1-9): ").strip()

            encoders = {
                "1": UnipolarNRZEncoder,
                "2": PolarNRZ,
                "3": ManchesterNRZEncoder,
                "4": BiPolarNRZLEncoder,
                "5": BiPolarNRZIEncoder,
                "6": BiPolarAMI,
                "7": PseudoTernary,
                "8": MLT3,
                "9": DifferentialManchesterEncoder,
            }

            if choice == "0":
                print("exiting...\n")
                print("bye!")

            # Printing an invalid request message
            if choice not in encoders:
                print("Invalid request")
                return

            # encoding the data_stream signal
            encoder = encoders[choice](data)
            encoder_signal = encoder.encode()

            print(f"Encoded signal: {encoder_signal}")
            # drawing the encoding signal graph
            encoder.plot(
                encoder_signal, f"Encoding signal {encoder.__class__.__name__}"
            )
        except ValueError as e:
            print(f"Error: {e}")


if __name__ == "__main__":
    main()
