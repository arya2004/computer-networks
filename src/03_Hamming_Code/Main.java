import java.util.Scanner;

/**
 * This class provides methods to generate Hamming code, introduce errors, and
 * detect and correct them using the Hamming Code algorithm.
 */
class HammingCode {

    /**
     * Generates a parity bit for a given power (position of parity bit) based on the data.
     *
     * @param data  The data array which includes both data bits and parity bits
     * @param power The position of the parity bit in the Hamming code
     * @return The calculated parity bit (0 or 1)
     */
    static int generateParityBit(int data[], int power) {
        int parityBit = 0;
        for (int j = 0; j < data.length; j++) {
            if (data[j] != 2) { // 2 indicates a parity bit position
                int k = j + 1;
                String binaryPosition = Integer.toBinaryString(k);
                int z = ((Integer.parseInt(binaryPosition)) / ((int) Math.pow(10, power))) % 10;
                if (z == 1 && data[j] == 1) {
                    parityBit = (parityBit + 1) % 2; // Modulo-2 addition for parity bit
                }
            }
        }
        return parityBit;
    }

    /**
     * Generates a Hamming codeword by inserting parity bits into the original data.
     *
     * @param _data The original data array (data word)
     * @return The Hamming codeword (including data and parity bits)
     */
    static int[] generateCodeWord(int _data[]) {
        int dataLength = _data.length;
        int parityCount = 0, j = 0, k = 0;

        // Calculate how many parity bits are needed
        while (Math.pow(2, parityCount) < dataLength + parityCount + 1) {
            parityCount++;
        }

        int[] codeWord = new int[dataLength + parityCount]; // Codeword with parity bits

        // Initialize the codeword with data bits and '2' in parity bit positions
        for (int position = 1; position <= codeWord.length; position++) {
            if (Math.pow(2, j) == position) {
                codeWord[position - 1] = 2; // Insert placeholder for parity bit
                j++;
            } else {
                codeWord[position - 1] = _data[k++]; // Insert data bit
            }
        }

        // Calculate and insert the parity bits
        for (int position = 0; position < parityCount; position++) {
            codeWord[((int) Math.pow(2, position)) - 1] = generateParityBit(codeWord, position);
        }

        return codeWord;
    }

    /**
     * Checks the received Hamming code for errors and corrects them if any.
     *
     * @param codeword   The received codeword with potential errors
     * @param parityCount The number of parity bits in the codeword
     */
    static void receivedCode(int codeword[], int parityCount) {
        int[] parity = new int[parityCount]; // Array to store parity checks
        String syndrome = "";

        // Verify the parity bits
        for (int power = 0; power < parityCount; power++) {
            for (int i = 0; i < codeword.length; i++) {
                int k = i + 1;
                String s = Integer.toBinaryString(k);
                int bit = ((Integer.parseInt(s)) / ((int) Math.pow(10, power))) % 10;
                if (bit == 1 && codeword[i] == 1) {
                    parity[power] = (parity[power] + 1) % 2; // Modulo-2 addition for parity check
                }
            }
            syndrome = parity[power] + syndrome; // Build the syndrome string
        }

        // Check if there's an error
        int errorLocation = Integer.parseInt(syndrome, 2); // Convert syndrome to integer
        if (errorLocation != 0) {
            System.out.println("Error detected at position " + errorLocation);
            codeword[errorLocation - 1] = (codeword[errorLocation - 1] + 1) % 2; // Correct the error
            System.out.println("Error corrected. New codeword:");
            for (int i = codeword.length - 1; i >= 0; i--) {
                System.out.print(codeword[i]);
            }
            System.out.println();
        } else {
            System.out.println("No errors detected in the received codeword.");
        }

        // Extract the original data word
        System.out.println("The original data was:");
        int power = parityCount - 1;
        for (int i = codeword.length; i > 0; i--) {
            if (Math.pow(2, power) != i) {
                System.out.print(codeword[i - 1]);
            } else {
                power--;
            }
        }
        System.out.println();
    }
}

public class Main {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        // Sample data word (reverse sequence of actual data for easier calculation)
        int[] dataWord = new int[]{1, 0, 0, 1, 1, 0, 1};

        System.out.println("Original data word (in reverse):");
        for (int i = dataWord.length - 1; i >= 0; i--) {
            System.out.print(dataWord[i]);
        }
        System.out.println();

        // Generate the Hamming codeword
        int[] codeWord = HammingCode.generateCodeWord(dataWord);

        System.out.println("Generated Hamming codeword:");
        for (int i = codeWord.length - 1; i >= 0; i--) {
            System.out.print(codeWord[i]);
        }
        System.out.println();

        // Allow user to introduce an error in the codeword
        System.out.println("Enter the position of the bit to introduce an error (0 for no error):");
        int errorPosition = 0;
        try {
            errorPosition = scan.nextInt();
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }

        if (errorPosition != 0 && errorPosition <= codeWord.length) {
            codeWord[errorPosition - 1] = (codeWord[errorPosition - 1] + 1) % 2;
        } else if (errorPosition > codeWord.length) {
            System.out.println("Error: Invalid position. Exiting.");
            return;
        }

        // Display the modified codeword
        System.out.println("Sent codeword:");
        for (int i = codeWord.length - 1; i >= 0; i--) {
            System.out.print(codeWord[i]);
        }
        System.out.println();

        // Detect and correct errors
        HammingCode.receivedCode(codeWord, codeWord.length - dataWord.length);
    }
}
