package com.heshun.blecustom.entity.responseBodyEntity;

import com.heshun.blecustom.base.BaseResponseBody;
import com.heshun.blecustom.tools.ByteUtils;
import com.heshun.blecustom.tools.ToolsUtils;

import java.util.Arrays;

/**
 * 查询充电历史记录
 * 高低位已反转
 * author：Jics
 * 2017/6/3 11:29
 */
public class ChargeHistoryResponse extends BaseResponseBody  {
	private short bodyLength;
	private short orderQuantity;
	private int orderId;//流水号
	private short quantityOfElectricity;//电量（百分之一度）
	private String startTime;
	private String endTime;
	private int duration;//时长
	private int id;//端口号

	public ChargeHistoryResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}

	public void decodeBody(byte[] bodyArray) {
		if (bodyArray.length>=23) {
			setBodyLength(ByteUtils.byte2ToShort(new byte[]{bodyArray[1],bodyArray[0]}));
			setOrderQuantity(ByteUtils.byte2ToShort(new byte[]{bodyArray[3],bodyArray[2]}));
			setOrderId(ByteUtils.byte4ToInt(ByteUtils.byteReverse(Arrays.copyOfRange(bodyArray,4,8))));
			setQuantityOfElectricity(ByteUtils.byte2ToShort(new byte[]{bodyArray[9],bodyArray[8]}));
			setStartTime(ToolsUtils.byteToSecondTime(Arrays.copyOfRange(bodyArray,10,14)));
			setEndTime(ToolsUtils.byteToSecondTime(Arrays.copyOfRange(bodyArray,14,18)));
			setDuration(ByteUtils.byte4ToInt(ByteUtils.byteReverse(Arrays.copyOfRange(bodyArray,18,22))));
			setId(bodyArray[22]);
		}else
			setResponseMsg("响应体长度不足");
	}

	public short getBodyLength() {
		return bodyLength;
	}

	public void setBodyLength(short bodyLength) {
		this.bodyLength = bodyLength;
	}

	public short getOrderQuantity() {
		return orderQuantity;
	}

	public void setOrderQuantity(short orderQuantity) {
		this.orderQuantity = orderQuantity;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public short getQuantityOfElectricity() {
		return quantityOfElectricity;
	}

	public void setQuantityOfElectricity(short quantityOfElectricity) {
		this.quantityOfElectricity = quantityOfElectricity;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "ChargeHistoryResponse{\n" +
				"bodyLength=" + bodyLength +
				",\n orderQuantity=" + orderQuantity +
				",\n orderId=" + orderId +
				",\n quantityOfElectricity=" + quantityOfElectricity +
				",\n startTime='" + startTime + '\'' +
				",\n endTime='" + endTime + '\'' +
				",\n duration=" + duration +
				",\n id=" + id +
				'}';
	}
}
