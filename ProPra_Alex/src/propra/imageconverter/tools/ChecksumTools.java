package propra.imageconverter.tools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ChecksumTools {
	private static byte[] data;
	private static int[] Ai;
	private static int x = 65521;
	private static int P;
	private static int An = 0;

	/* Baut Umgebung für Prüfsummenberechnung auf */
	public static byte[] getChecksum(byte[] byteData) {
		byte[] checksum;
		data = new byte[byteData.length];
		for (int i = 0; i < byteData.length; i++) {
			data[i] = byteData[i];
		}
		checksum = calculateChecksum();
		return checksum;
	}

	/* Berechnet die Prüfsumme */
	private static byte[] calculateChecksum() {
		int n = data.length;
		Ai = new int[n + 1];

		// 1. berechne A_n
		Ai[0] = 0;
		if (n != 0) {
			for (int i = 0; i < n; i++) {
				Ai[i + 1] = (Ai[i] + (i + 1) + Byte.toUnsignedInt(data[i])) % x;
				An = Ai[i + 1];
			}
		}

		// 2. berechne B_n
		int Bn = 1;
		for (int i = 1; i <= n; i++) {
			Bn = (Bn + Ai[i]) % x;
		}

		// 3. berechne P
		P = An * 65536 + Bn;
		
//		System.out.println("unsigned long P = " + Integer.toUnsignedLong(P));
//		System.out.println("int P as HEX = " + Integer.toHexString(P));

		// 4. Prüfsumme als Byte-Array zurückgeben
		ByteBuffer b = ByteBuffer.allocate(4);
		b.order(ByteOrder.LITTLE_ENDIAN);
		b.putInt(P);
		byte result[] = b.array();
		return result;
	}
	
	/* Vergleicht Prüfsumme aus gegebenem Header mit eigener Berechnung */
	public static boolean validateChecksum(byte[] fileChecksum, byte[] rgbData) {
		byte[] calculatedChecksum = getChecksum(rgbData);
		return (calculatedChecksum[0] == fileChecksum[0] && calculatedChecksum[1] == fileChecksum[1]
				&& calculatedChecksum[2] == fileChecksum[2] && calculatedChecksum[3] == fileChecksum[3]);
	}
}
