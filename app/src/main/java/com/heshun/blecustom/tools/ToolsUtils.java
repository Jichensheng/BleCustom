package com.heshun.blecustom.tools;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.heshun.blecustom.activity.BluetoothLeService;
import com.heshun.blecustom.base.Head;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.CRC32;

/**
 * author：Jics
 * 2017/6/2 14:58
 */
public class ToolsUtils {
	public static final String TAG = "jcs_write";
	private static final int MAX_FILE_SIZE = 1048576;//1MB

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
	 *
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
	 *
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
	 * 将32位的long变byte[]数组（低位在前高位在后）
	 * 0xffffffff
	 *
	 * @param res
	 * @return
	 */
	public static byte[] longToByte4(long res) {
		byte[] times = new byte[4];
		times[0] = (byte) (res & 0x000000ff);
		times[1] = (byte) ((res & 0x0000ff00) >> 8);
		times[2] = (byte) ((res & 0x00ff0000) >> 16);
		times[3] = (byte) ((res & 0xff000000) >> 24);
		return times;
	}

	/**
	 * 将byte[]数组转成int (低位在前)
	 *
	 * @param arr 低位在前
	 * @return
	 */
	public static int byte4ToInt(byte[] arr) {
		if (arr == null || arr.length != 4) {
			throw new IllegalArgumentException("byte数组必须不为空,并且是4位!");
		}
		return ((arr[3] & 0xff) << 24) | ((arr[2] & 0xff) << 16) | ((arr[1] & 0xff) << 8) | ((arr[0] & 0xff));
	}

	/**
	 * 多个数组合并
	 *
	 * @param first
	 * @param rest
	 * @return
	 */
	public static byte[] concatAll(byte[] first, byte[]... rest) {
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
	 * checksum计算(除了checksum位的其他位生成校验)
	 *
	 * @return
	 */
	public static byte checkSum(Head head, byte[] body) {
		byte[] temp = new byte[7];
		byte[] headArray = head.getHeadArray();

		for (int i = 0; i < headArray.length - 1; i++) {
			temp[i] = headArray[i];
		}
		byte[] res = ToolsUtils.concatAll(temp, body);//除了checksum位的其它为组成的数组

		byte checksum = 0x00;
		for (byte re : res) {
			checksum ^= re;
		}
		return (byte) (checksum & 0xff);
	}


	/**
	 * 截取文件块，下标从 1 开始
	 *
	 * @param file       完整文件
	 * @param startIndex 开始下标
	 * @param endIndex   结束下标
	 * @return
	 */
	public static byte[] getFileBlock(byte[] file, int startIndex, int endIndex) {
		if (startIndex < 0 || endIndex > file.length) {
			return new byte[0];
		}
		return Arrays.copyOfRange(file, startIndex - 1, endIndex);
	}

	/**
	 * 统一分发数据线程
	 * 拆分成 20B 的包发送
	 *
	 * @param msg
	 * @param mCharacteristic
	 * @param mBluetoothLeService
	 */
	public static void writeMsg(final byte[] msg, final BluetoothGattCharacteristic mCharacteristic, final BluetoothLeService mBluetoothLeService) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int packSize = 20;
				int length = msg.length;
				int packCount = length % packSize == 0 ? length / packSize : length / packSize + 1;//每包数据大小
				for (int i = 1; i <= packCount; i++) {
					if (i == packCount) {
						byte[] bytes = new byte[length % packSize == 0 ? packSize : length % packSize];
						System.arraycopy(msg, (i - 1) * packSize, bytes, 0, bytes.length);
						mCharacteristic.setValue(bytes);
						Log.e(TAG, "第" + i + "包数据: " + Arrays.toString(bytes));
					} else {
						byte[] bytes = new byte[packSize];
						System.arraycopy(msg, (i - 1) * packSize, bytes, 0, packSize);
						mCharacteristic.setValue(bytes);
						Log.e(TAG, "第" + i + "包数据: " + Arrays.toString(bytes));
					}
					if (!mBluetoothLeService.writeCharacteristic(mCharacteristic)) {
						i--;
					}
				}

			}
		}).start();
	}


	/**
	 * 将InputStream转换成byte数组
	 *
	 * @param file
	 * @param in   InputStream
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] inputStreamTOByte(File file, InputStream in) throws IOException {
		double size = FileUtils.getFileOrFilesSize(file, 1);
		if (size >= MAX_FILE_SIZE) {
			return new byte[0];
		} else {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] data = new byte[(int) size];
			int count = -1;
			while ((count = in.read(data, 0, (int) size)) != -1)
				outStream.write(data, 0, count);

			data = null;
			return outStream.toByteArray();
		}

	}

	/**
	 * CRC32
	 *
	 * @param file
	 * @return
	 */
	public static byte[] fileCRC32(byte[] file) {
		CRC32 crc32 = new CRC32();
		crc32.update(file);
		return longToByte4(crc32.getValue());
	}

	/**
	 * 适合单个byte转成16进制字符串
	 *
	 * @param s
	 * @return
	 */
	public static String byteStringToHex(String s) {
		if (s.length() != 0) {
			int temp = Integer.parseInt(s) >= 0 ? Integer.parseInt(s) : (256 + Integer.parseInt(s));
			String hex = s.length() != 0 ? Integer.toHexString(temp) : "0";
			return " 0x" + (hex.length() == 1 ? "0" + hex.toUpperCase() : hex.toUpperCase());
		} else return "0x00";
	}

	public static String getPercentage(byte[] fileblocks, int endIndex) {
		int fileLength = fileblocks.length;
		float percentage = ((float) endIndex) / fileLength;
		percentage = percentage >= 1 ? 1 : percentage;
		return String.format("%.2f", 100 * percentage) + "%";
	}
}
