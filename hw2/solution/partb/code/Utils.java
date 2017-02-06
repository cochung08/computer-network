
public class Utils {

	public static byte[] reverse_byte_array(byte[] arr) {
		int len = arr.length;
		byte[] newArr = new byte[len];

		for (int i = 0; i < len; i++) {
			newArr[i] = arr[len - i - 1];

		}
		return newArr;
	}

	public static int byteToInt(byte b) {
		return (b & 0xff);
	}

	public static short byte_2_ToShort(byte[] byte_2) {
		// System.out.println( "ss" +((b[offset] & 0xff) ) );
		// System.out.println( ((b[offset] & 0xff) << 8) );
		//
		// byte value = Byte.parseByte("00000001", 2);
		// System.out.println(value);
		// short value1= (short)( ((value & 0xff) <<7) | (value & 0xff) );
		//
		// System.out.println(value1);
		
		
		
		return (short) (((byte_2[0] & 0xff) << 8) | (byte_2[1] & 0xff));
	}

	public static byte[] int_to_byte_array(int i) {
		byte[] byte_ = new byte[4];

		byte_[0] = (byte) ((i >> 24) & 0xFF);
		byte_[1] = (byte) ((i >> 16) & 0xFF);
		byte_[2] = (byte) ((i >> 8) & 0xFF);
		byte_[3] = (byte) (i & 0xFF);
		return byte_;
	}

}