package propra.imageconverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TGAImage extends ImageFile {

//	private byte idLength; 				// byte 0 (evtl. für später) 
//	private byte colorMapType; 			// byte 1 (evtl. für später)
	private byte imageType; 			// byte 2 
//	private byte[] colorMapOrigin; 		// bytes 4,3 (evtl. für später)
//	private byte[] colorMapLength; 		// bytes 6,5 (evtl. für später)
//	private byte colorMapDepth; 		// byte 7 (evtl. für später)
//	private byte[] xOrigin; 			// bytes 9,8 (evtl. für später)
//	private byte[] yOrigin; 			// bytes 11,10 (evtl. für später)
	private int width; 					// bytes 13,12
	private int height; 				// bytes 15,14
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
				System.err.println("Abbruch - Falsche(r) Bildtyp/Kompression!");
				System.exit(123);
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
		extractRgbData();
	}

	/* Hilfsmethode - RGB-Daten in Byte-Array separieren */
	public void extractRgbData() {
		int pixels = getPixelAmount();
		/* Optionale Anforderung - inkonsistente Bilddimensionen */
		if(pixels == 0 ) {
			System.err.println("Abbruch - Inkonsistente Bilddimension (Nullbreite/Nullhöhe)!");
			System.exit(123);
		}
		int rgbValuesCount = pixels * 3;
		rgbData = new byte[rgbValuesCount];
		for (int i = 0; i < rgbValuesCount; i++) {
			rgbData[i] = inputByteData[i + 18];
		}
	}

	/* Hilfsmethode - berechne Gesamt-Pixelanzahl */
	private int getPixelAmount() {
		int imageWidth = Integer.parseInt((inputHexData[13] + inputHexData[12]), 16);
		int imageHeight = Integer.parseInt((inputHexData[15] + inputHexData[14]), 16);
		this.width = imageWidth;
		this.height = imageHeight;
		return imageWidth * imageHeight;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
