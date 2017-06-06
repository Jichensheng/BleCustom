package com.heshun.blecustom.entity.requestBodyEntity;

import com.heshun.blecustom.base.BaseRequestBody;

/**
 * 查询系统信息，无需body
 * author：Jics
 * 2017/6/3 19:13
 */
public class SysInfoRequest extends BaseRequestBody {
	@Override
	public byte[] getRequestBodyArray() {
		return new byte[0];
	}
}
