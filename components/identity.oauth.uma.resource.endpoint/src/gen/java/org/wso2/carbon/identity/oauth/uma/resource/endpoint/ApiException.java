package org.wso2.carbon.identity.oauth.uma.resource.endpoint;
//comment
public class ApiException extends Exception{
	private int code;
	private String msg;

	public ApiException(int code, String msg) {

		this.msg = msg;
		this.code = code;
	}

	public int getCode() {

		return code;
	}

	public void setCode(int code) {

		this.code = code;
	}

	public String getMsg() {

		return msg;
	}

	public void setMsg(String msg) {

		this.msg = msg;
	}
}

