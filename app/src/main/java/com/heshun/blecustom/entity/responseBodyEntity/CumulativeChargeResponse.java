package com.heshun.blecustom.entity.responseBodyEntity;

import com.heshun.blecustom.base.BaseResponseBody;
import com.heshun.blecustom.tools.ByteUtils;

import java.util.Arrays;

/**
 * 累计电量
 * author：Jics
 * 2017/6/3 11:29
 */
public class CumulativeChargeResponse extends BaseResponseBody {

	private int quantityOfElectricity;//电量（百分之一度）
	private int id;//端口号

	public CumulativeChargeResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}

	public void decodeBody(byte[] bodyArray) {
		if (bodyArray.length>=5) {
			setQuantityOfElectricity(ByteUtils.byte4ToInt(ByteUtils.byteReverse(Arrays.copyOfRange(bodyArray,0,4))));
			setId(bodyArray[4]);
		}else
			setResponseMsg("响应体长度不足");
	}

	public int getQuantityOfElectricity() {
		return quantityOfElectricity;
	}

	public void setQuantityOfElectricity(int quantityOfElectricity) {
		this.quantityOfElectricity = quantityOfElectricity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "累计电量响应体{\n" +
				"累计电量=" + quantityOfElectricity +
				"\n枪编号=" + id +"\n" +
				'}';
	}
}
