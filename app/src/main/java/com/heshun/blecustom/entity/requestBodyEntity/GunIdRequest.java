package com.heshun.blecustom.entity.requestBodyEntity;

import com.heshun.blecustom.base.BaseRequestBody;

/**
 * 查询状态 充电结束 历史记录 累计电量
 * author：Jics
 * 2017/6/6 14:34
 */
public class GunIdRequest extends BaseRequestBody {
	private int id;

	/**
	 * 枪端口号不能大于255
	 * @param id 枪端口号
	 */
	public GunIdRequest( int id) {
		id = id <= 0 ? 0 : id;
		id = id >= 0xFF ? 0xFF : id;
		this.id = id;
	}

	@Override
	public byte[] getRequestBodyArray() {
		return new byte[]{(byte) id};
	}

	public GunIdRequest() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
