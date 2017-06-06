package com.heshun.blecustom.entity.responseBodyEntity;


import com.heshun.blecustom.base.BaseResponseBody;
import com.heshun.blecustom.entity.GunStatus;
import com.heshun.blecustom.tools.ByteUtils;
import com.heshun.blecustom.tools.ToolsUtils;

import java.util.Arrays;

/**
 * 查询系统信息
 * 高低位已反转
 * author：Jics
 * 2017/6/3 11:29
 */
public class SysInfoResponse extends BaseResponseBody {

	//解析前
	private String pileID;//桩号 20 字节
	private String pileType;//桩类型 1 字节
	private int pileSupplier;//厂商 1 字节
	private String magicNumber;//MagicNumber 4 字节
	private String chargeMode;//充电模式 1 字节
	private int volume;//音量大小 1 字节
	private String pileTime;//充电桩时间 4 字节
	private int length;// 枪号+状态的长度 1 字节
	private GunStatus[] gunState;//枪状态 不定长(每两个字节为一个模块，低字节端口号，高字节枪状态)

	//解析后
	private String protocolVersion;//通讯协议版本号
	private String softVsersion;//软件版本号
	private String softModel;//嵌入式软件型号
	private String TBD;//未知
	private String startTime;
	private String endTime;

	public SysInfoResponse(byte[] bodyArray) {
		super(bodyArray);
		decodeBody(bodyArray);
	}


	public void decodeBody(byte[] bodyArray) {
		//bodyArray长度至少57
		if (bodyArray.length>=57) {
			setPileID(new String(ByteUtils.byteReverse(Arrays.copyOfRange(bodyArray, 0, 20))));
			switch (bodyArray[20]) {
				case 0x01:
					setPileType("单枪");
					break;
				case 0x02:
					setPileType("双枪");
					break;
				case 0x03:
					setPileType("多枪");
					break;
				case 0x04:
					setPileType("单枪广告桩");
					break;
				case 0x05:
					setPileType("双枪广告桩");
					break;
				default:
					setPileType("未知桩类型");
					setResponseMsg("未知桩类型");
			}
			setPileSupplier(bodyArray[21]);//供应商编号对应表未知
			setMagicNumber(new String(new byte[]{bodyArray[25], bodyArray[24], bodyArray[23], bodyArray[22]}));
			setProtocolVersion("V" + bodyArray[27] + "." + bodyArray[26]);
			setSoftVsersion("V" + bodyArray[30] + "." + bodyArray[29] + "." + bodyArray[28]);
			setSoftModel(new String(ByteUtils.byteReverse(Arrays.copyOfRange(bodyArray, 31, 41))));
			setTBD(new String(ByteUtils.byteReverse(Arrays.copyOfRange(bodyArray, 41, 46))));
			switch (bodyArray[46]) {
				case 0x00:
					setChargeMode("立即模式");
					break;
				case 0x01:
					setChargeMode("定时模式");
					break;
				case 0x02:
					setChargeMode("经济模式");
					break;
				default:
					setChargeMode("未知充电模式");
					setResponseMsg("未知充电模式");
			}
			setStartTime(bodyArray[47] + " : " + bodyArray[48]);
			setEndTime(bodyArray[49] + " : " + bodyArray[50]);
			setVolume(bodyArray[51]);
			setPileTime(ToolsUtils.byteToSecondTime(Arrays.copyOfRange(bodyArray, 52, 56)));
			setLength(bodyArray[56]);
			byte[] statusArray = Arrays.copyOfRange(bodyArray, 57, bodyArray.length);

			if (statusArray.length >= 2 && statusArray.length % 2 == 0) {
				GunStatus[] temp=new GunStatus[statusArray.length/2];
				for(int i=57;i<bodyArray.length;i+=2){
					GunStatus gunStatus=new GunStatus();
					gunStatus.setId(statusArray[i-57]);
					gunStatus.setStatus(statusArray[i-56]);
					temp[(i-57)/2]=gunStatus;
				}
				setGunState(temp);
			}else{
				setResponseMsg("没有枪状态");
			}


		}else{
			setResponseMsg("响应体不完整");
		}
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public String getSoftVsersion() {
		return softVsersion;
	}

	public void setSoftVsersion(String softVsersion) {
		this.softVsersion = softVsersion;
	}

	public String getSoftModel() {
		return softModel;
	}

	public void setSoftModel(String softModel) {
		this.softModel = softModel;
	}

	public String getTBD() {
		return TBD;
	}

	public void setTBD(String TBD) {
		this.TBD = TBD;
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

	public String getPileID() {
		return pileID;
	}

	public void setPileID(String pileID) {
		this.pileID = pileID;
	}

	public String getPileType() {
		return pileType;
	}

	public void setPileType(String pileType) {
		this.pileType = pileType;
	}

	public int getPileSupplier() {
		return pileSupplier;
	}

	public void setPileSupplier(int pileSupplier) {
		this.pileSupplier = pileSupplier;
	}

	public String getMagicNumber() {
		return magicNumber;
	}

	public void setMagicNumber(String magicNumber) {
		this.magicNumber = magicNumber;
	}

	public String getChargeMode() {
		return chargeMode;
	}

	public void setChargeMode(String chargeMode) {
		this.chargeMode = chargeMode;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public String getPileTime() {
		return pileTime;
	}

	public void setPileTime(String pileTime) {
		this.pileTime = pileTime;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public GunStatus[] getGunState() {
		return gunState;
	}

	public void setGunState(GunStatus[] gunState) {
		this.gunState = gunState;
	}

	@Override
	public String toString() {
		return "SysInfoResponse{" +
				"pileID='" + pileID + '\'' +
				", pileType='" + pileType + '\'' +
				", pileSupplier=" + pileSupplier +
				", magicNumber='" + magicNumber + '\'' +
				", chargeMode='" + chargeMode + '\'' +
				", volume=" + volume +
				", pileTime='" + pileTime + '\'' +
				", length=" + length +
				", protocolVersion='" + protocolVersion + '\'' +
				", softVsersion='" + softVsersion + '\'' +
				", softModel='" + softModel + '\'' +
				", TBD='" + TBD + '\'' +
				", startTime='" + startTime + '\'' +
				", endTime='" + endTime + '\'' +
				'}';
	}

}
