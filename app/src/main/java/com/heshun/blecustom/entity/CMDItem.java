package com.heshun.blecustom.entity;

/**
 * author：Jics
 * 2017/6/5 16:55
 */
public class CMDItem {
	private boolean itmeType;
	private String cmdName;
	private byte cmd;

	public CMDItem(boolean itmeType, String cmdName, byte cmd) {
		this.itmeType = itmeType;
		this.cmdName = cmdName;
		this.cmd = cmd;
	}

	public boolean getItmeType() {
		return itmeType;
	}

	public void setItmeType(boolean itmeType) {
		this.itmeType = itmeType;
	}

	public String getCmdName() {
		return cmdName;
	}

	public void setCmdName(String cmdName) {
		this.cmdName = cmdName;
	}

	public byte getCmd() {
		return cmd;
	}

	public void setCmd(byte cmd) {
		this.cmd = cmd;
	}
}
