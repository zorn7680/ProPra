package propra.imageconverter.tools;


public class ByteTools {
    public static short getUnsignedShortFromByteArray(byte[] bytes, int offset) {
        return (short) (bytes[offset] & 0xFF | (bytes[offset + 1] & 0xFF) << 8);
    }

    public static int getUnsignedIntFromByteArray(byte[] byteArray, int offset) {
        return byteArray[offset] & 0xFF |
                (byteArray[offset + 1] & 0xFF) << 8 |
                (byteArray[offset + 2] & 0xFF) << 16 |
                (byteArray[offset + 3] & 0xFF) << 24;
    }

    public static long getUnsignedLongFromByteArray(byte[] byteArray, int offset) {
        return byteArray[offset] & 0xFF |
                (byteArray[offset + 1] & 0xFF) << 8 |
                (byteArray[offset + 2] & 0xFF) << 16 |
                (byteArray[offset + 3] & 0xFF) << 24 |
                (byteArray[offset + 4] & 0xFF) << 32 |
                (byteArray[offset + 5] & 0xFF) << 40 |
                (byteArray[offset + 6] & 0xFF) << 48 |
                (byteArray[offset + 7] & 0xFF) << 56;
    }

    public static void setShortToByteArray(byte[] bytes, int offset, short value) {
        bytes[offset] = (byte) (value & 0xFF);
        bytes[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

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

    public static void setIntToByteArray(byte[] bytes, int offset, int value) {
        bytes[offset] = (byte) (value & 0xFF);
        bytes[offset + 1] = (byte) ((value >> 8) & 0xFF);
        bytes[offset + 2] = (byte) ((value >> 16) & 0xFF);
        bytes[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }
}
