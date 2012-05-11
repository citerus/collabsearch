package se.citerus.lookingfor.android.rest;

public class Response<T> {
	
	private final boolean ok;
	private final int httpResult;
	private final T data;
	private final ResponseCode responseCode;
	
	
	public Response(boolean ok, T data, int httpResult, ResponseCode responseCode) {
		super();
		this.ok = ok;
		this.responseCode = responseCode;
		this.httpResult = httpResult;
		this.data = data;
	}
	
	public boolean isOk() {
		return ok;
	}
	
	public int getHttpResult() {
		return httpResult;
	}
	
	public T getData() {
		return data;
	}
	public int getResponseCode() {
		if(responseCode != null) {
			return responseCode.getCode();
		} else {
			return httpResult;
		}
		
	}
	
	public String getMessage() {
		if(responseCode != null) {
			return responseCode.getMessage();
		} else
			return "";
	}
	
	
	
}



