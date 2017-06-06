package com.heshun.blecustom.entity;

/**
 * author：Jics
 * 2017/6/3 13:41
 */
public class GunStatus {
	public static final byte STATUS_FREE= 0x00;//空闲中
	public static final byte STATUS_UNCHARGED= 0x01;//插枪未充电
	public static final byte STATUS_CHARGING= 0x06;//充电中
	public static final byte STATUS_END_WITH_GUN= 0x08;//充电结束未拔枪
	public static final byte STATUS_IN_TROUBLE= 0x0F;//故障中

	private int id;
	private String status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		switch (status){
			case STATUS_FREE:
				this.status = "空闲中";
				break;
			case STATUS_UNCHARGED:
				this.status = "插枪未充电";
				break;
			case STATUS_CHARGING:
				this.status = "充电中";
				break;
			case STATUS_END_WITH_GUN:
				this.status = "充电结束未拔枪";
				break;
			case STATUS_IN_TROUBLE:
				this.status = "故障中";
				break;
			default:
				this.status = "枪状态未知状态";
		}

	}

	@Override
	public String toString() {
		return "GunStatus{" +
				"id=" + id +
				", status='" + status + '\'' +
				'}';
	}
}
