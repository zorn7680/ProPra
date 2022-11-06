package propra.imageconverter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class ProPraImage extends ImageFile {

	/* ProPra-spezifische Header-Attribute */
	
//	private byte[] formatId;					// bytes 11,10,9,8,7,6,5,4,3,2,1,0 (always "ProPraWiSe22") (evtl. für später)
	private byte compressionType; 				// byte 12 (0 = uncompressed) (evtl. für später)
//	private int bitsPerDot; 					// byte 17 (allowed values: 24) (evtl. für später)
//	private int dataSegmentSize; 				// bytes 25,24,23,22,21,20,19,18 (evtl. für später)
	private byte[] checksum = new byte[4]; 		// bytes 29,28,27,26

	public ProPraImage() {
		headerData = new byte[30];
	}

	public ProPraImage(String inputFileLocation) throws IOException {
		headerData = new byte[30];
		readProPraInput(inputFileLocation);
	}

	/* ProPra-Quelldatei als Byte- und Hex(String)-Array einlesen */
	public void readProPraInput(String loc) throws IOException {
		try {
			inputByteData = Files.readAllBytes(Paths.get(loc));
			inputHexData = new String[inputByteData.length];
			
			/* Optionale Anforderung - ungültiger Bild-/Kompressionstyp */
			compressionType = inputByteData[12];
			if(compressionType != 0) {
				System.err.println("Abbruch - nicht unterstützter Kompressionstyp!");
				System.exit(123);
			}

			for(int i = 0; i < 4 ; i++) {
				checksum[i] = inputByteData[i+26];
			}

			for (int i = 0; i < 30; i++) {
				headerData[i] = inputByteData[i];
			}

			for (int i = 0; i < inputByteData.length; i++) {
				inputHexData[i] = Integer.toHexString(inputByteData[i] & 0xff);
			}
			
			/* Optionale Anforderung - falsche Dateigröße im Header */
			int imageWidth = Integer.parseInt((inputHexData[14] + inputHexData[13]), 16); // bytes 18-25: dataSegmentSize
			int imageHeight = Integer.parseInt((inputHexData[16] + inputHexData[15]), 16);
			long dataSegmentSize = (long) imageWidth * (long) imageHeight * 3;
			ByteBuffer bbuf = ByteBuffer.allocate(8);
			bbuf.order(ByteOrder.BIG_ENDIAN);
			bbuf.putLong(dataSegmentSize);
			byte[] calculatedDataSegmentSize = bbuf.array();

			byte[] headerDataSegmentSize = { inputByteData[25], inputByteData[24], inputByteData[23], inputByteData[22],
					inputByteData[21], inputByteData[20], inputByteData[19], inputByteData[18] };
			
			if(!Arrays.equals(calculatedDataSegmentSize, headerDataSegmentSize)) {
				System.err.println("Abbruch - falsche Dateigröße im Header");
				System.exit(123);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		extractRgbData(DIMENSIONS_PROPRA,HEADER_SIZE_PROPRA);
	}
}
