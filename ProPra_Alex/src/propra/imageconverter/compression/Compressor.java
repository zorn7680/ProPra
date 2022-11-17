package propra.imageconverter.compression;

/*
 * CHANGELOG Abschnitt 2
 * - Klasse neu eingeführt, keine Vorgängerversion vorhanden
 */

/*
 * Diese Klasse komprimiert den Eingabe-Datensatz gemäß RLE-Spezifikation und
 * gibt ein komprimiertes Byte-Array zurück. Der Algorithmus ist leider
 * während der Planung und Erstellung "mitgewachsen" und ist jetzt sicher 
 * etwas verworrender und komplizierter, als er sein müsste.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class Compressor {
	// Paketgröße begrenzen
	public static final int MAX_PIXEL_COUNT = 127;

	private ByteArrayInputStream inStream;
	private ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream pixelBuffer = new ByteArrayOutputStream();

	private byte[] result;
	private byte[] p1 = null;
	private byte[] p2 = null;
	private byte cb;
	private int repCounter = 1;
	private boolean blockRunning = false;
	private boolean wh = false;
	private boolean firstRun = true;


	// Attribute teilweise re-initialisieren, wenn neues Paket bei Überlauf
	// (max. Paketgröße 127) beginnt
	private void init() {
		p1 = null; 
		p2 = null; 
		
		repCounter = 1;
		blockRunning = false;
		wh = false;
		firstRun = true;
		pixelBuffer = new ByteArrayOutputStream();
	}


	public byte[] compress(byte[] inputData) throws IOException {
		inStream = new ByteArrayInputStream(inputData);
		do {
			p2 = inStream.readNBytes(3);

			if (Arrays.equals(p1, p2)) {
				if (blockRunning == true || firstRun == true) {
					repCounter++;
					wh = true;
					firstRun = false;
					blockRunning = true;

					if (repCounter == MAX_PIXEL_COUNT) {
						writeToOutputStream(wh, repCounter);
						init();
					} else {
						// nix - weiter mit Schleife
					}
				} else {
					wh = false;
					blockRunning = true;
					repCounter -= 1;
					writeToOutputStream(wh, repCounter);
				}
			} else {
				if (blockRunning == true) {
					wh = true;
					blockRunning = false;
					writeToOutputStream(wh, repCounter);
				} else {
					if (p1 != null) {
						pixelBuffer.write(p1);
						repCounter++;
						wh = false;
						firstRun = false;
					}
					p1 = p2;
					if (repCounter == MAX_PIXEL_COUNT) {
						writeToOutputStream(wh, repCounter);
						init();
					} else {
						// nix - weiter mit Schleife
					}
				}
			}

		} while (inStream.available() > 0);


		if (pixelBuffer.size() > 0) {
			// repCounter reduziert!
			cb = (new ControlByte(false, repCounter-1)).getByteValue();
			outStream.write(cb);
			pixelBuffer.write(p2);
			outStream.write(pixelBuffer.toByteArray());
		} else if (blockRunning == false) {
			// repCounter reduziert!
			byte finalCB = 0;

			if (p2 != null) {
				outStream.write(finalCB);
				outStream.write(p2);
			}
		} else if (blockRunning == true) {

			// repCounter reduziert!
			cb = (new ControlByte(true, repCounter-1)).getByteValue();

			outStream.write(cb);
			outStream.write(p2);
		}

		result = outStream.toByteArray();
		inStream.close();
		outStream.close();
		System.out.println();
		return result;
	}


	private void writeToOutputStream(boolean wdh, int repeatCounter) throws IOException {
		
		// repCounter reduziert!
		cb = (new ControlByte(wdh, repeatCounter-1)).getByteValue();

		if (wdh == true) {
			repCounter = 1;
		} else {
			repCounter = 2;
		}

		// repCounter reduziert!
		if (cb != -1) {
			outStream.write(cb);
		}
		if (wdh == true) {
			outStream.write(p1);
			p1 = p2;
		} else {
			outStream.write(pixelBuffer.toByteArray());
			pixelBuffer = new ByteArrayOutputStream();

			if (repeatCounter == Compressor.MAX_PIXEL_COUNT && p2 != null) {
				outStream.write(p2);
			}
		}
	}
}
