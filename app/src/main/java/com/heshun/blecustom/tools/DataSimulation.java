package com.heshun.blecustom.tools;

/**
 * 数据模拟中心
 * author：Jics
 * 2017/6/8 15:37
 */
public class DataSimulation {

	/**
	 * 模拟历史充电记录响应数据
	 * body长度 1009
	 * 订单数量 999
	 * 流水号 10254852
	 * 充电电量 500
	 * 充电起始时间 2017-06-05 10:43:09
	 * 充电结束时间 2017-06-05 10:44:26
	 * 充电时长 77 秒
	 * 前端口号 0x0A
	 * @return
	 */
	public static byte[] getHistoryData(){
		return new byte[]{-86, -73, 0, 0, 23, 0, 0, -5, -15, 3, -25, 3, 4, 122, -100, 0, -12, 1, 61, -59, 52, 89, -118, -59, 52, 89, 77, 0, 0, 0, 10};
	}
}
