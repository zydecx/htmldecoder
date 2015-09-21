package com.debugtoday.htmldecoder.exception;

public class GeneralException extends Exception {
	public GeneralException() {
	}
	
	public GeneralException(String message) {
		super(message);
	}
	
	public GeneralException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public GeneralException(Throwable throwable) {
		super(throwable);
	}
}
