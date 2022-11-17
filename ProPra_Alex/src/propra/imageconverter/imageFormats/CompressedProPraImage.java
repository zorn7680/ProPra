package propra.imageconverter.imageFormats;

/* 
 * CHANGELOG Abschnitt 2
 * - Klasse neu eingeführt, keine Änderungen zu Vorgängerversion
 */


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import propra.imageconverter.tools.ByteTools;
import propra.imageconverter.tools.ChecksumTools;

/* 
 * Basisklasse für komprimierte ProPra-Bilder. Superklasse ist ImageFile, die
 * weitere Attribute und Methoden zur Verfügung stellt. 
 */


public class CompressedProPraImage extends ImageFile {

	// ProPra-spezifische Header-Attribute
	
//	private byte[] formatId;					// bytes 11,10,9,8,7,6,5,4,3,2,1,0 (always "ProPraWiSe22") (evtl. für später)
	private byte compressionType; 				// byte 12 (0 = uncompressed) (evtl. für später)
//	private int bitsPerDot; 					// byte 17 (allowed values: 24) (evtl. für später)
//	private int dataSegmentSize; 				// bytes 25,24,23,22,21,20,19,18 (evtl. für später)
	private byte[] checksum = new byte[4]; 		// bytes 29,28,27,26

	public CompressedProPraImage() {
		headerData = new byte[30];
	}

	public CompressedProPraImage(String inputFileLocation) throws IOException {
		headerData = new byte[30];
		readProPraInput(inputFileLocation);
	}

	// ProPra-Quelldatei als Byte- und Hex(String)-Array einlesen
	public void readProPraInput(String loc) throws IOException {
		try {
			inputByteData = Files.readAllBytes(Paths.get(loc));
			inputHexData = new String[inputByteData.length];
			
			// Optionale Anforderung - ungültiger Bild-/Kompressionstyp
			compressionType = inputByteData[12];
			if(compressionType != 0 && compressionType != 1) {
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
			extractCompressedRgbData(DIMENSIONS_PROPRA,HEADER_SIZE_PROPRA);
			
			// Optionale Anforderung - falsche Dateigröße im Header
			long dataSegmentSize = compressedRgbData.length;
			long dsSizeFromBytes = ByteTools.getLongFromByteArray(inputByteData, 18);
			if(dataSegmentSize != dsSizeFromBytes) {
				System.err.println("Abbruch - falsche Dateigröße im Header");
				System.exit(123);
			}
			
			// Optionale Anforderung - falsche Prüfsumme im Header
			int calcCheckSum = ByteTools.getIntFromByteArray(ChecksumTools.getChecksum(compressedRgbData),0);
			int headerCheckSum = ByteTools.getIntFromByteArray(inputByteData,26);
			if(calcCheckSum != headerCheckSum) {
				System.err.println("Abbruch - falsche Prüfsumme im Header");
				System.exit(123);
			}
		} catch (Exception e) {
			System.err.println("Fehler beim Einlesen der Quelldatei!");
			System.exit(123);
		}
	}
}
