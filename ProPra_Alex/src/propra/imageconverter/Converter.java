package propra.imageconverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.File;

public final class Converter {

	/* Konvertiert ProPra-Format zu ProPra-Format */
	protected static void convertProPraToProPra(String inPath, String outPath) throws IOException {
		try {
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

	/* Konvertiert ProPra-Format zu TGA-Format */
	protected static void convertProPraToTGA(String inPath, String outPath) throws IOException {
		try {
			ProPraImage sourceImage = new ProPraImage(inPath);
			TGAImage targetImage = new TGAImage();

			/* Header für TGA-Zieldatei aufbauen */
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

			/* RGB-Daten-Reihenfolge ändern von ProPra(R-B-G / 0-2-1) zu TGA(B-G-R / 2-1-0 ) */
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

	/* Konvertiert TGA-Format zu TGA-Format */
	protected static void convertTGAToTGA(String inPath, String outPath) throws IOException {
		try {
			TGAImage sourceImage = new TGAImage(inPath);

			byte[] targetHeaderData = sourceImage.headerData;
			byte[] targetRgbData = sourceImage.rgbData;

			byte[] outputData = new byte[targetHeaderData.length + targetRgbData.length];

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetHeaderData);
			os.write(targetRgbData);
			outputData = os.toByteArray();
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

	/* Konvertiert TGA-Format zu ProPra-Format */
	protected static void convertTGAToProPra(String inPath, String outPath) throws IOException {
		try {
			TGAImage sourceImage = new TGAImage(inPath);
			ProPraImage targetImage = new ProPraImage();

			/* Header für ProPra-Zieldatei aufbauen */
			byte[] formatId = { 80, 114, 111, 80, 114, 97, 87, 105, 83, 101, 50, 50 }; // bytes 0 - 11: formatId ->
																						// "ProPraWiSe22"
			for (int i = 0; i < 12; i++) {
				targetImage.headerData[i] = formatId[i];
			}

			targetImage.headerData[12] = 0; // byte 12: compressionType

			targetImage.headerData[13] = sourceImage.headerData[12]; // bytes 13-14: width
			targetImage.headerData[14] = sourceImage.headerData[13];

			targetImage.headerData[15] = sourceImage.headerData[14]; // bytes 15-16: height
			targetImage.headerData[16] = sourceImage.headerData[15];

			targetImage.headerData[17] = 24; // byte 17: bitsPerDot

			int imageWidth = Integer.parseInt((sourceImage.inputHexData[13] + sourceImage.inputHexData[12]), 16); // bytes 18-25: dataSegmentSize
			int imageHeight = Integer.parseInt((sourceImage.inputHexData[15] + sourceImage.inputHexData[14]), 16);
			long dataSegmentSize = (long) imageWidth * (long) imageHeight * 3;
			ByteBuffer bb = ByteBuffer.allocate(8);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putLong(dataSegmentSize);
			byte[] targetDataSegmentSize = bb.array();
			for (int i = 0; i < 8; i++) {
				targetImage.headerData[i + 18] = targetDataSegmentSize[i];
			}

			/* RGB-Reihenfolge ändern von TGA(B-G-R / 2-1-0) zu ProPra(R-B-G / 0-2-1 ) */
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

			byte[] targetChecksum = Checksum.getChecksum(targetImage.rgbData); // bytes 26-29: checksum
			for (int i = 26; i < 30; i++) {
				targetImage.headerData[i] = targetChecksum[i - 26];
			}

			byte[] outputData = new byte[targetImage.headerData.length + targetImage.rgbData.length];

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			os.write(targetImage.headerData);
			os.write(targetImage.rgbData);
			outputData = os.toByteArray();
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
