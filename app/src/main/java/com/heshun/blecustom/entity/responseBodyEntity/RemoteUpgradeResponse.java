package com.heshun.blecustom.entity.responseBodyEntity;

import com.heshun.blecustom.base.BaseResponseBody;

/**
 * 远程升级
 * 设备-> APP
 * author：Jics
 * 2017/6/3 11:29
 */
public class RemoteUpgradeResponse extends BaseResponseBody {

	public RemoteUpgradeResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}

	/**
	 * 远程升级响应体为空
	 * @param bodyArray
	 */
	public void decodeBody(byte[] bodyArray) {
	}
}
