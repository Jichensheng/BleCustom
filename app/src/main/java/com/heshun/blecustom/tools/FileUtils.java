package com.heshun.blecustom.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.heshun.blecustom.entity.ElectricityParameter;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类
 */
public class FileUtils {
	public static final int SIZETYPE_B = 1;// 获取文件大小单位为B的double值
	public static final int SIZETYPE_KB = 2;// 获取文件大小单位为KB的double值
	public static final int SIZETYPE_MB = 3;// 获取文件大小单位为MB的double值
	public static final int SIZETYPE_GB = 4;// 获取文件大小单位为GB的double值

	public static final int FILE_SELECT_CODE = 0x77;

	/**
	 * 打开文件选择器
	 *
	 * @param activity
	 */
	public static void showFileChooser(Activity activity) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			activity.startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(activity, "没有文件管理器", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 获取文件路径
	 *
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getPath(Context context, Uri uri) {

		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = {"_data"};
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * 字符串数据解析
	 * JLZ=VOL:220.3V,COR:16.3A,ELC:3.41Kwh,TIME:23MIN,STATE:0
	 *
	 * @param s
	 */
	@Deprecated
	public static String analysisWords(String s) {
		StringBuffer stringBuffer = new StringBuffer();
		String temp = s.substring(s.indexOf("JLZ=") + "JLZ=".length());
		for (String ss : temp.split(",")) {
			int cuser = 0;
			boolean isNum = false;
			for (int i = 0; i < ss.length(); i++) {
				if (ss.charAt(i) >= '0' && ss.charAt(i) <= '9') {
					isNum = true;
				} else if (isNum && (((ss.charAt(i) >= 'A' && ss.charAt(i) <= 'Z')) || ((ss.charAt(i) >= 'a' && ss.charAt(i) <= 'z')))) {
					cuser = i;
					isNum = false;
					break;
				} else
					isNum = false;
			}
			StringBuffer sb = new StringBuffer(ss);
			if (cuser != 0) {
				sb.insert(cuser, "\t");
			}
			sb.insert(sb.indexOf(":") + 1, "\t");
			stringBuffer.append(sb.toString() + "\n");
		}
		String original = stringBuffer.toString();
		return original.substring(0, original.indexOf("=JLZ"));
	}

	/**
	 * 字符串数据解析
	 * JLZ=VOL:220.3V,COR:16.3A,ELC:3.41Kwh,TIME:23MIN,STATE:0
	 *
	 * @param s
	 */
	public static List<ElectricityParameter> analysisWords2entity(String s) {
		List<ElectricityParameter> electricityParameters = new ArrayList<>();
		ElectricityParameter ep;
		String name;
		String value;
		String uint;
		String temp;
		try {
			if (s.contains("=JLZ")&&s.contains("JLZ=")) {
				temp = s.substring(s.lastIndexOf("JLZ=") + "JLZ=".length(), s.indexOf("=JLZ"));
			} else
				temp = s.substring(s.lastIndexOf("JLZ=") + "JLZ=".length());
			System.out.println(temp);
			for (String ss : temp.split(",")) {
				int cuser = 0;
				boolean isNum = false;
				for (int i = 0; i < ss.length(); i++) {
					if (ss.charAt(i) >= '0' && ss.charAt(i) <= '9') {
						isNum = true;
					} else if (isNum && (((ss.charAt(i) >= 'A' && ss.charAt(i) <= 'Z')) || ((ss.charAt(i) >= 'a' && ss.charAt(i) <= 'z')))) {
						cuser = i;
						isNum = false;
						break;
					} else
						isNum = false;
				}
				if (cuser != 0) {
					name = translate(ss.substring(0, ss.indexOf(":")));
					value = ss.substring(ss.indexOf(":") + 1, cuser);
					uint = ss.substring(cuser);
				} else {
					name = translate(ss.substring(0, ss.indexOf(":")));
					value = ss.substring(ss.indexOf(":") + 1);
					uint = "";
				}
				ep = new ElectricityParameter(name, value, uint);
				electricityParameters.add(ep);
			}
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
//			return FileUtils.analysisWords2entity("JLZ=VOL:0V,CUR:0A,ELC:0Kwh,TIME:0MIN,STATE:0=JLZ");
		}
		return electricityParameters;
	}

	/**
	 * 汉化
	 *
	 * @param name
	 * @return
	 */
	private static String translate(String name) {
		String result;
		switch (name.toLowerCase()) {
			case "vol":
				result = "电压";
				break;
			case "cur":
				result = "电流";
				break;
			case "elc":
				result = "电量";
				break;
			case "time":
				result = "时间";
				break;
			case "state":
				result = "状态";
				break;
			default:
				result = name;
		}
		return result;
	}

	/**
	 * 获取文件指定文件的指定单位的大小
	 *
	 * @param filePath 文件路径
	 * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
	 * @return double值的大小
	 */
	public static double getFileOrFilesSize(String filePath, int sizeType) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("获取文件大小", "获取失败!");
		}
		return FormetFileSize(blockSize, sizeType);
	}

	/**
	 * 调用此方法自动计算指定文件或指定文件夹的大小
	 *
	 * @param filePath 文件路径
	 * @return 计算好的带B、KB、MB、GB的字符串
	 */
	public static String getAutoFileOrFilesSize(String filePath) {
		File file = new File(filePath);
		long blockSize = 0;
		try {
			if (file.isDirectory()) {
				blockSize = getFileSizes(file);
			} else {
				blockSize = getFileSize(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("获取文件大小", "获取失败!");
		}
		return FormetFileSize(blockSize);
	}

	/**
	 * 获取指定文件大小
	 *
	 * @return
	 * @throws Exception
	 */
	private static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			file.createNewFile();
			Log.e("获取文件大小", "文件不存在!");
		}
		return size;
	}

	/**
	 * 获取指定文件夹
	 *
	 * @param f
	 * @return
	 * @throws Exception
	 */
	private static long getFileSizes(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSizes(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}
		}
		return size;
	}

	/**
	 * 转换文件大小
	 *
	 * @param fileS
	 * @return
	 */
	private static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileS == 0) {
			return wrongSize;
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	/**
	 * 转换文件大小,指定转换的类型
	 *
	 * @param fileS
	 * @param sizeType
	 * @return
	 */
	private static double FormetFileSize(long fileS, int sizeType) {
		DecimalFormat df = new DecimalFormat("#.00");
		double fileSizeLong = 0;
		switch (sizeType) {
			case SIZETYPE_B:
				fileSizeLong = Double.valueOf(df.format((double) fileS));
				break;
			case SIZETYPE_KB:
				fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
				break;
			case SIZETYPE_MB:
				fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
				break;
			case SIZETYPE_GB:
				fileSizeLong = Double.valueOf(df
						.format((double) fileS / 1073741824));
				break;
			default:
				break;
		}
		return fileSizeLong;
	}
	public static String bin2hex(String bin) {
		char[] digital = "0123456789ABCDEF".toCharArray();
		StringBuffer sb = new StringBuffer("");
		byte[] bs = bin.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(digital[bit]);
			bit = bs[i] & 0x0f;
			sb.append(digital[bit]);
		}
		return sb.toString();
	}

	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0) {
			throw new IllegalArgumentException("长度不是偶数");
		}
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			// 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		b = null;
		return b2;
	}
}