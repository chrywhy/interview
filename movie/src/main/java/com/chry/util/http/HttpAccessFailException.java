package com.chry.util.http;

public class HttpAccessFailException extends RuntimeException {
	public HttpAccessFailException(Exception e) {
		super(e);
	}
}
