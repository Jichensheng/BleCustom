package com.heshun.blecustom.entity.responseBodyEntity;

import com.heshun.blecustom.base.BaseResponseBody;
import com.heshun.blecustom.tools.ToolsUtils;

import java.util.Arrays;

/**
 * 请求下载响应体
 * 设备->APP
 * author：Jics
 * 2017/6/3 11:29
 */
public class DownloadPackageResponse extends BaseResponseBody {
	private int statrIndex;
	private int endIndex;

	public DownloadPackageResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}

	public void decodeBody(byte[] bodyArray) {
		if(bodyArray.length==8){
			setStatrIndex(ToolsUtils.byte4ToInt(Arrays.copyOfRange(bodyArray,0,4)));
			setEndIndex(ToolsUtils.byte4ToInt(Arrays.copyOfRange(bodyArray,4,8)));
		}else
		{
			setResponseMsg("收到的不完整");
		}

	}

	public int getStatrIndex() {
		return statrIndex;
	}

	public void setStatrIndex(int statrIndex) {
		this.statrIndex = statrIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
}
