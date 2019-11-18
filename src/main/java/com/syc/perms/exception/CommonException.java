package com.syc.perms.exception;

import lombok.Data;

@Data
public class CommonException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
    private String msg;
    private int code = 500;
    
    public CommonException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public CommonException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}
	
	public CommonException(String msg, int code) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}
	
	public CommonException(String msg, int code, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}

}
