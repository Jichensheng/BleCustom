package com.heshun.blecustom.wheel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import com.heshun.blecustom.R;

import java.util.Calendar;

/**
 * author：Jics
 * 2017/4/14 12:56
 */
public abstract class GetTime {
	private Context context;
private static final int YEAR_COUNT = 30;
	private WheelView yearWheel,monthWheel,dayWheel,hourWheel,minuteWheel,secondWheel;
	public static String[] yearContent=null;
	public static String[] monthContent=null;
	public static String[] dayContent=null;
	public static String[] hourContent = null;
	public static String[] minuteContent=null;
	public static String[] secondContent=null;

	public GetTime(Context context) {
		this.context = context;
		initTime();
	}

	private void initTime() {
		yearContent = new String[YEAR_COUNT];
		for(int i=0;i<YEAR_COUNT;i++)
			yearContent[i] = String.valueOf(i+2017);

		monthContent = new String[12];
		for(int i=0;i<12;i++)
		{
			monthContent[i]= String.valueOf(i+1);
			if(monthContent[i].length()<2)
			{
				monthContent[i] = "0"+monthContent[i];
			}
		}

		dayContent = new String[31];
		for(int i=0;i<31;i++)
		{
			dayContent[i]=String.valueOf(i+1);
			if(dayContent[i].length()<2)
			{
				dayContent[i] = "0"+dayContent[i];
			}
		}
		hourContent = new String[24];
		for(int i=0;i<24;i++)
		{
			hourContent[i]= String.valueOf(i);
			if(hourContent[i].length()<2)
			{
				hourContent[i] = "0"+hourContent[i];
			}
		}

		minuteContent = new String[60];
		for(int i=0;i<60;i++)
		{
			minuteContent[i]=String.valueOf(i);
			if(minuteContent[i].length()<2)
			{
				minuteContent[i] = "0"+minuteContent[i];
			}
		}
		secondContent = new String[60];
		for(int i=0;i<60;i++)
		{
			secondContent[i]=String.valueOf(i);
			if(secondContent[i].length()<2)
			{
				secondContent[i] = "0"+secondContent[i];
			}
		}
	}
public abstract void getTimeString(final String sb);
	public void makeDailag(){
		View view = ((LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.time_picker, null);

		Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(Calendar.YEAR);
		int curMonth= calendar.get(Calendar.MONTH)+1;
		int curDay = calendar.get(Calendar.DAY_OF_MONTH);
		int curHour = calendar.get(Calendar.HOUR_OF_DAY);
		int curMinute = calendar.get(Calendar.MINUTE);
		int curSecond = calendar.get(Calendar.SECOND);

		yearWheel = (WheelView)view.findViewById(R.id.yearwheel);
		monthWheel = (WheelView)view.findViewById(R.id.monthwheel);
		dayWheel = (WheelView)view.findViewById(R.id.daywheel);
		hourWheel = (WheelView)view.findViewById(R.id.hourwheel);
		minuteWheel = (WheelView)view.findViewById(R.id.minutewheel);
		secondWheel = (WheelView)view.findViewById(R.id.secondwheel);


		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setView(view);

		yearWheel.setAdapter(new StrericWheelAdapter(yearContent));
		yearWheel.setCurrentItem(curYear-2017);
		yearWheel.setCyclic(true);
		yearWheel.setInterpolator(new AnticipateOvershootInterpolator());


		monthWheel.setAdapter(new StrericWheelAdapter(monthContent));

		monthWheel.setCurrentItem(curMonth-1);

		monthWheel.setCyclic(true);
		monthWheel.setInterpolator(new AnticipateOvershootInterpolator());

		dayWheel.setAdapter(new StrericWheelAdapter(dayContent));
		dayWheel.setCurrentItem(curDay-1);
		dayWheel.setCyclic(true);
		dayWheel.setInterpolator(new AnticipateOvershootInterpolator());

		hourWheel.setAdapter(new StrericWheelAdapter(hourContent));
		hourWheel.setCurrentItem(curHour);
		hourWheel.setCyclic(true);
		hourWheel.setInterpolator(new AnticipateOvershootInterpolator());

		minuteWheel.setAdapter(new StrericWheelAdapter(minuteContent));
		minuteWheel.setCurrentItem(curMinute);
		minuteWheel.setCyclic(true);
		minuteWheel.setInterpolator(new AnticipateOvershootInterpolator());

		secondWheel.setAdapter(new StrericWheelAdapter(secondContent));
		secondWheel.setCurrentItem(curSecond);
		secondWheel.setCyclic(true);
		secondWheel.setInterpolator(new AnticipateOvershootInterpolator());

		builder.setTitle("请选择时间");
		builder.setPositiveButton("设置定时", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				StringBuffer sb = new StringBuffer();
				sb.append(formats(yearWheel.getCurrentItemValue())).append("-")
						.append(formats(monthWheel.getCurrentItemValue())).append("-")
						.append(formats(dayWheel.getCurrentItemValue()));

				sb.append(" ");
				sb.append(formats(hourWheel.getCurrentItemValue()))
						.append(":").append(formats(minuteWheel.getCurrentItemValue()))
						.append(":").append(formats(secondWheel.getCurrentItemValue()));
				getTimeString(sb.toString());
				dialog.cancel();
			}
		});

		builder.show();
	}
	private String formats(String num){
		return String.format("%02d",Integer.valueOf(num));
	}
}
