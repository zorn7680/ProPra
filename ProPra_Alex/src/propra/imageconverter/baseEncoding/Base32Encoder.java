package propra.imageconverter.baseEncoding;


public class Base32Encoder {

	// base32 alphabet
	private String base32Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUV";

	public String encode(byte[] inBytes) {
		StringBuffer base32 = new StringBuffer(inBytes.length * 8);
		String stringSequence;
		String[] stringArray;

		// write bit sequence to base32 (StringBuffer)
		for (int i = 0; i <= inBytes.length - 1; i++) {
			stringSequence = String.format("%8s", Integer.toBinaryString(inBytes[i] & 0xFF)).replace(' ', '0');
			base32.append(stringSequence);
		}

		// create sequence padded to full 5-bit strips
		int missingBits = 5 - (base32.length() % 5);
		for (int i = 1; i <= missingBits; i++) {
			base32.append("0");
		}

		// create String array with 5-bit-strips from StringBuffer
		stringSequence = base32.toString();
		int numChars = base32.length() / 5;
		stringArray = new String[numChars];
		int block = 0;
		for (int i = 0; i < numChars; i++) {
			stringArray[i] = base32.substring(block, block + 5);
			block += 5;
		}

		// create byte array with byte representations to base32 alphabet
		byte[] charTable = new byte[numChars];
		for (int i = 0; i < numChars; i++) {
			charTable[i] = (byte) Integer.parseInt(stringArray[i], 2);
		}

		// create char array with base32 encoded chars
		char[] result = new char[charTable.length];
		for (int i = 0; i < numChars; i++) {
			result[i] = base32Chars.charAt(charTable[i]);
		}

//		for (char c : result) {
//			System.out.print(c);
//		}

		return new String(result);
	}

}
