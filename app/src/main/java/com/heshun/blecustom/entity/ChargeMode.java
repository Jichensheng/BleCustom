package com.heshun.blecustom.entity;

/**
 * 充电模式实体
 * author：Jics
 * 2017/6/6 15:34
 */
public class ChargeMode {
	private byte mode;
	private int startHour, endHour, startMinute, endMinute;

	public ChargeMode() {
	}

	public ChargeMode(byte mode, int startHour, int endHour, int startMinute, int endMinute) {
		this.mode = mode;
		this.startHour = startHour;
		this.endHour = endHour;
		this.startMinute = startMinute;
		this.endMinute = endMinute;
	}

	public byte getMode() {
		return mode;
	}

	public void setMode(byte mode) {
		this.mode = mode;
	}

	public int getStartHour() {
		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
	}

	public int getEndHour() {
		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
	}

	public int getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	public int getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
	}
}
