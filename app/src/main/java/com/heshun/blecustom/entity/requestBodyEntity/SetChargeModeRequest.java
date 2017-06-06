package com.heshun.blecustom.entity.requestBodyEntity;

import com.heshun.blecustom.base.BaseRequestBody;

import java.util.Calendar;

/**
 * 设置充电模式请求体
 * author：Jics
 * 2017/6/3 19:14
 */
public class SetChargeModeRequest extends BaseRequestBody {
	public static final byte MODE_IMMEDIATELY=0x00;//立即模式
	public static final byte MODE_BARGAIN=0x01;//经济模式
	public static final byte MODE_TIMING=0x02;//定时模式

	private int chargeMode;
	private int startH;
	private int startM;
	private int endH;
	private int endM;

	/**
	 * 立即模式（开始时间为当前时间，结束时间全零）
	 * @param chargeMode
	 */
	public SetChargeModeRequest(int chargeMode) {
		this.chargeMode = chargeMode;
		Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
		setStartH(c.get(Calendar.HOUR_OF_DAY));
		setStartM(c.get(Calendar.MINUTE));
		setEndH(0);
		setEndM(0);
	}

	/**
	 * 定时模式（开始时间用户设定，结束时间全零，充至自动结束）
	 * @param chargeMode
	 * @param startH
	 * @param startM
	 */
	public SetChargeModeRequest(int chargeMode, int startH, int startM) {
		this.chargeMode = chargeMode;
		this.startH = startH;
		this.startM = startM;
		setEndH(0);
		setEndM(0);
	}

	/**
	 * 经济模式（开始和结束时间全由用户决定）
	 * @param chargeMode
	 * @param startH
	 * @param startM
	 * @param endH
	 * @param endM
	 */
	public SetChargeModeRequest(int chargeMode, int startH, int startM, int endH, int endM) {
		this.chargeMode = chargeMode;
		this.startH = startH;
		this.startM = startM;
		this.endH = endH;
		this.endM = endM;
	}

	public SetChargeModeRequest() {
	}


	@Override
	public byte[] getRequestBodyArray() {
		return new byte[]{(byte) getChargeMode(), (byte) getStartH(), (byte) getStartM(), (byte) getEndH(), (byte) getEndM()};
	}

	public int getChargeMode() {
		return chargeMode;
	}

	public void setChargeMode(int chargeMode) {
		this.chargeMode = chargeMode;
	}

	public int getStartH() {
		return startH;
	}

	public void setStartH(int startH) {
		this.startH = startH;
	}

	public int getStartM() {
		return startM;
	}

	public void setStartM(int startM) {
		this.startM = startM;
	}

	public int getEndH() {
		return endH;
	}

	public void setEndH(int endH) {
		this.endH = endH;
	}

	public int getEndM() {
		return endM;
	}

	public void setEndM(int endM) {
		this.endM = endM;
	}
}
