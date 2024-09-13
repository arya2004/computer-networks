package Hamming;

class HammingCode{
	
	static int generateParityBit(int data[], int power) {
		int parity_bit = 0;
		for(int j=0 ; j < data.length ; j++) {
			if(data[j] != 2) {
				
				// If 'j' is not a parity bit position
				// We will save that index value in k, increase it by 1,
				// Then we convert it into a binary string for later use:
				
				int k = j+1;
				String s = Integer.toBinaryString(k);
				
				//We now need to check the value stored at the location if the bit at the 2^(power) location of the binary value of index is 1
				//If the value is 1, we perform a modulo-2 addition to get the parity bit.
				
				int z = ((Integer.parseInt(s))/((int) Math.pow(10, power)))%10;
				if(z == 1) {
					if(data[j] == 1) {
						parity_bit = (parity_bit+1)%2;
					}
				}
			}
		}
		return parity_bit;
	}
	
	static int[] generateCodeWord(int _data[]) {
		
		int b[];
		
		// We find how many parity bits are required to be added to the data word:
		
		int position=0, parityCount=0 , j=0, k=0;
		
		while(position < _data.length) {
			
			// 2^(parity count) must equal the current position
			// +1 is needed since array indices start from 0 whereas we need to start from 1.
			
			if(Math.pow(2,parityCount) ==position+parityCount + 1) {
				parityCount++;
			}
			else {
				position++;
			}
		}
		
		// Length of new array is length of original array + parity count.
		b = new int[_data.length + parityCount];
		
		// We initialise this array with '2' at the locations of the parity bit:
		
		for(position=1 ; position <= b.length ; position++) {
			if(Math.pow(2, j) == position) {
				
			// Found a parity bit location.
			// Adjusting with (-1) to account for array indices starting from 0 instead of 1.
				
				b[position-1] = 2;
				j++;
			}
			else {
				b[k+j] = _data[k++];
			}
		}
		for(position=0 ; position < parityCount ; position++) {
			
			// Generating even parity bits for the parity bit locations:
			
			b[((int) Math.pow(2, position))-1] = generateParityBit(b, position);
		}
		return b;
	}
	
	static void receivedCode(int codeword[], int parityCount) {
		
		// It receives a Hamming code in array 'codeword'
		// We also require the number of parity bits added to the original data.
		// Now it needs to detect the error and correct it, if any.
		
		int power;
		
		int parity[] = new int[parityCount];
		// 'parity' array stores the values of the parity checks.
		
		String syndrome = new String();
		// The above string stores the integer value of error location.
		
		for(power=0 ; power < parityCount ; power++) {
		// The values of the parity bits are to be verified.
			
			for(int i=0 ; i < codeword.length ; i++) {
				// Extracting the bit from 2^(power):
				
				int k = i+1;
				String s = Integer.toBinaryString(k);
				int bit = ((Integer.parseInt(s))/((int) Math.pow(10, power)))%10;
				if(bit == 1) {
					if(codeword[i] == 1) {
						parity[power] = (parity[power]+1)%2;
					}
				}
			}
			syndrome = parity[power] + syndrome;
		}
		
		// The above algorithm gives us the parity check equation values.
		// We will use the values now to check if there is a single bit error and then correct it.
		
		int error_location = Integer.parseInt(syndrome, 2);
		if(error_location != 0) {
			System.out.println("An error has been identified at location " + error_location + ".");
			codeword[error_location-1] = (codeword[error_location-1]+1)%2;
			System.out.println("The erroneous code has been corrected. The new codeword is:");
			for(int i=codeword.length-1 ; i >=0 ; i--) {
				System.out.print(codeword[i]);
			}
			System.out.println();
		}
		else {
			System.out.println("It is assumed that there are no errors in the recieved codeword");
		}
		
		// Finally, we extract the data word initially sent from the received code:
		System.out.println("The data initially sent was:");
		power = parityCount-1;
		for(int i=codeword.length ; i > 0 ; i--) {
			if(Math.pow(2, power) != i) {
				System.out.print(codeword[i-1]);
			}
			else {
				power--;
			}
		}
		System.out.println();
	}
}