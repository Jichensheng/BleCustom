package com.heshun.blecustom.base;

/**
 * author：Jics
 * 2017/6/3 10:01
 */
public class BaseResponseBody  {
	private String responseMsg="流程判断结果未设定";

	/**
	 * 需要传入响应体数组
	 * @param bodyArray
	 */
	public BaseResponseBody(byte[] bodyArray) {

	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}
}
