package propra.imageconverter.compression;

/* This class represents a control byte for compression/decompression. */

public class ControlByte {
	final byte STEUERBIT = (byte) 0b10000000;
	final int UNSIGNED_MASK = 0xFF;

	private int type;
	private int length;

	public ControlByte(byte inByte) {
		this.type = (STEUERBIT & inByte) != 0 ? 1 : 0;
		this.length = (~STEUERBIT & inByte) + 1;
	}

	public ControlByte(int length, int type) {
		this.length = length;
		this.type = type;
	}

//	public void printInByte() {
//		System.out.println(inByte);
//	}



	public byte generateControlByte(boolean wh, int repCounter) {
		int result;
		if (wh) {
			result = repCounter;
		} else {
			result = repCounter + 128;
		}

		return (byte) result;
	}

	public int getType() {
		return this.type;
	}

	public int getLength() {
		return this.length;
	}
}
