package propra.imageconverter.compression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Compressor {

	private ByteArrayInputStream inStream;
	private ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream pixelBuffer = new ByteArrayOutputStream();

	private byte[] result;
	private byte[] p1 = null;
	private byte[] p2 = null;
	private ControlByte cb;
	private int repCounter = 0;
	private boolean blockRunning = false;
	private boolean wh = false;



	public byte[] compress(byte[] inputData) throws IOException {
		System.out.println("\nCompressionTools.compress() invoked!\n");

		inStream = new ByteArrayInputStream(inputData);

		do {
			// code (1)
			p2 = inStream.readNBytes(1);
			if (Arrays.equals(p1, p2)) {
				if (blockRunning == true) {
					// code (5)
					if (repCounter == 127) {
						writeToOutputStream(true, 1);
					} else {
						// do nothing, continue loop
					}
				} else {
					// code (6)
					writeToOutputStream(true, 1);
				}
			} else {
				if (blockRunning == true) {
					// code (7)
					writeToOutputStream(true, 1);
				} else {
					if (p1 != null) {
						// code (8)
					}
					// code (9)
					if (repCounter == 127) {
						// go to code 10
						writeToOutputStream(true, 1);
					} else {
						// nothing, continue loop
					}
				}

				// code (10)
				if (wh == true) {
					// code (11)
				} else {
					// code (12)
				}
				// code (13)
				if (wh == true) {
					// code (14)
				} else {
					// code (15)
				}
			}
		} while (inStream.available() >= 0); // evtl. auch >= -1...ausprobieren!

		if (pixelBuffer != null) {
			// code (2)
		} else if (blockRunning == false) {
			// code (3)
			// code (4)
		}

		result = outStream.toByteArray();
		inStream.close();
		outStream.close();
		return result;

	}

	private void writeToOutputStream(boolean wh, int repCounter) throws IOException {
		// createControlByte(wh, repCounter)
		cb = null;
		if (wh == true) {
			repCounter = 0;
		} else {
			repCounter = 2;
		}
		outStream.write(cb.generateControlByte(wh, repCounter));
		if (wh == true) {
			outStream.write(p1);
		} else {
			outStream.write(pixelBuffer.toByteArray());
			pixelBuffer = new ByteArrayOutputStream();
		}
	}
}
