package propra.imageconverter.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import compression.ControlByte;

public class CompressionTools {

	static ByteArrayInputStream inStream;
	static ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	static ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	public static byte[] compress(byte[] inData) throws IOException {
		System.out.println("\nCompressionTools.compress() invoked!\n");

		return null;
	}

	/**
	 * Method decompresses given byte array and returns byte array with decompressed
	 * RGB information. Decompressed pixel byte order is equal to compressed pixel
	 * byte order (TGA format: BGR, ProPra format: RBG).
	 * 
	 * @param inputData byte array of compressed RGB information with control bytes
	 * @return byte array with uncompressed RGB information
	 * @throws IOException
	 */
	public static byte[] decompress(byte[] inputData) throws IOException {
		System.out.println("\nCompressionTools.decompress() invoked!\n");
		byte[] result;
		byte[] rgbSequence;

		inStream = new ByteArrayInputStream(inputData);
		outStream = new ByteArrayOutputStream();
		int cbType;
		int cbLength;
		ControlByte cByte;

		while (inStream.available() > 0) {
			cByte = new ControlByte(inStream.readNBytes(1)[0]);
			cbType = cByte.getType();
			cbLength = cByte.getLength();

			System.out.println("cb Typ: " + cbType);
			System.out.println("cb Länge: " + cbLength);

			/* control byte type is 0 - sequence with individual pixels */
			if (cbType == 0) {
				System.out.println("Sequenz!\n");
				rgbSequence = inStream.readNBytes(cbLength * 3);
				outStream.write(rgbSequence);
				/* control byte type is 1 - block with repeating pixels */
			} else {
				System.out.println("Wiederholung!\n");
				rgbSequence = inStream.readNBytes(3);
				for (int i = 1; i <= cbLength; i++) {
					outStream.write(rgbSequence);
				}
			}
		}

		result = outStream.toByteArray();
		inStream.close();
		outStream.close();

		for (byte b : result) {
			System.out.println(b);
		}
		System.out.println();

		return result;
	}

	/* HELPERS */

	@SuppressWarnings("unused")
	private static byte createControlByte(boolean isRepetitionCounter, int count) {
//		 Wiederholungszähler: Anzahl + 128
//		 Datenzähler: Anzahl
		return 0;
	}

}
