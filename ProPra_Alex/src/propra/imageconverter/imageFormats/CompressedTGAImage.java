package propra.imageconverter.imageFormats;

/* CHANGELOG Abschnitt 2
 * - Klasse neu eingeführt, keine Änderungen zu Vorgängerversion
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CompressedTGAImage extends ImageFile {
	
	/*
	 * Basisklasse für komprimierte TGA-Bilder. Superklasse ist ImageFile, 
	 * die weitere Attribute und Methoden zur Verfügung stellt.
	 */

	/* TGA-spezifische Header-Attribute */

//	private byte idLength; 				// byte 0 (evtl. für später) 
//	private byte colorMapType; 			// byte 1 (evtl. für später)
	private byte imageType; 			// byte 2
//	private byte[] colorMapOrigin; 		// bytes 4,3 (evtl. für später)
//	private byte[] colorMapLength; 		// bytes 6,5 (evtl. für später)
//	private byte colorMapDepth; 		// byte 7 (evtl. für später)
//	private byte[] xOrigin; 			// bytes 9,8 (evtl. für später)
//	private byte[] yOrigin; 			// bytes 11,10 (evtl. für später)
//	private byte bitsPerPixel; 			// byte 16 (evtl. für später)
//	private byte imageAttributes; 		// byte 17 (evtl. für später)

	public CompressedTGAImage() {
		headerData = new byte[18];
	}

	public CompressedTGAImage(String inputFileLocation) throws IOException {
		headerData = new byte[18];
		readTGAinput(inputFileLocation);
	}

	// TGA-Quelldatei in Byte- und Hex(String)-Array einlesen
	private void readTGAinput(String loc) throws IOException {
		try {
			// Lese Quelldatei als Byte-Array
			inputByteData = Files.readAllBytes(Paths.get(loc));
			// Erzeuge HEX-Array aus Byte-Array
			inputHexData = new String[inputByteData.length];

			for (int i = 0; i < 18; i++) {
				headerData[i] = inputByteData[i];
			}

			for (int i = 0; i < inputByteData.length; i++) {
				inputHexData[i] = Integer.toHexString(inputByteData[i] & 0xff);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		extractCompressedRgbData(DIMENSIONS_TGA,HEADER_SIZE_TGA);
	}
}