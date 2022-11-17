package propra.imageconverter.compression;

/*
 * CHANGELOG Abschnitt 2
 * - Klasse neu eingeführt, keine Vorgängerversion vorhanden
 */

/* 
 * Diese Klasse repräsentiert ein Steuerbyte für Dekompressions-/Kompressions-
 * Operationen. Sie wird sowohl für die Kompression als auch für die 
 * Dekompression verwendet (jeweils mit dem entspr. Konstruktor).
 */

public class ControlByte {
	final byte STEUERBIT = (byte) 0b10000000;
	final int UNSIGNED_MASK = 0xFF;

	private int type;
	private int length;
	private int result;

	public ControlByte(byte inByte) {
		this.type = (STEUERBIT & inByte) != 0 ? 1 : 0;
		this.length = (~STEUERBIT & inByte) + 1;
	}

	public ControlByte(boolean inType, int length) {
		this.length = length;
		this.type = (inType == true ? 1 : 0);
		this.result = (type == 1 ? length + 128 : length);
	}

	public int getType() {
		return this.type;
	}

	public int getLength() {
		return this.length;
	}

	public byte getByteValue() {
		return (byte) result;
	}
}