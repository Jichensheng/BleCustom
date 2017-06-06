package com.heshun.blecustom.wheel;

import java.util.Calendar;

/**
 * author：Jics
 * 2017/4/14 13:08
 */
public class GetCurrentTime {
	Calendar calendar = Calendar.getInstance();

	public String getTime() {
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth = calendar.get(Calendar.MONTH) + 1;
		int curDay = calendar.get(Calendar.DAY_OF_MONTH);
		int curHour = calendar.get(Calendar.HOUR_OF_DAY);
		int curMinute = calendar.get(Calendar.MINUTE);
		int curSecond = calendar.get(Calendar.SECOND);
		StringBuffer sb = new StringBuffer();
		sb.append(format(curYear)).append("-")
				.append(format(curMonth)).append("-")
				.append(format(curDay));

		sb.append(" ");
		sb.append(format(curHour))
				.append(":").append(format(curMinute))
				.append(":").append(format(curSecond));
		return sb.toString();
	}
	private String format(int num){
		return String.format("%02d",num);
	}
/*	public static void main(String[] args){
		System.out.println(new GetCurrentTime().getTime());
	}*/
}
