package com.heshun.blecustom.entity.requestBodyEntity;

import com.heshun.blecustom.base.BaseRequestBody;
import com.heshun.blecustom.tools.ToolsUtils;

/**
 * author：Jics
 * 2017/6/3 19:18
 */
public class RemoteUpgradeRequest extends BaseRequestBody {

	private byte signal;//客户端表示，升级程序对应的桩类型，不一致不能升级
	private String softVsersion = "0.0.0";//软件版本号 1.00.01
	private int packageLength;//升级包大小
	private int checksum;//校验码CRC32
	private int subsectionNum;//分段字节数

	@Override
	public byte[] getRequestBodyArray() {
		String[] versions = softVsersion.split("\\.");
		byte versionMain = versions.length == 3 ? Byte.parseByte(versions[0]) : 0;
		byte versionSecond = versions.length == 3 ? Byte.parseByte(versions[1]) : 0;
		byte versionRevise = versions.length == 3 ? Byte.parseByte(versions[2]) : 0;

		byte[] first = new byte[]{signal, versionRevise, versionSecond, versionMain};
		byte[] packageLengths = ToolsUtils.intToByte4(packageLength);
		byte[] checksums = ToolsUtils.intToByte4(checksum);
		byte[] subsectionNums = ToolsUtils.intToByte4(subsectionNum);

		return ToolsUtils.concatAll(first, packageLengths, checksums, subsectionNums);
	}

	/**
	 * @param signal        客户端标识
	 * @param softVsersion  软件版本号
	 * @param packageLength 升级包长度
	 * @param checksum      校验码
	 * @param subsectionNum 分段下载字节数
	 */
	public RemoteUpgradeRequest(byte signal, String softVsersion, int packageLength, int checksum, int subsectionNum) {
		this.signal = signal;
		this.softVsersion = softVsersion;
		this.packageLength = packageLength;
		this.checksum = checksum;
		this.subsectionNum = subsectionNum;
	}

	public RemoteUpgradeRequest() {
	}

	public byte getSignal() {
		return signal;
	}

	public void setSignal(byte signal) {
		this.signal = signal;
	}

	public String getSoftVsersion() {
		return softVsersion;
	}

	public void setSoftVsersion(String softVsersion) {
		this.softVsersion = softVsersion;
	}

	public int getPackageLength() {
		return packageLength;
	}

	public void setPackageLength(int packageLength) {
		this.packageLength = packageLength;
	}

	public int getChecksum() {
		return checksum;
	}

	public void setChecksum(int checksum) {
		this.checksum = checksum;
	}

	public int getSubsectionNum() {
		return subsectionNum;
	}

	public void setSubsectionNum(int subsectionNum) {
		this.subsectionNum = subsectionNum;
	}
}
