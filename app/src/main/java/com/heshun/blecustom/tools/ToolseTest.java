package com.heshun.blecustom.tools;

import java.util.Arrays;

/**
 * author：Jics
 * 2017/6/5 10:35
 */
public class ToolseTest {

	private static String stringToHex(String s) {
		if (s.length() != 0) {
			int temp = Integer.parseInt(s) >= 0 ? Integer.parseInt(s) : (256 + Integer.parseInt(s));
			String hex = s.length() != 0 ? Integer.toHexString(temp) : "0";
			return " 0x" + (hex.length() == 1 ? "0" + hex.toUpperCase() : hex.toUpperCase());
		}
		else return "0x00";
	}

	public static void main(String[] args) {
		System.out.println(Integer.toBinaryString(0xabcd|0xff));
		//2017-06-05 10:44:26
		String time = ToolsUtils.byteToSecondTime(new byte[]{-118, -59, 52, 89});
		System.out.println(time);
		System.out.println(Arrays.toString(ToolsUtils.intToByte4(77)));
//{-86, -73, 0, 0, 23, 0, 0, -5, -15, 3, -25, 3, 4, 122, -100, 0, -12, 1, 61, -59, 52, 89, -118, -59, 52, 89, 77, 0, 0, 0, 10};
		byte[] history=DataSimulation.getHistoryData();
		for (byte b : history) {
			System.out.print(stringToHex(""+b)+" ");
		}

		System.out.println("\n"+ToolsUtils.getPercentage(DataSimulation.getHistoryData(),5));

		System.out.println(Arrays.toString(int2byte(256)));

		System.out.println(Integer.toBinaryString(256));
		System.out.println(ToolsUtils.byte4ToInt(ToolsUtils.intToByte4(-0)));
	}
	public static byte[] int2byte(int res) {
		byte[] targets = new byte[4];
		targets[0] = (byte) (res & 0xff);// 最低位
		targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
		targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
		targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
		return targets;
	}
}
