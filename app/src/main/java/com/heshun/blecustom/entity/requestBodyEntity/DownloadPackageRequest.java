package com.heshun.blecustom.entity.requestBodyEntity;


import com.heshun.blecustom.base.BaseRequestBody;
import com.heshun.blecustom.tools.ToolsUtils;

/**
 * 生成文件块请求体
 * 外围设备发来起止下标，得到文件块byte[]，来此加工
 * author：Jics
 * 2017/6/3 19:18
 */
public class DownloadPackageRequest extends BaseRequestBody {
	private byte[] fileBlock;
	public DownloadPackageRequest() {

	}

	public DownloadPackageRequest(byte[] fileBlock) {
		this.fileBlock = fileBlock;
	}

	@Override
	public byte[] getRequestBodyArray() {
		byte[] checksum= ToolsUtils.fileCRC32(fileBlock);
		byte[] fileBlockSize=ToolsUtils.intToByte4(fileBlock.length);

		return ToolsUtils.concatAll(checksum,fileBlockSize,fileBlock);
	}

	public byte[] getFileBlock() {
		return fileBlock;
	}

	public void setFileBlock(byte[] fileBlock) {
		this.fileBlock = fileBlock;
	}
}
