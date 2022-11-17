package propra.imageconverter;

/* CHANGELOG Abschnitt 2
 * - Erweiterung um neue Methoden für Base32 und Kompression
 * - Erweiterung um Datei-Handling entsprechend der neuen Methoden
 */


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;

import propra.imageconverter.baseEncoding.Base32Decoder;
import propra.imageconverter.baseEncoding.Base32Encoder;
import propra.imageconverter.compression.Compressor;
import propra.imageconverter.imageFormats.CompressedProPraImage;
import propra.imageconverter.imageFormats.CompressedTGAImage;
import propra.imageconverter.imageFormats.ProPraImage;
import propra.imageconverter.imageFormats.TGAImage;
import propra.imageconverter.tools.ChecksumTools;

public final class Converter {

	/*
	 * HINWEIS Dieses undynamische Methodenkonstrukt ist nicht OOP-tauglich, 
	 * aber es hat die Zeit für eine modularere Implementierung gefehlt. 
	 * Architektur wird evtl. nach dem Bearbeitungsabschnitt 2 noch verbessert. 
	 * Ich möchte mich bereits im Vorfeld bei der Person entschuldigen, die 
	 * diesen Code reviewen muss... ;-)
	 * Es wiederholt sich aber vieles, durch meine Kommentare werden die 
	 * einzelnen Teilstücke deutlich und müssen nicht mehrfach geprüft werden.
	 */


	/*
	 * ===================================================================== 
	 * LEGACY Operations (Konvertierungen aus Abschnitt 1) -----------------
	 * =====================================================================
	 */


	// Konvertiert TGA nach TGA
	protected static void convertTGAToTGA(String inPath, String outPath) throws IOException {
		try {
			// Quellbild einlesen und Daten ins Ausgabeformat überführen
			TGAImage sourceImage = new TGAImage(inPath);

			byte[] targetHeaderData = sourceImage.headerData;
			byte[] targetRgbData = sourceImage.rgbData;

			byte[] outputData = new byte[targetHeaderData.length + targetRgbData.length];

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetHeaderData);
			os.write(targetRgbData);
			outputData = os.toByteArray();
			Files.write(Paths.get(outPath), outputData);

			// Prüfung: Hat Schreibvorgang funktioniert?
			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert TGA nach ProPra
	protected static void convertTGAToProPra(String inPath, String outPath) throws IOException {
		try {
			// Quellbild einlesen, leeres Zielbild anlegen und Daten aufbauen
			TGAImage sourceImage = new TGAImage(inPath);
			ProPraImage targetImage = new ProPraImage();

			/* Header für ProPra-Zieldatei aufbauen */
			byte[] formatId = { 80, 114, 111, 80, 114, 97, 87, 105, 83, 101, 50, 50 }; 
			for (int i = 0; i < 12; i++) {
				targetImage.headerData[i] = formatId[i];
			}

			targetImage.headerData[12] = 0; // byte 12: compressionType

			targetImage.headerData[13] = sourceImage.headerData[12]; // bytes 13-14: width
			targetImage.headerData[14] = sourceImage.headerData[13];

			targetImage.headerData[15] = sourceImage.headerData[14]; // bytes 15-16: height
			targetImage.headerData[16] = sourceImage.headerData[15];

			targetImage.headerData[17] = 24; // byte 17: bitsPerDot

			// Datensegmentgröße für ProPra bestimmen
			int imageWidth = Integer.parseInt((sourceImage.inputHexData[13] + sourceImage.inputHexData[12]), 16); 
			int imageHeight = Integer.parseInt((sourceImage.inputHexData[15] + sourceImage.inputHexData[14]), 16);
			long dataSegmentSize = (long) imageWidth * (long) imageHeight * 3;
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putLong(dataSegmentSize);
			byte[] targetDataSegmentSize = bb.array();
			for (int i = 0; i < 8; i++) {
				targetImage.headerData[i + 18] = targetDataSegmentSize[i];
			}

			// RGB-Reihenfolge ändern
			byte[] targetRgbData = new byte[sourceImage.rgbData.length];
			targetImage.rgbData = new byte[targetRgbData.length];
			int tripletsCount = sourceImage.rgbData.length / 3;
			int factor = 0;

			for (int i = 0; i < tripletsCount; i++) {
				targetImage.rgbData[1 + i + factor] = sourceImage.rgbData[0 + i + factor];
				targetImage.rgbData[2 + i + factor] = sourceImage.rgbData[1 + i + factor];
				targetImage.rgbData[0 + i + factor] = sourceImage.rgbData[2 + i + factor];
				factor += 2;
			}

			// Prüfsumme berechnen
			byte[] targetChecksum = ChecksumTools.getChecksum(targetImage.rgbData); // bytes 26-29: checksum
			for (int i = 26; i < 30; i++) {
				targetImage.headerData[i] = targetChecksum[i - 26];
			}

			// Zieldatei schreiben
			byte[] outputData = new byte[targetImage.headerData.length + targetImage.rgbData.length];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetImage.headerData);
			os.write(targetImage.rgbData);
			outputData = os.toByteArray();
			Files.write(Paths.get(outPath), outputData);

			// Prüfe: War Schreibvorgang erfolgreich?
			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert ProPra nach TGA
	protected static void convertProPraToTGA(String inPath, String outPath) throws IOException {
		try {
			// Quellbild einlesen, leeres Zielbild anlegen
			ProPraImage sourceImage = new ProPraImage(inPath);
			TGAImage targetImage = new TGAImage();

			// Header-Daten für Zielbild aufbauen
			targetImage.headerData[0] = 0; // byte 0 - idLength

			targetImage.headerData[1] = 0; // byte 1 - colorMapType

			targetImage.headerData[2] = 2; // byte 2 - imageType

			targetImage.headerData[3] = 0; // bytes 3-4 - colorMapOrigin
			targetImage.headerData[4] = 0;

			targetImage.headerData[5] = 0; // bytes 5-6 - colorMapLength
			targetImage.headerData[6] = 0;

			targetImage.headerData[7] = 0; // byte 7 - colorMapDepth

			targetImage.headerData[8] = 0; // bytes 8-9 - xOrigin
			targetImage.headerData[9] = 0;

			int sourceHeight = sourceImage.getHeight(); // bytes 10-11 - yOrigin (must equal source image height)
			ByteBuffer bbHeight = ByteBuffer.allocate(4);
			bbHeight.order(ByteOrder.LITTLE_ENDIAN);
			bbHeight.putInt(sourceHeight);
			byte[] targetHeight = bbHeight.array();
			targetImage.headerData[10] = targetHeight[0];
			targetImage.headerData[11] = targetHeight[1];

			int sourceWidth = sourceImage.getWidth(); // bytes 12-13 - width
			ByteBuffer bbWidth = ByteBuffer.allocate(4);
			bbWidth.order(ByteOrder.LITTLE_ENDIAN);
			bbWidth.putInt(sourceWidth);
			byte[] targetWidth = bbWidth.array();
			targetImage.headerData[12] = targetWidth[0];
			targetImage.headerData[13] = targetWidth[1];

			targetImage.headerData[14] = targetImage.headerData[10]; // bytes 14-15 - height
			targetImage.headerData[15] = targetImage.headerData[11];

			targetImage.headerData[16] = 24; // byte 16 - bitsPerPixel

			targetImage.headerData[17] = 32; // byte 17 - imageAttributes


			// RGB-Daten-Reihenfolge ändern
			byte[] targetRgbData = new byte[sourceImage.rgbData.length];
			targetImage.rgbData = new byte[targetRgbData.length];
			int tripletsCount = sourceImage.rgbData.length / 3;
			int factor = 0;

			for (int i = 0; i < tripletsCount; i++) {
				targetImage.rgbData[2 + i + factor] = sourceImage.rgbData[0 + i + factor];
				targetImage.rgbData[0 + i + factor] = sourceImage.rgbData[1 + i + factor];
				targetImage.rgbData[1 + i + factor] = sourceImage.rgbData[2 + i + factor];
				factor += 2;
			}

			// Zieldatei schreiben
			byte[] outputData = new byte[targetImage.headerData.length + targetImage.rgbData.length];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetImage.headerData);
			os.write(targetImage.rgbData);
			outputData = os.toByteArray();
			Files.write(Paths.get(outPath), outputData);

			// Prüfung: Schreibvorgang erfolgreich?
			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert ProPra nach ProPra
	protected static void convertProPraToProPra(String inPath, String outPath) throws IOException {
		try {
			// Überführe 1:1 Quell- in Zieldatei, keine Operationen erforderlich
			ProPraImage sourceImage = new ProPraImage(inPath);
			byte[] outputData = sourceImage.inputByteData;
			Files.write(Paths.get(outPath), outputData);
			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}



	/*
	 * ===================================================================== 
	 * BASE32 Operations ---------------------------------------------------
	 * =====================================================================
	 */


	// Encode Datei nach Base32
	protected static void encodeToBase32(String inPath, String outPath) throws IOException {
		System.out.println("Encoding source file to BASE32...");

		// Quelldaten einlesen
		byte[] inputByteData = Files.readAllBytes(Paths.get(inPath));
		int[] ibdu = new int[inputByteData.length];

		for (int i = 0; i < inputByteData.length; i++) {
			ibdu[i] = Byte.toUnsignedInt(inputByteData[i]);
		}

		// Quelldaten codieren
		Base32Encoder enc = new Base32Encoder();
		String result = enc.encode(ibdu);
		
		// Zieldatei schreiben
		Files.write(Paths.get(outPath), result.getBytes());
		System.out.println("...success!");
	}

	// Decode Base32-Datei
	protected static void decodeFromBase32(String inPath, String outPath) throws IOException {
		System.out.println("Decoding source file from BASE32...");

		// Quelldaten einlesen
		byte[] inputByteData = Files.readAllBytes(Paths.get(inPath));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(inputByteData);
		String inputStringData = bos.toString();
		
		// Decodieren
		Base32Decoder dec = new Base32Decoder();
		byte[] result = dec.decode(inputStringData);
		
		// Zieldatei schreiben
		Files.write(Paths.get(outPath), result);
		System.out.println("...success!");
	}



	/*
	 * =====================================================================
	 * Compression Conversion Operations -----------------------------------
	 * =====================================================================
	 */


	// Konvertiert TGA komprimiert nach TGA unkomprimiert
	protected static void convertCTGAToUTGA(String inPath, String outPath) throws IOException {
		try {
			// Quelldatei einlesen
			CompressedTGAImage sourceImage = new CompressedTGAImage(inPath);

			// Header-Anpassungen für Kompression
			byte[] targetHeaderData = sourceImage.headerData;
			targetHeaderData[2] = 2;
			byte[] targetRgbData = sourceImage.rgbData;

			byte[] outputData = new byte[targetHeaderData.length + targetRgbData.length];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetHeaderData);
			os.write(targetRgbData);
			outputData = os.toByteArray();

			// Zieldatei schreiben
			Files.write(Paths.get(outPath), outputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert TGA komprimiert nach TGA komprimiert
	protected static void convertCTGAToCTGA(String inPath, String outPath) throws IOException {
		try {
			// Quelldatei einlesen
			CompressedTGAImage sourceImage = new CompressedTGAImage(inPath);

			byte[] targetHeaderData = sourceImage.headerData;
			byte[] targetRgbData = sourceImage.compressedRgbData;

			byte[] outputData = new byte[targetHeaderData.length + targetRgbData.length];

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetHeaderData);
			os.write(targetRgbData);
			outputData = os.toByteArray();
			
			// Zieldatei schreiben
			Files.write(Paths.get(outPath), outputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert TGA unkomprimiert nach TGA komprimiert
	protected static void convertUTGAToCTGA(String inPath, String outPath) throws IOException {
		try {
			//Quelldatei einlesen
			TGAImage sourceImage = new TGAImage(inPath);
			
			// Header-Anpassung für Kompression
			byte[] targetHeaderData = sourceImage.headerData;
			targetHeaderData[2] = 10;
			
			// Komprimiere RGB-Daten
			Compressor comp = new Compressor();
			byte[] targetRgbData = comp.compress(sourceImage.rgbData);
			byte[] outputData = new byte[targetHeaderData.length + targetRgbData.length];

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetHeaderData);
			os.write(targetRgbData);
			outputData = os.toByteArray();
			
			// Zieldatei schreiben
			Files.write(Paths.get(outPath), outputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert TGA komprimiert nach Propra unkomprimiert
	protected static void convertCTGAToUProPra(String inPath, String outPath) throws IOException {
		try {
			// Quelldatei einlesen, leere Zieldatei erzeugen
			CompressedTGAImage sourceImage = new CompressedTGAImage(inPath);
			ProPraImage targetImage = new ProPraImage();

			// Header für ProPra-Zieldatei aufbauen
			byte[] formatId = { 80, 114, 111, 80, 114, 97, 87, 105, 83, 101, 50, 50 };
			for (int i = 0; i < 12; i++) {
				targetImage.headerData[i] = formatId[i];
			}

			targetImage.headerData[12] = 0; // byte 12: compressionType (0=unc, 1=RLE)

			targetImage.headerData[13] = sourceImage.headerData[12]; // bytes 13-14: width
			targetImage.headerData[14] = sourceImage.headerData[13];

			targetImage.headerData[15] = sourceImage.headerData[14]; // bytes 15-16: height
			targetImage.headerData[16] = sourceImage.headerData[15];

			targetImage.headerData[17] = 24; // byte 17: bitsPerDot

			// RGB-Reihenfolge für Zielformat ändern
			byte[] targetRgbData = new byte[sourceImage.rgbData.length];
			targetImage.rgbData = new byte[targetRgbData.length];
			int tripletsCount = sourceImage.rgbData.length / 3;
			int factor = 0;

			for (int i = 0; i < tripletsCount; i++) {
				targetImage.rgbData[1 + i + factor] = sourceImage.rgbData[0 + i + factor];
				targetImage.rgbData[2 + i + factor] = sourceImage.rgbData[1 + i + factor];
				targetImage.rgbData[0 + i + factor] = sourceImage.rgbData[2 + i + factor];
				factor += 2;
			}

			// Datensegmentgröße berechnen
			int imageWidth = sourceImage.getWidth();
			int imageHeight = sourceImage.getHeight();
			long dataSegmentSize = (long) imageWidth * (long) imageHeight * 3;
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putLong(dataSegmentSize);
			byte[] targetDataSegmentSize = bb.array();
			for (int i = 0; i < 8; i++) {
				targetImage.headerData[i + 18] = targetDataSegmentSize[i];
			}

			// Prüfsumme berechnen
			byte[] targetChecksum = ChecksumTools.getChecksum(targetImage.rgbData); // bytes 26-29: checksum
			for (int i = 26; i < 30; i++) {
				targetImage.headerData[i] = targetChecksum[i - 26];
			}

			byte[] outputData = new byte[targetImage.headerData.length + targetImage.rgbData.length];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetImage.headerData);
			os.write(targetImage.rgbData);
			outputData = os.toByteArray();
			
			// Zieldatei schreiben
			Files.write(Paths.get(outPath), outputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert TGA komprimiert nach Propra komprimiert
	protected static void convertCTGAToCProPra(String inPath, String outPath) throws IOException {
		try {
			// Quelldatei einlesen, leeres Zielformat erzeugen
			CompressedTGAImage sourceImage = new CompressedTGAImage(inPath);
			ProPraImage targetImage = new ProPraImage();

			// Header für ProPra-Zieldatei aufbauen
			byte[] formatId = { 80, 114, 111, 80, 114, 97, 87, 105, 83, 101, 50, 50 };
			for (int i = 0; i < 12; i++) {
				targetImage.headerData[i] = formatId[i];
			}

			targetImage.headerData[12] = 1; // byte 12: compressionType (0=unc, 1=RLE)

			targetImage.headerData[13] = sourceImage.headerData[12]; // bytes 13-14: width
			targetImage.headerData[14] = sourceImage.headerData[13];

			targetImage.headerData[15] = sourceImage.headerData[14]; // bytes 15-16: height
			targetImage.headerData[16] = sourceImage.headerData[15];

			targetImage.headerData[17] = 24; // byte 17: bitsPerDot

			// RGB-Reihenfolge für Zielformat ändern
			byte[] targetRgbData = new byte[sourceImage.rgbData.length];
			targetImage.rgbData = new byte[targetRgbData.length];
			int tripletsCount = sourceImage.rgbData.length / 3;
			int factor = 0;

			for (int i = 0; i < tripletsCount; i++) {
				targetImage.rgbData[1 + i + factor] = sourceImage.rgbData[0 + i + factor];
				targetImage.rgbData[2 + i + factor] = sourceImage.rgbData[1 + i + factor];
				targetImage.rgbData[0 + i + factor] = sourceImage.rgbData[2 + i + factor];
				factor += 2;
			}

			// Zieldaten komprimieren und austauschen
			Compressor comp = new Compressor();
			byte[] compressedData = comp.compress(targetImage.rgbData);
			targetImage.rgbData = compressedData;


			// Datensegmentgröße berechnen
			long dataSegmentSize = targetImage.rgbData.length;
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putLong(dataSegmentSize);
			byte[] targetDataSegmentSize = bb.array();
			for (int i = 0; i < 8; i++) {
				targetImage.headerData[i + 18] = targetDataSegmentSize[i];
			}

			// Prüfsumme berechnen
			byte[] targetChecksum = ChecksumTools.getChecksum(targetImage.rgbData); // bytes 26-29: checksum
			for (int i = 26; i < 30; i++) {
				targetImage.headerData[i] = targetChecksum[i - 26];
			}

			byte[] outputData = new byte[targetImage.headerData.length + targetImage.rgbData.length];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetImage.headerData);
			os.write(targetImage.rgbData);
			outputData = os.toByteArray();
			
			// Zieldatei schreiben
			Files.write(Paths.get(outPath), outputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert TGA unkomprimiert nach Propra komprimiert
	protected static void convertUTGAToCProPra(String inPath, String outPath) throws IOException {
		try {
			// Quelldatei einlesen, leeres Zielformat erzeugen
			TGAImage sourceImage = new TGAImage(inPath);
			ProPraImage targetImage = new ProPraImage();

			// Header für ProPra-Zieldatei aufbauen
			byte[] formatId = { 80, 114, 111, 80, 114, 97, 87, 105, 83, 101, 50, 50 };
			for (int i = 0; i < 12; i++) {
				targetImage.headerData[i] = formatId[i];
			}

			targetImage.headerData[12] = 1; // byte 12: compressionType (0=unc, 1=RLE)

			targetImage.headerData[13] = sourceImage.headerData[12]; // bytes 13-14: width
			targetImage.headerData[14] = sourceImage.headerData[13];

			targetImage.headerData[15] = sourceImage.headerData[14]; // bytes 15-16: height
			targetImage.headerData[16] = sourceImage.headerData[15];

			targetImage.headerData[17] = 24; // byte 17: bitsPerDot

			// RGB-Reihenfolge ändern für Zielformat
			byte[] targetRgbData = new byte[sourceImage.rgbData.length];
			targetImage.rgbData = new byte[targetRgbData.length];
			int tripletsCount = sourceImage.rgbData.length / 3;
			int factor = 0;

			for (int i = 0; i < tripletsCount; i++) {
				targetImage.rgbData[1 + i + factor] = sourceImage.rgbData[0 + i + factor];
				targetImage.rgbData[2 + i + factor] = sourceImage.rgbData[1 + i + factor];
				targetImage.rgbData[0 + i + factor] = sourceImage.rgbData[2 + i + factor];
				factor += 2;
			}

			// Zieldaten komprimieren und tauschen
			Compressor comp = new Compressor();
			byte[] compressedData = comp.compress(targetImage.rgbData);
			targetImage.rgbData = compressedData;


			// Datensegmentgröße berechnen
			long dataSegmentSize = targetImage.rgbData.length;
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putLong(dataSegmentSize);
			byte[] targetDataSegmentSize = bb.array();
			for (int i = 0; i < 8; i++) {
				targetImage.headerData[i + 18] = targetDataSegmentSize[i];
			}

			// Prüfsumme berechnen
			byte[] targetChecksum = ChecksumTools.getChecksum(targetImage.rgbData); // bytes 26-29: checksum
			for (int i = 26; i < 30; i++) {
				targetImage.headerData[i] = targetChecksum[i - 26];
			}

			byte[] outputData = new byte[targetImage.headerData.length + targetImage.rgbData.length];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetImage.headerData);
			os.write(targetImage.rgbData);
			outputData = os.toByteArray();
			
			// Zieldatei schreiben
			Files.write(Paths.get(outPath), outputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert ProPra komprimiert nach TGA unkomprimiert
	protected static void convertCProPraToUTGA(String inPath, String outPath) throws IOException {
		try {
			// Quelldatei einlesen, leeres Zielformat erzeugen
			CompressedProPraImage sourceImage = new CompressedProPraImage(inPath);
			TGAImage targetImage = new TGAImage();

			// Header für TGA-Zieldatei aufbauen
			targetImage.headerData[0] = 0; // byte 0 - idLength

			targetImage.headerData[1] = 0; // byte 1 - colorMapType

			targetImage.headerData[2] = 2; // byte 2 - imageType

			targetImage.headerData[3] = 0; // bytes 3-4 - colorMapOrigin
			targetImage.headerData[4] = 0;

			targetImage.headerData[5] = 0; // bytes 5-6 - colorMapLength
			targetImage.headerData[6] = 0;

			targetImage.headerData[7] = 0; // byte 7 - colorMapDepth

			targetImage.headerData[8] = 0; // bytes 8-9 - xOrigin
			targetImage.headerData[9] = 0;

			int sourceHeight = sourceImage.getHeight(); // bytes 10-11 - yOrigin (must equal source image height)
			ByteBuffer bbHeight = ByteBuffer.allocate(4);
			bbHeight.order(ByteOrder.LITTLE_ENDIAN);
			bbHeight.putInt(sourceHeight);
			byte[] targetHeight = bbHeight.array();
			targetImage.headerData[10] = targetHeight[0];
			targetImage.headerData[11] = targetHeight[1];

			int sourceWidth = sourceImage.getWidth(); // bytes 12-13 - width
			ByteBuffer bbWidth = ByteBuffer.allocate(4);
			bbWidth.order(ByteOrder.LITTLE_ENDIAN);
			bbWidth.putInt(sourceWidth);
			byte[] targetWidth = bbWidth.array();
			targetImage.headerData[12] = targetWidth[0];
			targetImage.headerData[13] = targetWidth[1];

			targetImage.headerData[14] = targetImage.headerData[10]; // bytes 14-15 - height
			targetImage.headerData[15] = targetImage.headerData[11];

			targetImage.headerData[16] = 24; // byte 16 - bitsPerPixel

			targetImage.headerData[17] = 32; // byte 17 - imageAttributes

			// RGB-Reihenfolge ändern für Zielformat
			byte[] targetRgbData = new byte[sourceImage.rgbData.length];
			targetImage.rgbData = new byte[targetRgbData.length];
			int tripletsCount = sourceImage.rgbData.length / 3;
			int factor = 0;

			for (int i = 0; i < tripletsCount; i++) {
				targetImage.rgbData[2 + i + factor] = sourceImage.rgbData[0 + i + factor];
				targetImage.rgbData[0 + i + factor] = sourceImage.rgbData[1 + i + factor];
				targetImage.rgbData[1 + i + factor] = sourceImage.rgbData[2 + i + factor];
				factor += 2;
			}

			byte[] outputData = new byte[targetImage.headerData.length + targetImage.rgbData.length];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetImage.headerData);
			os.write(targetImage.rgbData);
			outputData = os.toByteArray();
			
			// Zieldatei schreiben
			Files.write(Paths.get(outPath), outputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert ProPra komprimiert nach TGA komprimiert
	protected static void convertCProPraToCTGA(String inPath, String outPath) throws IOException {
		try {
			// Quelldatei einlesen, leeres Zielformat erzeugen
			CompressedProPraImage sourceImage = new CompressedProPraImage(inPath);
			TGAImage targetImage = new TGAImage();

			// Header für TGA-Zieldatei aufbauen
			targetImage.headerData[0] = 0; // byte 0 - idLength

			targetImage.headerData[1] = 0; // byte 1 - colorMapType

			targetImage.headerData[2] = 10; // byte 2 - imageType

			targetImage.headerData[3] = 0; // bytes 3-4 - colorMapOrigin
			targetImage.headerData[4] = 0;

			targetImage.headerData[5] = 0; // bytes 5-6 - colorMapLength
			targetImage.headerData[6] = 0;

			targetImage.headerData[7] = 0; // byte 7 - colorMapDepth

			targetImage.headerData[8] = 0; // bytes 8-9 - xOrigin
			targetImage.headerData[9] = 0;

			int sourceHeight = sourceImage.getHeight(); // bytes 10-11 - yOrigin (must equal source image height)
			ByteBuffer bbHeight = ByteBuffer.allocate(4);
			bbHeight.order(ByteOrder.LITTLE_ENDIAN);
			bbHeight.putInt(sourceHeight);
			byte[] targetHeight = bbHeight.array();
			targetImage.headerData[10] = targetHeight[0];
			targetImage.headerData[11] = targetHeight[1];

			int sourceWidth = sourceImage.getWidth(); // bytes 12-13 - width
			ByteBuffer bbWidth = ByteBuffer.allocate(4);
			bbWidth.order(ByteOrder.LITTLE_ENDIAN);
			bbWidth.putInt(sourceWidth);
			byte[] targetWidth = bbWidth.array();
			targetImage.headerData[12] = targetWidth[0];
			targetImage.headerData[13] = targetWidth[1];

			targetImage.headerData[14] = targetImage.headerData[10]; // bytes 14-15 - height
			targetImage.headerData[15] = targetImage.headerData[11];

			targetImage.headerData[16] = 24; // byte 16 - bitsPerPixel

			targetImage.headerData[17] = 32; // byte 17 - imageAttributes

			// RGB-Reihenfolge ändern für Zielformat
			byte[] targetRgbData = new byte[sourceImage.rgbData.length];
			targetImage.rgbData = new byte[targetRgbData.length];
			int tripletsCount = sourceImage.rgbData.length / 3;
			int factor = 0;

			for (int i = 0; i < tripletsCount; i++) {
				targetImage.rgbData[2 + i + factor] = sourceImage.rgbData[0 + i + factor];
				targetImage.rgbData[0 + i + factor] = sourceImage.rgbData[1 + i + factor];
				targetImage.rgbData[1 + i + factor] = sourceImage.rgbData[2 + i + factor];
				factor += 2;
			}

			// RGB-Daten komprimieren
			Compressor comp = new Compressor();
			byte[] compressedData = comp.compress(targetImage.rgbData);
			byte[] outputData = new byte[targetImage.headerData.length + compressedData.length];

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetImage.headerData);
			os.write(compressedData);
			outputData = os.toByteArray();
			
			// Zieldatei schreiben
			Files.write(Paths.get(outPath), outputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert ProPra unkomprimiert nach TGA komprimiert
	protected static void convertUProPraToCTGA(String inPath, String outPath) throws IOException {
		try {
			// Quelldatei einlesen, leeres Zielformat erzeugen
			ProPraImage sourceImage = new ProPraImage(inPath);
			TGAImage targetImage = new TGAImage();

			/* Header für TGA-Zieldatei aufbauen */
			targetImage.headerData[0] = 0; // byte 0 - idLength

			targetImage.headerData[1] = 0; // byte 1 - colorMapType

			targetImage.headerData[2] = 10; // byte 2 - imageType

			targetImage.headerData[3] = 0; // bytes 3-4 - colorMapOrigin
			targetImage.headerData[4] = 0;

			targetImage.headerData[5] = 0; // bytes 5-6 - colorMapLength
			targetImage.headerData[6] = 0;

			targetImage.headerData[7] = 0; // byte 7 - colorMapDepth

			targetImage.headerData[8] = 0; // bytes 8-9 - xOrigin
			targetImage.headerData[9] = 0;

			int sourceHeight = sourceImage.getHeight(); // bytes 10-11 - yOrigin (must equal source image height)
			System.out.println("source height: " + sourceHeight);
			ByteBuffer bbHeight = ByteBuffer.allocate(4);
			bbHeight.order(ByteOrder.LITTLE_ENDIAN);
			bbHeight.putInt(sourceHeight);
			byte[] targetHeight = bbHeight.array();
			targetImage.headerData[10] = targetHeight[0];
			targetImage.headerData[11] = targetHeight[1];

			int sourceWidth = sourceImage.getWidth(); // bytes 12-13 - width
			ByteBuffer bbWidth = ByteBuffer.allocate(4);
			bbWidth.order(ByteOrder.LITTLE_ENDIAN);
			bbWidth.putInt(sourceWidth);
			byte[] targetWidth = bbWidth.array();
			targetImage.headerData[12] = targetWidth[0];
			targetImage.headerData[13] = targetWidth[1];

			targetImage.headerData[14] = targetImage.headerData[10]; // bytes 14-15 - height
			targetImage.headerData[15] = targetImage.headerData[11];

			targetImage.headerData[16] = 24; // byte 16 - bitsPerPixel

			targetImage.headerData[17] = 32; // byte 17 - imageAttributes

			// RGB-Reihenfolge ändern für Zielformat
			byte[] targetRgbData = new byte[sourceImage.rgbData.length];
			targetImage.rgbData = new byte[targetRgbData.length];
			int tripletsCount = sourceImage.rgbData.length / 3;
			int factor = 0;

			for (int i = 0; i < tripletsCount; i++) {
				targetImage.rgbData[2 + i + factor] = sourceImage.rgbData[0 + i + factor];
				targetImage.rgbData[0 + i + factor] = sourceImage.rgbData[1 + i + factor];
				targetImage.rgbData[1 + i + factor] = sourceImage.rgbData[2 + i + factor];
				factor += 2;
			}

			// RGB-Daten komprimieren und austauschen
			Compressor comp = new Compressor();
			byte[] compressedData = comp.compress(targetImage.rgbData);
			byte[] outputData = new byte[targetImage.headerData.length + compressedData.length];

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetImage.headerData);
			os.write(compressedData);
			outputData = os.toByteArray();
			
			// Zieldatei schreiben
			Files.write(Paths.get(outPath), outputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert ProPra komprimiert nach ProPra unkomprimiert
	protected static void convertCProPraToUProPra(String inPath, String outPath) throws IOException {
		try {
			// Quelldatei einlesen, leeres Zielformat erzeugen
			CompressedProPraImage sourceImage = new CompressedProPraImage(inPath);
			ProPraImage targetImage = new ProPraImage();


			// Header für ProPra-Zieldatei aufbauen
			byte[] formatId = { 80, 114, 111, 80, 114, 97, 87, 105, 83, 101, 50, 50 };
			for (int i = 0; i < 12; i++) {
				targetImage.headerData[i] = formatId[i];
			}

			targetImage.headerData[12] = 0; // byte 12: compressionType (0=unc, 1=RLE)

			targetImage.headerData[13] = sourceImage.headerData[13]; // bytes 13-14: width
			targetImage.headerData[14] = sourceImage.headerData[14];

			targetImage.headerData[15] = sourceImage.headerData[15]; // bytes 15-16: height
			targetImage.headerData[16] = sourceImage.headerData[16];

			targetImage.headerData[17] = 24; // byte 17: bitsPerDot

			// RGB-Daten übernehmen (Dekomprimiert wurde im Bild-Objekt!)
			targetImage.rgbData = sourceImage.rgbData;

			// Datensegmentgröße berechnen
			int imageWidth = sourceImage.getWidth();
			int imageHeight = sourceImage.getHeight();
			System.out.println(imageWidth);
			System.out.println(imageHeight);

			long dataSegmentSize = (long) imageWidth * (long) imageHeight * 3;
			System.out.println("data segment size" + dataSegmentSize);
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putLong(dataSegmentSize);
			byte[] targetDataSegmentSize = bb.array();
			for (int i = 0; i < 8; i++) {
				targetImage.headerData[i + 18] = targetDataSegmentSize[i];
			}

			// Prüfsumme berechnen
			byte[] targetChecksum = ChecksumTools.getChecksum(targetImage.rgbData); // bytes 26-29: checksum
			for (int i = 26; i < 30; i++) {
				targetImage.headerData[i] = targetChecksum[i - 26];
			}

			byte[] outputData = new byte[targetImage.headerData.length + targetImage.rgbData.length];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetImage.headerData);
			os.write(targetImage.rgbData);
			outputData = os.toByteArray();
			
			// Zieldatei schreiben
			Files.write(Paths.get(outPath), outputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");

			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert ProPra komprimiert nach ProPra komprimiert
	protected static void convertCProPraToCProPra(String inPath, String outPath) throws IOException {
		try {
			// Quelldaten 1:1 in Zieldaten überführen und Zieldatei schreiben
			byte[] inputData = Files.readAllBytes(Paths.get(inPath));
			Files.write(Paths.get(outPath), inputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");

			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}

	// Konvertiert ProPra unkomprimiert nach ProPra komprimiert
	protected static void convertUProPraToCProPra(String inPath, String outPath) throws IOException {
		try {
			// Quelldatei einlesen, leeres Zielformat erzeugen
			ProPraImage sourceImage = new ProPraImage(inPath);
			ProPraImage targetImage = new ProPraImage();

			// Header für ProPra-Zieldatei aufbauen
			byte[] formatId = { 80, 114, 111, 80, 114, 97, 87, 105, 83, 101, 50, 50 };
			for (int i = 0; i < 12; i++) {
				targetImage.headerData[i] = formatId[i];
			}

			targetImage.headerData[12] = 1; // byte 12: compressionType (0=unc, 1=RLE)

			targetImage.headerData[13] = sourceImage.headerData[13]; // bytes 13-14: width
			targetImage.headerData[14] = sourceImage.headerData[14];

			targetImage.headerData[15] = sourceImage.headerData[15]; // bytes 15-16: height
			targetImage.headerData[16] = sourceImage.headerData[16];

			targetImage.headerData[17] = 24; // byte 17: bitsPerDot

			// RGB-Daten komprimieren und austauschen
			Compressor comp = new Compressor();
			byte[] compressedData = comp.compress(sourceImage.rgbData);
			targetImage.rgbData = compressedData;

			// Datensegmentgröße berechnen
			long dataSegmentSize = targetImage.rgbData.length;
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putLong(dataSegmentSize);
			byte[] targetDataSegmentSize = bb.array();
			for (int i = 0; i < 8; i++) {
				targetImage.headerData[i + 18] = targetDataSegmentSize[i];
			}

			// Prüfsumme berechnen
			byte[] targetChecksum = ChecksumTools.getChecksum(targetImage.rgbData); // bytes 26-29: checksum
			for (int i = 26; i < 30; i++) {
				targetImage.headerData[i] = targetChecksum[i - 26];
			}

			byte[] outputData = new byte[targetImage.headerData.length + targetImage.rgbData.length];
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetImage.headerData);
			os.write(targetImage.rgbData);
			outputData = os.toByteArray();
			
			// Zieldatei schreiben
			Files.write(Paths.get(outPath), outputData);

			File myFile = new File(outPath);
			if (myFile.isFile()) {
				System.out.println("...erfolgreich konvertiert!");
			}
		} catch (Exception e) {
			System.err.println("Lese-/Schreibfehler!");
			System.exit(123);
		}
	}
}