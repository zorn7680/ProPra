package compression;

public class ControlByte {

	/* Attribute */

	final byte STEUERBIT = (byte) 0b10000000;
	final int UNSIGNED_MASK = 0xFF;

	private int type;
	private int length;
	private byte inByte;

	/* Konstruktoren */

	public ControlByte(byte inByte) {
		this.inByte = inByte;
		this.type = (STEUERBIT & inByte) != 0 ? 1 : 0;
		this.length = (~STEUERBIT & inByte)+1;
	}

	/* Methoden */
	public void printInByte() {
		System.out.println(inByte);
	}
	
	public int getType() {
		return this.type;
	}
	
	public int getLength() {
		return this.length;
	}
}
