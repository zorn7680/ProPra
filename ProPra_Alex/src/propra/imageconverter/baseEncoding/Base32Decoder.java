package propra.imageconverter.baseEncoding;

/*
 * CHANGELOG Abschnitt 2
 * - Klasse neu angelegt, keine Vorgängerversion
 */

/*
 * Diese Klasse dekodiert einen BASE32-kodierten Datensatz, der als String 
 * an die Methode übergeben wird. Rückgabetyp ist ein Byte-Array.
 */

public class Base32Decoder {
	
	// BASE32 Alphabet
	private String base32Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUV";
	private StringBuffer sBuffer = new StringBuffer();

	public byte[] decode(String inString) {

		int[] indexArray = new int[inString.length()];
		for (int i = 0; i <= inString.length() - 1; i++) {
			String p = inString.substring(i, i + 1);
			indexArray[i] = (byte) base32Chars.indexOf(p);
			String bits = Integer.toBinaryString(indexArray[i] & 0xFF).replace(' ', '0');
			String str = String.format("%05d", Integer.parseInt(bits));
			sBuffer.append(str);
		}

		String test = sBuffer.toString();
		int crop = test.length() % 8;
		String croppedSequence = test.substring(0, test.length() - crop);

		int numChars = croppedSequence.length() / 8;
		String[] stringArray = new String[numChars];
		int block = 0;
		for (int i = 0; i < numChars; i++) {
			stringArray[i] = croppedSequence.substring(block, block + 8);
			block += 8;
		}

		int[] intTable = new int[stringArray.length];

		for (int i = 0; i < intTable.length; i++) {
			intTable[i] = Integer.parseInt(stringArray[i], 2);
		}
		
		byte[] byteTable = new byte[stringArray.length];
		for(int i = 0; i < stringArray.length; i++ ) {
			byteTable[i] = (byte)intTable[i];
		}
		return byteTable;
	}
}