package com.heshun.blecustom.tools;

import java.util.zip.CRC32;

/**
 * 
 * <pre>
 * 基本数据类型转换(主要是byte和其它类型之间的互转).
 * </pre>
 * 
 * @author F.Fang
 * @version $Id: ByteUtils.java, v 0.1 2014年11月9日 下午11:23:21 F.Fang Exp $
 */
public class ByteUtils {


	/**
	 * byte[]数组反转
	 * @param res
	 * @return
	 */
	public static byte[] byteReverse (byte[] res){
		byte[] dst=new byte[res.length];
		for (int i = 0; i < res.length; i++) {
			dst[i]=res[res.length-i-1];
		}
		return dst;
	}

	/**
	 * 
	 * <pre>
	 * 将4个byte数字组成的数组合并为一个float数.
	 * </pre>
	 * 
	 * @param arr
	 * @return
	 */
	public static float byte4ToFloat(byte[] arr) {
		if (arr == null || arr.length != 4) {
			throw new IllegalArgumentException("byte数组必须不为空,并且是4位!");
		}
		int i = byte4ToInt(arr);
		return Float.intBitsToFloat(i);
	}

	/**
	 * 
	 * 将一个float数字转换为4个byte数字组成的数组.
	 * 
	 * @param f
	 * @return
	 */
	public static byte[] floatToByte4(float f) {
		int i = Float.floatToIntBits(f);
		return intToByte4(i);
	}

	/**
	 * 
	 * 将八个byte数字组成的数组转换为一个double数字.
	 * 
	 * @param arr
	 * @return
	 */
	public static double byte8ToDouble(byte[] arr) {
		if (arr == null || arr.length != 8) {
			throw new IllegalArgumentException("byte数组必须不为空,并且是8位!");
		}
		long l = byte8ToLong(arr);
		return Double.longBitsToDouble(l);
	}

	/**
	 * 
	 * 将一个double数字转换为8个byte数字组成的数组.
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] doubleToByte8(double i) {
		long j = Double.doubleToLongBits(i);
		return longToByte8(j);
	}

	/**
	 * 
	 * 将一个char字符转换为两个byte数字转换为的数组.
	 * 
	 * @param c
	 * @return
	 */
	public static byte[] charToByte2(char c) {
		byte[] arr = new byte[2];
		arr[0] = (byte) (c >> 8);
		arr[1] = (byte) (c & 0xff);
		return arr;
	}

	/**
	 * 
	 * 将2个byte数字组成的数组转换为一个char字符.
	 * 
	 * @param arr
	 * @return
	 */
	public static char byte2ToChar(byte[] arr) {
		if (arr == null || arr.length != 2) {
			throw new IllegalArgumentException("byte数组必须不为空,并且是2位!");
		}
		return (char) (((char) (arr[0] << 8)) | ((char) arr[1]));
	}

	/**
	 * 
	 * 将一个16位的short转换为长度为2的8位byte数组.
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] shortToByte2(Short s) {
		byte[] arr = new byte[2];
		arr[0] = (byte) (s >> 8);
		arr[1] = (byte) (s & 0xff);
		return arr;
	}

	/**
	 * 
	 * 长度为2的8位byte数组转换为一个16位short数字.
	 * 
	 * @param arr
	 * @return
	 */
	public static short byte2ToShort(byte[] arr) {
		if (arr != null && arr.length != 2) {
			throw new IllegalArgumentException("byte数组必须不为空,并且是2位!");
		}
		return (short) (((short) arr[0] << 8) | ((short) arr[1] & 0xff));
	}

	/**
	 * 
	 * 将short转换为长度为16的byte数组. 实际上每个8位byte只存储了一个0或1的数字 比较浪费.
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] shortToByte16(short s) {
		byte[] arr = new byte[16];
		for (int i = 15; i >= 0; i--) {
			arr[i] = (byte) (s & 1);
			s >>= 1;
		}
		return arr;
	}

	public static short byte16ToShort(byte[] arr) {
		if (arr == null || arr.length != 16) {
			throw new IllegalArgumentException("byte数组必须不为空,并且长度为16!");
		}
		short sum = 0;
		for (int i = 0; i < 16; ++i) {
			sum |= (arr[i] << (15 - i));
		}
		return sum;
	}

	/**
	 * 
	 * 将32位int转换为由四个8位byte数字.
	 * 
	 * @param sum
	 * @return
	 */
	public static byte[] intToByte4(int sum) {
		byte[] arr = new byte[4];
		arr[0] = (byte) (sum >> 24);
		arr[1] = (byte) (sum >> 16);
		arr[2] = (byte) (sum >> 8);
		arr[3] = (byte) (sum & 0xff);
		return arr;
	}

	/**
	 * 将长度为4的8位byte数组转换为32位int.
	 * 
	 * @param arr
	 * @return
	 */
	public static int byte4ToInt(byte[] arr) {
		if (arr == null || arr.length != 4) {
			throw new IllegalArgumentException("byte数组必须不为空,并且是4位!");
		}
		return (int) (((arr[0] & 0xff) << 24) | ((arr[1] & 0xff) << 16) | ((arr[2] & 0xff) << 8) | ((arr[3] & 0xff)));
	}

	/**
	 * 
	 * 将长度为8的8位byte数组转换为64位long.
	 * 
	 * 0xff对应16进制,f代表1111,0xff刚好是8位 byte[]
	 * arr,byte[i]&0xff刚好满足一位byte计算,不会导致数据丢失. 如果是int计算. int[] arr,arr[i]&0xffff
	 * 
	 * @param arr
	 * @return
	 */
	public static long byte8ToLong(byte[] arr) {
		if (arr == null || arr.length != 8) {
			throw new IllegalArgumentException("byte数组必须不为空,并且是8位!");
		}
		return (long) (((long) (arr[0] & 0xff) << 56) | ((long) (arr[1] & 0xff) << 48) | ((long) (arr[2] & 0xff) << 40)
				| ((long) (arr[3] & 0xff) << 32) | ((long) (arr[4] & 0xff) << 24) | ((long) (arr[5] & 0xff) << 16)
				| ((long) (arr[6] & 0xff) << 8) | ((long) (arr[7] & 0xff)));
	}

	public static long byte4ToLong(byte[] high, byte[] low) {

		byte[] arr = new byte[] { 0, 0, 0, 0, high[0], high[1], low[0], low[1] };

		return (long) (((long) (arr[0] & 0xff) << 56) | ((long) (arr[1] & 0xff) << 48) | ((long) (arr[2] & 0xff) << 40)
				| ((long) (arr[3] & 0xff) << 32) | ((long) (arr[4] & 0xff) << 24) | ((long) (arr[5] & 0xff) << 16)
				| ((long) (arr[6] & 0xff) << 8) | ((long) (arr[7] & 0xff)));
	}

	public static long byte4ToLong(byte[] b) {

		byte[] arr = new byte[] { 0, 0, 0, 0, b[0], b[1], b[2], b[3] };

		return (long) (((long) (arr[0] & 0xff) << 56) | ((long) (arr[1] & 0xff) << 48) | ((long) (arr[2] & 0xff) << 40)
				| ((long) (arr[3] & 0xff) << 32) | ((long) (arr[4] & 0xff) << 24) | ((long) (arr[5] & 0xff) << 16)
				| ((long) (arr[6] & 0xff) << 8) | ((long) (arr[7] & 0xff)));
	}

	/**
	 * 将一个long数字转换为8个byte数组组成的数组.
	 */
	public static byte[] longToByte8(long sum) {
		byte[] arr = new byte[8];
		arr[0] = (byte) (sum >> 56);
		arr[1] = (byte) (sum >> 48);
		arr[2] = (byte) (sum >> 40);
		arr[3] = (byte) (sum >> 32);
		arr[4] = (byte) (sum >> 24);
		arr[5] = (byte) (sum >> 16);
		arr[6] = (byte) (sum >> 8);
		arr[7] = (byte) (sum & 0xff);
		return arr;
	}

	/**
	 * 
	 * 将int转换为32位byte. 实际上每个8位byte只存储了一个0或1的数字 比较浪费.
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] intToByte32(int num) {
		byte[] arr = new byte[32];
		for (int i = 31; i >= 0; i--) {
			// &1 也可以改为num&0x01,表示取最低位数字.
			arr[i] = (byte) (num & 1);
			// 右移一位.
			num >>= 1;
		}
		return arr;
	}

	/**
	 * 
	 * 将长度为32的byte数组转换为一个int类型值. 每一个8位byte都只存储了0或1的数字.
	 * 
	 * @param arr
	 * @return
	 */
	public static int byte32ToInt(byte[] arr) {
		if (arr == null || arr.length != 32) {
			throw new IllegalArgumentException("byte数组必须不为空,并且长度是32!");
		}
		int sum = 0;
		for (int i = 0; i < 32; ++i) {
			sum |= (arr[i] << (31 - i));
		}
		return sum;
	}

	/**
	 * 
	 * 将长度为64的byte数组转换为一个long类型值. 每一个8位byte都只存储了0或1的数字.
	 * 
	 * @param arr
	 * @return
	 */
	public static long byte64ToLong(byte[] arr) {
		if (arr == null || arr.length != 64) {
			throw new IllegalArgumentException("byte数组必须不为空,并且长度是64!");
		}
		long sum = 0L;
		for (int i = 0; i < 64; ++i) {
			sum |= ((long) arr[i] << (63 - i));
		}
		return sum;
	}

	/**
	 * 
	 * 将一个long值转换为长度为64的8位byte数组. 每一个8位byte都只存储了0或1的数字.
	 * 
	 * @param sum
	 * @return
	 */
	public static byte[] longToByte64(long sum) {
		byte[] arr = new byte[64];
		for (int i = 63; i >= 0; i--) {
			arr[i] = (byte) (sum & 1);
			sum >>= 1;
		}
		return arr;
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 * 
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static String getHexString(byte[] b) {
		if (null == b)
			return "";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			sb.append(getHexString(b[i]));
		}
		return sb.toString();
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String getHexString(byte b) {
		String hex = Integer.toHexString(b & 0xFF);
		if (hex.length() == 1)
			return '0' + hex;
		return hex;
	}

	// 合并两个byte数组
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	// 合并N个byte数组
	public static byte[] byteMergerMore(byte[]... byteArr) {
		int length = 0;
		int flag = 0;
		for (byte[] barr : byteArr) {
			length += barr.length;
		}

		byte[] result = new byte[length];
		for (byte[] barr : byteArr) {
			for (byte b : barr) {
				result[flag] = b;
				flag++;
			}
		}

		return result;
	}

	// 数组倒置
	public static void arrayReverse(byte a[]) {
		int len = a.length;
		for (int i = 0; i < len / 2; i++) {
			byte tmp = a[i];
			a[i] = a[len - 1 - i];
			a[len - 1 - i] = tmp;
		}
	}

	// CRC32
	public static byte[] getCRC32(byte[] bytes) {
		CRC32 crc32 = new CRC32();
		crc32.update(bytes);
		// return (int)crc32.getValue();
		int crc = (int) crc32.getValue();
		return ByteUtils.intToByte4(crc);
	}

	public static void main(String[] args) {
		// 0111 1111 1111 1111
		// 0011 1111 1111 1111
		short a = (short) 0x7fff;
		int b = a >> 1;

		System.out.println(b);
	}

	public static void append(byte[] target, int cursor, byte[] source) {
		for (int j = 0; j < source.length; j++)
			target[cursor + j] = source[j];
	}

}