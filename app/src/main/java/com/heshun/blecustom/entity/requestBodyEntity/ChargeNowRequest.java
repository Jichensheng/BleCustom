package com.heshun.blecustom.entity.requestBodyEntity;

import com.heshun.blecustom.base.BaseRequestBody;

/**
 * 立即充电请求体
 * author：Jics
 * 2017/6/3 19:15
 */
public class ChargeNowRequest extends BaseRequestBody {
	private int authorizeType;
	private int id;

	public ChargeNowRequest(int authorizeType, int id) {
		this.authorizeType = authorizeType;
		id = id <= 0 ? 0 : id;
		id = id >= 0xFF ? 0xFF : id;
		this.id = id;
	}

	@Override
	public byte[] getRequestBodyArray() {
		return new byte[]{(byte) authorizeType, (byte) id};
	}

	public ChargeNowRequest() {
	}

	public int getAuthorizeType() {
		return authorizeType;
	}

	public void setAuthorizeType(int authorizeType) {
		this.authorizeType = authorizeType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
