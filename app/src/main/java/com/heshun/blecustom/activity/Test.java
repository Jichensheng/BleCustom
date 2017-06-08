package com.heshun.blecustom.activity;

import com.heshun.blecustom.base.BaseResponseBody;
import com.heshun.blecustom.base.BleMessage;
import com.heshun.blecustom.base.Head;
import com.heshun.blecustom.entity.requestBodyEntity.GunIdRequest;
import com.heshun.blecustom.entity.responseBodyEntity.ChargeHistoryResponse;
import com.heshun.blecustom.tools.ToolsUtils;

import java.util.Arrays;

/**
 * author：Jics
 * 2017/6/5 09:33
 */
public class Test {
	public static void main(String[] args){
		//查询充电历史记录
		Head head=new Head(Head.CMD_TYPE_REQUEST,Head.CMD_QUERY_CHARGING_HISTORY,Head.STATUSCODE_NORMAL,(byte)0x00, (short) 0x00 );
		GunIdRequest chargeHistoryRequest=new GunIdRequest(0x0A);//枪号
		head.setCheckSum(ToolsUtils.checkSum(head,chargeHistoryRequest.getRequestBodyArray()));

		BleMessage bleMessage=new BleMessage();
		//编码部分
		byte[] message=bleMessage.encodeMessage(head,chargeHistoryRequest);
		System.out.println(Arrays.toString(message)+"\n");
		//解码部分
		System.out.println(Arrays.toString(makeData()));
		ChargeHistoryResponse chargeHistoryResponse= (ChargeHistoryResponse) bleMessage.decodeMessage(makeData());
		System.out.println(chargeHistoryResponse.toString()+"\n");

		System.out.println("**************");
		BaseResponseBody chargeHistory=  bleMessage.decodeMessage(makeData());
		System.out.println(chargeHistory.toString()+"\n");
	}

	/**
	 * 模拟充电桩发数据
	 * @return
	 */
	public static byte[] makeData(){
		byte[] bodyBytes=new byte[23];
		Head head=new Head(Head.CMD_TYPE_RESPONSE,Head.CMD_QUERY_CHARGING_HISTORY,Head.STATUSCODE_NORMAL,(byte)0x00,(short)0x00 );
		head.setBodyLength((short) bodyBytes.length);

		//body长度1009
		bodyBytes[0]= (byte) 0xf1;
		bodyBytes[1]= (byte) 0x03;

		//订单量999
		bodyBytes[2]= (byte) 0xe7;
		bodyBytes[3]= (byte) 0x03;

		//流水号10254852
		bodyBytes[4]= (byte) 0x04;
		bodyBytes[5]= (byte) 0x7a;
		bodyBytes[6]= (byte) 0x9c;
		bodyBytes[7]= (byte) 0x00;

		//电量500
		bodyBytes[8]= (byte) 0xf4;
		bodyBytes[9]= (byte) 0x01;

		//2017-06-05 10:43:09
		bodyBytes[10]=  61;
		bodyBytes[11]= -59;
		bodyBytes[12]= 52;
		bodyBytes[13]= 89;

		//2017-06-05 10:44:26
		bodyBytes[14]=  -118;
		bodyBytes[15]=  -59;
		bodyBytes[16]= 52;
		bodyBytes[17]=  89;

		//77秒
		bodyBytes[18]=  77;
		bodyBytes[19]=  0;
		bodyBytes[20]=0;
		bodyBytes[21]= 0;

		//枪号0x0a
		bodyBytes[22]= (byte) 0x0a;

		head.setCheckSum(ToolsUtils.checkSum(head,bodyBytes));
		System.out.println(head.toString()+"\n");
		return ToolsUtils.concatAll(head.getHeadArray(),bodyBytes);
	}

}
