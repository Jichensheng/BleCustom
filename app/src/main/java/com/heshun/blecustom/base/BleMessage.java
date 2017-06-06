package com.heshun.blecustom.base;

import com.heshun.blecustom.entity.responseBodyEntity.ChargeHistoryResponse;
import com.heshun.blecustom.entity.responseBodyEntity.ChargeNowResponse;
import com.heshun.blecustom.entity.responseBodyEntity.CumulativeChargeResponse;
import com.heshun.blecustom.entity.responseBodyEntity.DownloadPackageResponse;
import com.heshun.blecustom.entity.responseBodyEntity.DownloadSuccessfullyResponse;
import com.heshun.blecustom.entity.responseBodyEntity.QueryStateResponse;
import com.heshun.blecustom.entity.responseBodyEntity.RemoteUpgradeResponse;
import com.heshun.blecustom.entity.responseBodyEntity.SetChargeModeResponse;
import com.heshun.blecustom.entity.responseBodyEntity.SetVolumeResponse;
import com.heshun.blecustom.entity.responseBodyEntity.StopChargeResponse;
import com.heshun.blecustom.entity.responseBodyEntity.SysInfoResponse;
import com.heshun.blecustom.entity.responseBodyEntity.TimeSyncResponse;
import com.heshun.blecustom.tools.ByteUtils;
import com.heshun.blecustom.tools.ToolsUtils;

import java.util.Arrays;

/**
 * 编码和解码算一个周期，请求编码后发送出去会收到响应内容进行解码
 * 因此用户决定使用何种实体
 * author：Jics
 * 2017/6/3 09:47
 */
public class BleMessage {
	private byte CMDCode = 0x00;

	/**
	 * 编码请求体
	 * body长度和校验不用用户管
	 *
	 * @param head
	 * @param body 请求体
	 * @return
	 */
	public byte[] encodeMessage(Head head, BaseRequestBody body) {
		head.setBodyLength((short)( body.getRequestBodyArray().length));
		head.setCheckSum(ToolsUtils.checkSum(head, body.getRequestBodyArray()));//再此设置checksum
		return ToolsUtils.concatAll(head.getHeadArray(), body.getRequestBodyArray());
	}

	/**
	 * 解码响应体,对数据进行校验，校验成功开始解码
	 *
	 * @param message （head+body）
	 * @return 响应体
	 */
	public BaseResponseBody decodeMessage(byte[] message) {
		if (message.length < 8)
			return null;

		//解析head
		Head head = new Head();
		head.setStartCode(message[0]);
		head.setCMD(message[1]);
		head.setStatusCode(message[2]);
		head.setSequenceNumber(message[3]);
		byte[] temp = new byte[]{message[5], message[4]};
		head.setBodyLength(ByteUtils.byte2ToShort(temp));
		head.setFlags(message[6]);
		head.setCheckSum(message[7]);

		CMDCode = head.getCMD();
		//校验逻辑
		byte[] bodyArrays = Arrays.copyOfRange(message, 8, message.length);
		//checksum校验校验成功再解析
		if (head.getCheckSum() == ToolsUtils.checkSum(head, bodyArrays)) {
			//解析body
			switch (head.getCMD()) {
				case Head.CMD_TIME_SYNCHRONIZATION://时间同步
					return new TimeSyncResponse(bodyArrays);

				case Head.CMD_QUERY_SYSTEM_INFORMATION://查询系统信息
					return new SysInfoResponse(bodyArrays);

				case Head.CMD_SET_VOLUME://设置音量
					return new SetVolumeResponse(bodyArrays);

				case Head.CMD_SET_CHARGE_MODE://设置充电模式
					return new SetChargeModeResponse(bodyArrays);

				case Head.CMD_CHARGE_NOW://立即充电
					return new ChargeNowResponse(bodyArrays);

				case Head.CMD_QUERY_STATE://查询状态
					return new QueryStateResponse(bodyArrays);

				case Head.CMD_END_CHARGE://结束充电
					return new StopChargeResponse(bodyArrays);

				case Head.CMD_QUERY_CHARGING_HISTORY://查询充电历史记录
					return new ChargeHistoryResponse(bodyArrays);

				case Head.CMD_QUERY_CUMULATIVE_CHARGE://查询累计充电电量
					return new CumulativeChargeResponse(bodyArrays);

				case Head.CMD_START_REMOTE_UPGRADE://启动远程升级
					return new RemoteUpgradeResponse(bodyArrays);

				case Head.CMD_REQUEST_DOWNLOAD_PACKAGE://请求下载升级包（设备->APP）
					return new DownloadPackageResponse(bodyArrays);

				case Head.CMD_PACKAGE_DOWNLOAD_SUCCESSFULLY://升级包下载成功（设备->APP）
					return new DownloadSuccessfullyResponse(bodyArrays);

			}
		}
		return null;
	}


}
