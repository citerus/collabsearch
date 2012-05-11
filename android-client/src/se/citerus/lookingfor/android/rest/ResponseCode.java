package se.citerus.lookingfor.android.rest;

public class ResponseCode {

//	/* OK */
	public final static int FOOTPRINT_SENT = 10001;

	private int code;
	private String msg;
	
	public ResponseCode() {

	}
	
	public int getCode() {
		return code;
	}
	
	public String getMessage() {
		return msg;
	}
}