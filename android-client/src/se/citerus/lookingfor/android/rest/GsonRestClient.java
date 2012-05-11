package se.citerus.lookingfor.android.rest;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.google.gson.Gson;

public class GsonRestClient {

	protected final Gson gson = new Gson();
	protected final String baseUrl;
	
	public GsonRestClient(String baseUrl) {
		this.baseUrl = baseUrl;
		
	}

	protected <D> Response<D> executeGet(String url, Type listType, Param... params) {
		try {
			String addParams = "?";
			for (Param param : params) {
				if(!(param instanceof Header)) {
					String encodedValue = URLEncoder.encode(param.value, "UTF-8");
					addParams += param.key + "=" + encodedValue + "&";
				}
			}
			if(addParams.endsWith("&") || addParams.endsWith("?")) {
				addParams = addParams.substring(0, addParams.length() - 1);
			}
			
			
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(baseUrl + url + addParams);
			
			for (Param param : params) {
				if(param instanceof Header) {
					httpGet.addHeader(param.key, param.value);
				}
			}
					
			HttpResponse execute = client.execute(httpGet);
			int errorCode = execute.getStatusLine().getStatusCode();
			
			D d = null;
			ResponseCode responseCode = null;
			boolean responseOk = false;

			if(errorCode != 200) {
				try {
					responseCode = (ResponseCode) gson.fromJson(new InputStreamReader(execute.getEntity().getContent()), ResponseCode.class);
				} catch (Exception e) {
					//TODO: do a nicer check if we have a response to parse or not based on errorCode.
					responseCode = null;
				}
			} else {
				d = (D) gson.fromJson(new InputStreamReader(execute.getEntity().getContent()), listType);
				responseOk = true;
			}
			Response<D> response = new Response<D>(responseOk, d, errorCode, responseCode);	
			return response;
			
		} catch (ClientProtocolException e) {
			return new Response<D>(false, null, -1, null);	
		} catch (IOException e) {
			return new Response<D>(false, null, -1, null);
		} 
		
	}
	
	protected <D> Response<D> executePost(String url, Type listType, Param... params) {
		try {
			
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(baseUrl + url);
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (Param param : params) {
				if(param instanceof Header) {
					httpPost.addHeader(param.key, param.value);
				} else {
					nameValuePairs.add(new BasicNameValuePair(param.key, param.value));
				}
				
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			
			HttpResponse execute = client.execute(httpPost);
			int errorCode = execute.getStatusLine().getStatusCode();
			
			D d = null;
			ResponseCode responseCode = null;
			boolean responseOk = false;

			if(errorCode != 200) {
				try {
					Log.d("lookingfor", "Statuscode: " + execute.getStatusLine().getStatusCode());
					responseCode = (ResponseCode) gson.fromJson(new InputStreamReader(execute.getEntity().getContent()), ResponseCode.class);
				} catch (Exception e) {
					//TODO: do a nicer check if we have a response to parse or not based on errorCode.
					responseCode = null;
				}
			} else {
				d = (D) gson.fromJson(new InputStreamReader(execute.getEntity().getContent()), listType);
				responseOk = true;
			}
			Response<D> response = new Response<D>(responseOk, d, errorCode, responseCode);	
			return response;
			
		} catch (ClientProtocolException e) {
			return new Response<D>(false, null, -1, null);	
		} catch (IOException e) {
			return new Response<D>(false, null, -1, null);
		} 
		
	}
	
	protected <D> Response<D> executePut(String url, Type listType, Param... params) {
		try {
			
			HttpClient client = new DefaultHttpClient();
			HttpPut httpPut = new HttpPut(baseUrl + url);
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (Param param : params) {
				if(param instanceof Header) {
					httpPut.addHeader(param.key, param.value);
				} else {
					nameValuePairs.add(new BasicNameValuePair(param.key, param.value));
				}
				
			}
			httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			
			HttpResponse execute = client.execute(httpPut);
			int errorCode = execute.getStatusLine().getStatusCode();
			
			D d = null;
			ResponseCode responseCode = null;
			boolean responseOk = false;

			if(errorCode != 200) {
				try {
				responseCode = (ResponseCode) gson.fromJson(new InputStreamReader(execute.getEntity().getContent()), ResponseCode.class);
				} catch (Exception e) {
					//TODO: do a nicer check if we have a response to parse or not based on errorCode.
					responseCode = null;
				}
			} else {
				d = (D) gson.fromJson(new InputStreamReader(execute.getEntity().getContent()), listType);
				responseOk = true;
			}
			Response<D> response = new Response<D>(responseOk, d, errorCode, responseCode);	
			return response;
			
		} catch (ClientProtocolException e) {
			return new Response<D>(false, null, -1, null);	
		} catch (IOException e) {
			return new Response<D>(false, null, -1, null);
		} 
		
	}

	protected Param param(String key, String value) {
		return new Param(key, value);
	}
	
	protected Header header(String key, String value) {
		return new Header(key, value);
	}
	
	protected class Param {
		String key;
		String value;
		public Param(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
	}
	protected class Header extends Param{

		public Header(String key, String value) {
			super(key, value);
		}
		
	}

}
