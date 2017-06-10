package com.heshun.blecustom.entity.responseBodyEntity;


import com.heshun.blecustom.base.BaseResponseBody;
import com.heshun.blecustom.tools.ByteUtils;
import com.heshun.blecustom.tools.ToolsUtils;

/**
 * 时间同步响应体
 * author：Jics
 * 2017/6/3 11:24
 */
public class TimeSyncResponse extends BaseResponseBody {
	private int time;

	public TimeSyncResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public void decodeBody(byte[] bodyArray) {
		int time = 0;
		if (bodyArray.length == 4) {
			time = ByteUtils.byte4ToInt(new byte[]{bodyArray[3], bodyArray[2], bodyArray[1], bodyArray[0]});
		}
		this.setTime(time);
	}

	@Override
	public String toString() {
		return "时间同步响应体 {\n" +
				"时间=" + ToolsUtils.byteToSecondTime(ToolsUtils.intToByte4(time)) +"\n" +
				'}';
	}
}
