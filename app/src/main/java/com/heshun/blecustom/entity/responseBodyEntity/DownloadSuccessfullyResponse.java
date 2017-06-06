package com.heshun.blecustom.entity.responseBodyEntity;

import com.heshun.blecustom.base.BaseResponseBody;

/**
 * 升级包下载成功
 * 设备-> APP
 * author：Jics
 * 2017/6/3 11:29
 */
public class DownloadSuccessfullyResponse extends BaseResponseBody{

	public DownloadSuccessfullyResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}

	public void decodeBody(byte[] bodyArray) {
	}
}
