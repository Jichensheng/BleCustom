package com.heshun.blecustom.entity.responseBodyEntity;

import com.heshun.blecustom.base.BaseResponseBody;

/**
 * 请求下载响应体
 * 设备->APP
 * author：Jics
 * 2017/6/3 11:29
 */
public class DownloadPackageResponse extends BaseResponseBody{

	public DownloadPackageResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}

	public void decodeBody(byte[] bodyArray) {
	}
}
