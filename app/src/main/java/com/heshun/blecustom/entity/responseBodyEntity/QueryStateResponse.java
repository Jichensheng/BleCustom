package com.heshun.blecustom.entity.responseBodyEntity;

import com.heshun.blecustom.base.BaseResponseBody;
import com.heshun.blecustom.entity.GunStatus;
import com.heshun.blecustom.tools.ByteUtils;

import java.util.Arrays;

/**
 * 查询状态
 * 不同状态时的响应不同（三种状态的字段混在一起取值的时候先判断状态再取相应字段）
 * 使用时需查询状态
 * 高地位已反转
 * author：Jics
 * 2017/6/3 11:29
 */
public class QueryStateResponse extends BaseResponseBody {
	private int id;//端口号
	private byte status;//枪状态
	private int serialNumber;//流水号
	private short quantityOfElectricity;//电量（百分之一度）
	private int voltage;//电压 百分之一V
	private short electricCurrent;//电流 百分之一A
	private int duration;//时长
	private String faultDescription;//故障说明

	public QueryStateResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}


	public void decodeBody(byte[] bodyArray) {
		if (bodyArray.length>=2) {//至少有一个状态字段和一个端口号
			setStatus(bodyArray[0]);
			switch (getStatus()){
				case 0x06:
					if (bodyArray.length>=18) {
						setSerialNumber(ByteUtils.byte4ToInt(ByteUtils.byteReverse(Arrays.copyOfRange(bodyArray,1,5))));
						setQuantityOfElectricity(ByteUtils.byte2ToShort(new byte[]{bodyArray[6],bodyArray[5]}));
						setVoltage(ByteUtils.byte4ToInt(ByteUtils.byteReverse(Arrays.copyOfRange(bodyArray,7,11))));
						setElectricCurrent(ByteUtils.byte2ToShort(new byte[]{bodyArray[12],bodyArray[11]}));
						setDuration(ByteUtils.byte4ToInt(ByteUtils.byteReverse(Arrays.copyOfRange(bodyArray,13,17))));
						setId(bodyArray[17]);
					}else {
						setResponseMsg("充电信息不完整");
					}
					break;
				case 0x0F:
					if (bodyArray.length>=4) {
						short faultCode=ByteUtils.byte2ToShort(new byte[]{bodyArray[1],bodyArray[2]});//Todo 是否要反转
						switch (faultCode){
							case 0x0004:
								setFaultDescription("过流");
								break;
							case 0x0008:
								setFaultDescription("过压");
								break;
							case 0x0010:
								setFaultDescription("欠压");
								break;
							case 0x0100:
								setFaultDescription("急停按下");
								break;
							default:
								setFaultDescription("未知");
						}
						setId(bodyArray[3]);
					}else {
						setResponseMsg("故障信息不完整");
					}
					break;
				default:
					if (bodyArray.length==2) {
						GunStatus gunStatus=new GunStatus();
						gunStatus.setId(bodyArray[1]);
						gunStatus.setStatus(getStatus());
						setResponseMsg(gunStatus.getStatus());
					}else
						setResponseMsg("状态未知");

			}
		}else
			setResponseMsg("响应体为空");


	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}

	public int getQuantityOfElectricity() {
		return quantityOfElectricity;
	}

	public void setQuantityOfElectricity(short quantityOfElectricity) {
		this.quantityOfElectricity = quantityOfElectricity;
	}

	public int getVoltage() {
		return voltage;
	}

	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}

	public int getElectricCurrent() {
		return electricCurrent;
	}

	public void setElectricCurrent(short electricCurrent) {
		this.electricCurrent = electricCurrent;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getFaultDescription() {
		return faultDescription;
	}

	public void setFaultDescription(String faultDescription) {
		this.faultDescription = faultDescription;
	}

	@Override
	public String toString() {
		return "查询状态{\n" +
				"端口号=" + id +
				"\n状态=" + status +
				"\n流水号=" + serialNumber +
				"\n电量=" + quantityOfElectricity +
				"\n电压=" + voltage +
				"\n电流=" + electricCurrent +
				"\n时长=" + duration +
				"\n失败描述='" + faultDescription +"\n" +
				'}';
	}
}
