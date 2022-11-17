package propra.imageconverter.tools;

/*
 * CHANGELOG Abschnitt 2
 * - Klasse neu eingeführt, keine Vorgängerversion
 */

/* Klasse konvertiert ein 4- bzw. 8-stelliges byte Array in einen integer
 * bzw. einen long Wert.
 * 
 * Hinweis: Für diese Klasse habe ich mich durch ein Review inspirieren lassen
 * und mich auch am Code orientiert. Eine vergleichbare Utility-Klasse für 
 * die Byte-Umrechnungen wollte ich bereits in Abschnitt 1 schreiben, dafür 
 * war aber leider nicht mehr die nötige Zeit.   
 */


public class ByteTools {
    
	// Erzeugt ein int aus dem Byte-Array
	public static int getIntFromByteArray(byte[] byteArray, int offset) {
        return byteArray[offset] & 0xFF |
        		(byteArray[offset + 1] & 0xFF) << 8 |
        		(byteArray[offset + 2] & 0xFF) << 16 |
        		(byteArray[offset + 3] & 0xFF) << 24;
    }

    // Erzeugt ein long aus dem Byte-Array
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
}
