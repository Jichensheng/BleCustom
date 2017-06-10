package com.heshun.blecustom.entity.responseBodyEntity;

import com.heshun.blecustom.base.BaseResponseBody;

/**
 * 升级包下载成功
 * 设备-> APP
 * author：Jics
 * 2017/6/3 11:29
 */
public class DownloadSuccessfullyResponse extends BaseResponseBody{
	private boolean isSucc;
	public DownloadSuccessfullyResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}

	public void decodeBody(byte[] bodyArray) {
		if (bodyArray.length==1) {
			if (bodyArray[0]==0)
				setSucc(true);
			else setSucc(false);
		}
	}

	public boolean isSucc() {
		return isSucc;
	}

	public void setSucc(boolean succ) {
		isSucc = succ;
	}

	@Override
	public String toString() {
		return "下载成功响应体 { \n" +
				"是否成功=" + isSucc +"\n" +
				'}';
	}
}
