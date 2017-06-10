package com.heshun.blecustom.base;

/**
 * author：Jics
 * 2017/6/1 15:35
 */
public class Head {
	//指令码类型 对应CMD
	public static final byte CMD_TIME_SYNCHRONIZATION = (byte) 0xB0;//时间同步
	public static final byte CMD_QUERY_SYSTEM_INFORMATION = (byte) 0xB1;//查询系统信息
	public static final byte CMD_SET_VOLUME = (byte) 0xB2;//设置音量
	public static final byte CMD_SET_CHARGE_MODE = (byte) 0xB3;//设置充电模式
	public static final byte CMD_CHARGE_NOW = (byte) 0xB4;//立即充电
	public static final byte CMD_QUERY_STATE = (byte) 0xB5;//查询状态
	public static final byte CMD_END_CHARGE = (byte) 0xB6;//结束充电
	public static final byte CMD_QUERY_CHARGING_HISTORY = (byte) 0xB7;//查询充电历史记录
	public static final byte CMD_QUERY_CUMULATIVE_CHARGE = (byte) 0xB8;//查询累计充电电量
	public static final byte CMD_START_REMOTE_UPGRADE = (byte) 0xB9;//启动远程升级
	public static final byte CMD_REQUEST_DOWNLOAD_PACKAGE = (byte) 0xBA;//请求下载升级包（设备->APP）
	public static final byte CMD_PACKAGE_DOWNLOAD_SUCCESSFULLY = (byte) 0xBB;//升级包下载成功（设备->APP）
	public static final byte CMD_TYPE_REQUEST = 0x68;//请求
	public static final byte CMD_TYPE_RESPONSE = (byte) 0xAa;//响应

	//响应码状态类型 对应statusCode
	public static final byte STATUSCODE_NORMAL = (byte) 0x00;//正常
	public static final byte STATUSCODE_REQUEST_FORMAT_ERROR = (byte) 0x11;//请求格式错误，解析出错
	public static final byte STATUSCODE_UNAUTHORIZED = (byte) 0x12;//未授权，权限验证时使用
	public static final byte STATUSCODE_DENIAL_SERVICE = (byte) 0x13;//拒绝提供服务（CheckSum验证不通过或充电桩号识别不出）
	public static final byte STATUSCODE_CMD_NONEXISTENT = (byte) 0x14;//指令不存在
	public static final byte STATUSCODE_NONSUPPORT = (byte) 0x15;//当前设备不支持该指令
	public static final byte STATUSCODE_DEVICE_ERROR = (byte) 0x50;//设备内部出错
	public static final byte STATUSCODE_FAILED_EXECUTION = (byte) 0x51;//指令执行失败

	//不用的字段必须清零
	private byte startCode = 0x00;;//类型
	private byte CMD = 0x00;;//指令码
	private byte statusCode = 0x00;;//响应码状态
	private byte sequenceNumber = 0x00;//序列号
	private short bodyLength = 0x0000;;//body数据长度
	private byte flags = 0x00;//默认为0，主要用来扩展和修改
	private byte checkSum = 0x00;;//校验码

	public Head() {

	}

	/**
	 * 默认不使用flags
	 * @param startCode 类型
	 * @param CMD 指令码
	 * @param statusCode 响应码状态
	 * @param sequenceNumber 序列号
	 * @param bodyLength body数据长度
	 */
	public Head(byte startCode, byte CMD, byte statusCode, byte sequenceNumber, short bodyLength) {
		this.startCode = startCode;
		this.CMD = CMD;
		this.statusCode = statusCode;
		this.sequenceNumber = sequenceNumber;
		this.bodyLength = bodyLength;
	}
	public byte[] getHeadArray(){
		byte[] head=new byte[8];
		head[0]=startCode;
		head[1]=CMD;
		head[2]=statusCode;
		head[3]=sequenceNumber;
		head[4]=(byte)(bodyLength&0x00ff);//低位在前
		head[5]=(byte)(bodyLength>>8);
		head[6]=flags;
		head[7]=getCheckSum();
		return head;
	}
	/**
	 *	可扩展flags
	 * @param startCode 类型
	 * @param CMD 指令码
	 * @param statusCode 响应码状态
	 * @param sequenceNumber 序列号
	 * @param bodyLength body数据长度
	 * @param flags 用于扩展
	 */
	public Head(byte startCode, byte CMD, byte statusCode, byte sequenceNumber, short bodyLength, byte flags) {
		this(startCode,CMD,statusCode,sequenceNumber,bodyLength);
		this.flags = flags;
	}

	/**
	 * 快速初始化请求head
	 * @param CMD
	 */
	public Head(byte CMD) {
		this(Head.CMD_TYPE_REQUEST,CMD, Head.STATUSCODE_NORMAL, (byte) 0x00, (short) 0x00);
	}

	/**
	 * 快速初始化请求head
	 * @param CMD
	 */
	public Head(byte cmdType,byte CMD) {
		this(cmdType,CMD, Head.STATUSCODE_NORMAL, (byte) 0x00, (short) 0x00);
	}
	public byte getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(byte checkSum) {
		this.checkSum = checkSum;
	}

	public byte getStartCode() {
		return startCode;
	}

	public void setStartCode(byte startCode) {
		this.startCode = startCode;
	}

	public byte getCMD() {
		return CMD;
	}

	public void setCMD(byte CMD) {
		this.CMD = CMD;
	}

	public byte getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(byte statusCode) {
		this.statusCode = statusCode;
	}

	public byte getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(byte sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public short getBodyLength() {
		return bodyLength;
	}

	public void setBodyLength(short bodyLength) {
		this.bodyLength = bodyLength;
	}

	public byte getFlags() {
		return flags;
	}

	public void setFlags(byte flags) {
		this.flags = flags;
	}


	@Override
	public String toString() {
		return "Head{\n" +
				"startCode=" + startCode +
				",\n CMDItem=" + CMD +
				",\n statusCode=" + statusCode +
				",\n sequenceNumber=" + sequenceNumber +
				",\n bodyLength=" + bodyLength +
				",\n flags=" + flags +
				",\n checkSum=" + checkSum +
				'}';
	}
}
