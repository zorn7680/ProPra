package propra.imageconverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TGAImage extends ImageFile {

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

	public TGAImage() {
		headerData = new byte[18];
	}

	public TGAImage(String inputFileLocation) throws IOException {
		headerData = new byte[18];
		readTGAinput(inputFileLocation);
	}

	/* TGA-Quelldatei in Byte- und Hex(String)-Array einlesen */
	private void readTGAinput(String loc) throws IOException {
		try {
			inputByteData = Files.readAllBytes(Paths.get(loc));
			inputHexData = new String[inputByteData.length];
			
			/* Optionale Anforderung - falscher Bild-/Kompressionstyp */
			imageType = inputByteData[2];
			if(imageType != 2) {
//				System.err.println("Abbruch - Falsche(r) Bildtyp/Kompression!");
//				System.exit(123);
			}

			for (int i = 0; i < 18; i++) {
				headerData[i] = inputByteData[i];
			}

			for (int i = 0; i < inputByteData.length; i++) {
				inputHexData[i] = Integer.toHexString(inputByteData[i] & 0xff);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		extractRgbData(DIMENSIONS_TGA,HEADER_SIZE_TGA);
	}
}
