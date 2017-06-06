package com.heshun.blecustom.entity.requestBodyEntity;

import com.heshun.blecustom.base.BaseRequestBody;
import com.heshun.blecustom.tools.ToolsUtils;

/**
 * 同步时间请求体
 * author：Jics
 * 2017/6/3 19:12
 */
public class TimeSyncRequest extends BaseRequestBody {
	@Override
	public byte[] getRequestBodyArray() {
		return  ToolsUtils.getSecondTimestamp();
	}
}
