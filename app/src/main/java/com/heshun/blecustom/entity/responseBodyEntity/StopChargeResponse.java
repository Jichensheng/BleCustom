package com.heshun.blecustom.entity.responseBodyEntity;

import com.heshun.blecustom.base.BaseResponseBody;
import com.heshun.blecustom.tools.ByteUtils;
import com.heshun.blecustom.tools.ToolsUtils;

import java.util.Arrays;

/**
 * 结束充电
 * author：Jics
 * 2017/6/3 11:29
 */
public class StopChargeResponse extends BaseResponseBody {
	private String time;

	public StopChargeResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}

	public void decodeBody(byte[] bodyArray) {
		if (bodyArray.length >= 4) {
			setTime(ToolsUtils.byteToSecondTime(ByteUtils.byteReverse(Arrays.copyOfRange(bodyArray, 0, 4))));
		} else {
			setResponseMsg("响应体长度不足");
		}
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return "StopChargeResponse{" +
				"time='" + time + '\'' +
				'}';
	}
}
