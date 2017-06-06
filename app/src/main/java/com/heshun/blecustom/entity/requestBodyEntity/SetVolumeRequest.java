package com.heshun.blecustom.entity.requestBodyEntity;

import com.heshun.blecustom.base.BaseRequestBody;

/**
 * 音量设置请求体
 * author：Jics
 * 2017/6/3 19:14
 */
public class SetVolumeRequest extends BaseRequestBody {
	private int volume=20;//默认

	public SetVolumeRequest(int volume) {
		volume = volume <= 0 ? 0 : volume;
		volume = volume >= 100 ? 100 : volume;
		this.volume = volume;
	}

	@Override
	public byte[] getRequestBodyArray() {
		return new byte[]{(byte) getVolume()};
	}

	public int getVolume() {
		return volume;
	}

	public SetVolumeRequest() {
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}
}
