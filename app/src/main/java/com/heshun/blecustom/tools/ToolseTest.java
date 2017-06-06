package com.heshun.blecustom.tools;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

/**
 * author：Jics
 * 2017/6/5 10:35
 */
public class ToolseTest {
	 static int howLong=17;
	static Timer timer=new Timer();
	public static void main(String[] args){
		//2017-06-05 10:44:26
		String time=ToolsUtils.byteToSecondTime(new byte[]{-118, -59, 52, 89});
		System.out.println(time);
		System.out.println(Arrays.toString(ToolsUtils.intToByte4(77)));

		byte[] test=new byte[]{1,2,3,4,5,6,7};
		byte[] ads=Arrays.copyOfRange(test,7,test.length);
		System.out.println(Arrays.toString(ads));

		byte[] response=new byte[0];
		for(int i=0;i<9;i++){
			response=ToolsUtils.concatAll(response,test);
		}
		System.out.println(Arrays.toString(response));

		TimerTask task=new TimerTask() {
			@Override
			public void run() {
				System.out.println(howLong);
				howLong--;
				if (howLong<0){
					timer.cancel();
				}
			}
		};
		timer.schedule(task,1000,1000);

	}
}
