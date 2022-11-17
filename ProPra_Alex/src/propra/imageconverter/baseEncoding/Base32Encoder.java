package propra.imageconverter.baseEncoding;

/*
 * CHANGELOG Abschnitt 2
 * - Klasse neu eingeführt, keine Vorgängerversion
 */

/* Diese Klasse überführt die Eingabedaten in einen BASE32-kodierten
 * Datensatz und gibt diesen als String zurück.
 */

public class Base32Encoder {

	// base32 Alphabet
	private String base32Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUV";

	// ÜBERLADUNG - Encoding, wenn Eigabeformat ein int-Array ist
	public String encode(int[] inInt) {
		StringBuffer base32 = new StringBuffer(inInt.length * 8);
		String stringSequence;
		String[] stringArray;

		// Bit-Sequenz in den Buffer schreiben
		for (int i = 0; i <= inInt.length - 1; i++) {
			stringSequence = String.format("%8s", Integer.toBinaryString(inInt[i] & 0xFF)).replace(' ', '0');
			base32.append(stringSequence);
		}

		// Sequenz zu vollen 5-bit-Tupeln auffüllen
		int missingBits = 5 - (base32.length() % 5);
		for (int i = 1; i <= missingBits; i++) {
			base32.append("0");
		}

		// String-Array mit 5-bit-Sequenzen erzeugen
		stringSequence = base32.toString();
		int numChars = base32.length() / 5;
		stringArray = new String[numChars];
		int block = 0;
		for (int i = 0; i < numChars; i++) {
			stringArray[i] = base32.substring(block, block + 5);
			block += 5;
		}

		// Byte-Array mit Byte-Repräsentationen erzeugen
		byte[] charTable = new byte[numChars];
		for (int i = 0; i < numChars; i++) {
			charTable[i] = (byte) Integer.parseInt(stringArray[i], 2);
		}

		// Char-Array aufbauen
		char[] result = new char[charTable.length];
		for (int i = 0; i < numChars; i++) {
			result[i] = base32Chars.charAt(charTable[i]);
		}

		return new String(result);
	}

	// ÜBERLADUNG - Encoding, wenn Eingabeformat ein String-Array ist
	public String encode(String inString) {
		byte[] inBytes = inString.getBytes();
		StringBuffer base32 = new StringBuffer(inBytes.length * 8);
		String stringSequence;
		String[] stringArray;

		// Bit-Sequenz in den Buffer schreiben
		for (int i = 0; i <= inBytes.length - 1; i++) {
			stringSequence = String.format("%8s", Integer.toBinaryString(inBytes[i] & 0xFF)).replace(' ', '0');
			base32.append(stringSequence);
		}

		// Sequenz zu vollen 5-bit-Tupeln auffüllen
		int missingBits = 5 - (base32.length() % 5);
		for (int i = 1; i <= missingBits; i++) {
			base32.append("0");
		}

		// String-Array mit 5-bit-Sequenzen erzeugen
		stringSequence = base32.toString();
		int numChars = base32.length() / 5;
		stringArray = new String[numChars];
		int block = 0;
		for (int i = 0; i < numChars; i++) {
			stringArray[i] = base32.substring(block, block + 5);
			block += 5;
		}

		// Byte-Array mit Byte-Repräsentationen erzeugen
		byte[] charTable = new byte[numChars];
		for (int i = 0; i < numChars; i++) {
			charTable[i] = (byte) Integer.parseInt(stringArray[i], 2);
		}

		// Char-Array aufbauen
		char[] result = new char[charTable.length];
		for (int i = 0; i < numChars; i++) {
			result[i] = base32Chars.charAt(charTable[i]);
		}

		return new String(result);
	}

}

