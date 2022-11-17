package propra.imageconverter.compression;

/*
 * CHANGELOG Abschnitt 2
 * - Klasse neu eingeführt, keine Vorgängerversion vorhanden
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/*
 * Diese Klasse dekomprimiert RGB-Daten, die als Byte-Array übergeben werden,
 * gemäß der Spezifikation zur RLE-Kompression.  Die dekomprimierte RGB-
 * Reihenfolge bleibt unverändert zur komprimierten.
 */

public class Decompressor {

	private ByteArrayInputStream inStream;
	private ByteArrayOutputStream outStream;


	public byte[] decompress(byte[] inputData) throws IOException {
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

			if (cbType == 0) {
				// Es folgt eine RAW-Paket
				rgbSequence = inStream.readNBytes(cbLength * 3);
				outStream.write(rgbSequence);
			} else {
				// Es folgt ein RunLength-Paket
				rgbSequence = inStream.readNBytes(3);
				for (int i = 1; i <= cbLength; i++) {
					outStream.write(rgbSequence);
				}
			}
		}

		result = outStream.toByteArray();
		inStream.close();
		outStream.close();

		return result;
	}
}