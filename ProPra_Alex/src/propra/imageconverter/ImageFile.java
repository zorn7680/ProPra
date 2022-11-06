package propra.imageconverter;

public class ImageFile {

	public byte[] inputByteData;
	public String[] inputHexData;
	public byte[] headerData;
	public byte[] rgbData;
	
	private int width; 			// TGA: bytes 13,12  -  ProPra: bytes 14,13 
	private int height; 		// TGA: bytes 15,14  -  ProPra: bytes 16,15
	
	public final int HEADER_SIZE_PROPRA = 30;
	public final int HEADER_SIZE_TGA = 18;
	
	public final byte[] DIMENSIONS_TGA = {13,12,15,14};
	public final byte[] DIMENSIONS_PROPRA = {14,13,16,15};

	public void printHeader(String mode) {
		switch (mode) {
		case "hex":
			System.out.println("Header im HEX-Format");
			for (int i = 0; i < headerData.length; i++) {
				System.out.println("Byte " + i + ": " + Integer.toHexString(headerData[i] & 0xff));
			}
			System.out.println("\n");
			break;
		case "byte":
			System.out.println("Header im Original-Byte-Format");
			for (int i = 0; i < headerData.length; i++) {
				System.out.println("Byte " + i + ": " + headerData[i]);
			}
			System.out.println("\n");
			break;
		default:
			break;
		}
	}
	
	/* Hilfsmethode - RGB-Daten in Byte-Array separieren */
	public void extractRgbData(byte[] dimensions, int headerSize) { 
		int pixels = getPixelAmount(dimensions);
		
		/* Optionale Anforderung - inkonsistente Bilddimensionen */
		if(pixels == 0 ) {
			System.err.println("Abbruch - Inkonsistente Bilddimension (Nullbreite/Nullhöhe)!");
			System.exit(123);
		}
		int rgbValuesCount = pixels * 3;
		rgbData = new byte[rgbValuesCount];
		
		for (int i = 0; i < rgbValuesCount; i++) {
			rgbData[i] = inputByteData[i + headerSize];
		}
	}
	
	/* Hilfsmethode - berechne Dimensionen und Bilddatenmenge abhängig vom Bildformat */
	private int getPixelAmount(byte[] dimensions) {
		int imageWidth = Integer.parseInt((inputHexData[dimensions[0]] + inputHexData[dimensions[1]]), 16);
		int imageHeight = Integer.parseInt((inputHexData[dimensions[2]] + inputHexData[dimensions[3]]), 16);
		width = imageWidth;
		height = imageHeight;
		return imageWidth * imageHeight;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
