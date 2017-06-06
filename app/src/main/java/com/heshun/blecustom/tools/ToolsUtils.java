package com.heshun.blecustom.tools;

import com.heshun.blecustom.base.Head;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * author：Jics
 * 2017/6/2 14:58
 */
public class ToolsUtils {

	/**
	 * 获取精确到秒的低位在前的时间戳
	 *
	 * @return
	 */
	public static byte[] getSecondTimestamp() {
		String timestamp = String.valueOf(new Date().getTime() / 1000);
		int time = Integer.valueOf(timestamp);
		return intToByte4(time);
	}

	/**
	 * 秒变日期
	 * @param bytes 低位在前
	 * @return
	 */
	public static String byteToSecondTime(byte[] bytes) {
		int intTime = ByteUtils.byte4ToInt(ByteUtils.byteReverse(bytes));
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis((long) intTime * 1000);
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(gc.getTime());
	}

	/**
	 * 将int变byte[]数组（低位在前高位在后）
	 * @param res
	 * @return
	 */
	public static byte[] intToByte4(int res) {
		byte[] times = new byte[4];
		times[0] = (byte) (res & 0x000000ff);
		times[1] = (byte) ((res & 0x0000ff00) >> 8);
		times[2] = (byte) ((res & 0x00ff0000) >> 16);
		times[3] = (byte) ((res & 0xff000000) >> 24);
		return times;
	}

	/**
	 * 将byte[]数组转成int (低位在前)
	 * @param arr 低位在前
	 * @return
	 */
	public static int byte4ToInt(byte[] arr){
		if (arr == null || arr.length != 4) {
			throw new IllegalArgumentException("byte数组必须不为空,并且是4位!");
		}
		return ((arr[3] & 0xff) << 24) | ((arr[2] & 0xff) << 16) | ((arr[1] & 0xff) << 8) | ((arr[0] & 0xff));
	}

	/**
	 * 多个数组合并
	 * @param first
	 * @param rest
	 * @return
	 */
	public static  byte[] concatAll(byte[] first, byte[]... rest) {
		int totalLength = first.length;
		for (byte[] array : rest) {
			totalLength += array.length;
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/**
	 *
	 * checksum计算(除了checksum位的其他位生成校验)
	 * @return
	 */
	public static byte checkSum(Head head, byte[] body){
		byte[] temp=new byte[7];
		byte[] headArray=head.getHeadArray();

		for (int i = 0; i < headArray.length-1; i++) {
			temp[i]=headArray[i];
		}
		byte[] res=ToolsUtils.concatAll(temp,body);//除了checksum位的其它为组成的数组

		byte checksum=0x00;
		for (byte re : res) {
			checksum^=re;
		}
		return (byte) (checksum&0xff);
	}



}
