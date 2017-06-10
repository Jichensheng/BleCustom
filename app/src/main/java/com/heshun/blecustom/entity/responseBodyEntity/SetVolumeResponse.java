package com.heshun.blecustom.entity.responseBodyEntity;

import com.heshun.blecustom.base.BaseResponseBody;

/**
 * 设置音量
 * author：Jics
 * 2017/6/3 11:29
 */
public class SetVolumeResponse extends BaseResponseBody {
	private String setResult;

	public SetVolumeResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}

	public void decodeBody(byte[] bodyArray) {
		if (bodyArray.length == 1) {
			switch (bodyArray[0]) {
				case 0x00:
					setSetResult("成功");
					break;
				default:
					setSetResult("失败");
					setResponseMsg("失败");

			}
		}else{
			setResponseMsg("响应体为空");
		}

	}

	public String getSetResult() {
		return setResult;
	}

	public void setSetResult(String setResult) {
		this.setResult = setResult;
	}

	@Override
	public String toString() {
		return "音量设置响应体{\n" +
				"设置结果='" + setResult +"\n" +
				'}';
	}
}
