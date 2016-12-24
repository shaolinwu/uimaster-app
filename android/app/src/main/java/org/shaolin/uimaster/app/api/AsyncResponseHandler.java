package org.shaolin.uimaster.app.api;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import android.os.Looper;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class AsyncResponseHandler extends AsyncHttpResponseHandler {

	private Object[] args;

	public AsyncResponseHandler(Looper looper, Object... args) {
		super(looper);
		this.args = args;
	}

	public AsyncResponseHandler(Object... args) {
		this.args = args;
	}

	public void onFailure(int code, String errorMessage, Object[] args) {
	}

	public void onSuccess(int code, ByteArrayInputStream is, Object[] args)
			throws Exception {
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
		try {
			onSuccess(statusCode, new ByteArrayInputStream(responseBody), args);
		} catch (Exception e) {
			e.printStackTrace();
			onFailure(statusCode, e.getMessage(), args);
		}
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
		try {
			onFailure(statusCode, new String(responseBody, "utf-8"), args);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
