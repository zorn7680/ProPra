package propra.imageconverter;

public class ImageFile {

	public byte[] inputByteData;
	public String[] inputHexData;
	public byte[] headerData;
	public byte[] rgbData;

	public void printHeader(String mode) {
		switch (mode) {
		case "hex":
			System.out.println("Header im HEX-Format");
			for (int i = 0; i < headerData.length; i++) {
				System.out.println("Byte " + i + ": " + Integer.toHexString(headerData[i] & 0xff));
			}
			System.out.println("\n");
			break;
		case "byte":
			System.out.println("Header im Original-Byte-Format");
			for (int i = 0; i < headerData.length; i++) {
				System.out.println("Byte " + i + ": " + headerData[i]);
			}
			System.out.println("\n");
			break;
		default:
			break;
		}
	}
}
