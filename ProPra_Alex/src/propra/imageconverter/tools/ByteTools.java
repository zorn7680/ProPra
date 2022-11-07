package propra.imageconverter.tools;

/* Die Funktionalität dieser Klasse habe ich aus einem Review übernommen, eine
 * vergleichbare Utility-Klasse für die Byte-Umrechnungen wollte ich ohnehin
 * in meiner Implementierung ergänzen.  
 * Nun bin ich im Review über diese Implementierung gestolpert. Sie 
 * erscheint mir hervorragend umgesetzt. Es wäre Unsinn, diese jetzt 
 * nachzuprogrammieren, nur um "es selber gemacht zu haben". Ich habe den
 * Code studiert und verstehe, wie er funktioniert. 
 */

/* This class converts given byte data to different number formats and 
 * vice versa.
 */
public class ByteTools {
    
	// returns an unsigned short from given byte array
	public static short getShortFromByteArray(byte[] bytes, int offset) {
        return (short) (bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8);
    }

	// returns an unsigned int from given byte array
	public static int getIntFromByteArray(byte[] byteArray, int offset) {
        return byteArray[offset] & 0xFF |
        		(byteArray[offset + 1] & 0xFF) << 8 |
        		(byteArray[offset + 2] & 0xFF) << 16 |
        		(byteArray[offset + 3] & 0xFF) << 24;
    }

    // returns an unsigned long from given byte array
	public static long getLongFromByteArray(byte[] byteArray, int offset) {
        return byteArray[offset] & 0xFF |
                (byteArray[offset + 1] & 0xFF) << 8 |
                (byteArray[offset + 2] & 0xFF) << 16 |
                (byteArray[offset + 3] & 0xFF) << 24 |
                (byteArray[offset + 4] & 0xFF) << 32 |
                (byteArray[offset + 5] & 0xFF) << 40 |
                (byteArray[offset + 6] & 0xFF) << 48 |
                (byteArray[offset + 7] & 0xFF) << 56;
    }

    // fills a given byte array with the byte code to a given short value
	public static void setShortToByteArray(byte[] bytes, int offset, short value) {
        bytes[offset] = (byte) (value & 0xFF);
        bytes[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

	// fills a given byte array with the byte code to a given int value 
	public static void setIntToByteArray(byte[] bytes, int offset, int value) {
    	bytes[offset] = (byte) (value & 0xFF);
    	bytes[offset + 1] = (byte) ((value >> 8) & 0xFF);
    	bytes[offset + 2] = (byte) ((value >> 16) & 0xFF);
    	bytes[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }
    
    // fills a given byte array with the byte code to a given long value
	public static void setLongToByteArray(byte[] bytes, int offset, long value) {
        bytes[offset] = (byte) (value & 0xFF);
        bytes[offset + 1] = (byte) ((value >> 8) & 0xFF);
        bytes[offset + 2] = (byte) ((value >> 16) & 0xFF);
        bytes[offset + 3] = (byte) ((value >> 24) & 0xFF);
        bytes[offset + 4] = (byte) ((value >> 32) & 0xFF);
        bytes[offset + 5] = (byte) ((value >> 40) & 0xFF);
        bytes[offset + 6] = (byte) ((value >> 48) & 0xFF);
        bytes[offset + 7] = (byte) ((value >> 56) & 0xFF);
    }
}
