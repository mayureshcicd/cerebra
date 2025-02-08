package com.cerebra.fileprocessor.logger.helper;

import java.util.Arrays;
import java.util.List;

public class RequestHeaderHelper {

	public static final String AUTH_TOKEN = "AuthToken";
	//public static final String USER_ID = "user_id";
	public static final String AUTHORIZATION = "Authorization";

	public static final String CONTENT_TYPE = "Content-Type";

	public static final String ACCEPT = "Accept";

	public static final String CACHE_CONTROL = "Cache-Control";

	public static final String X_REQUESTED_WITH = "X-Requested-With";

	public static final String ORIGIN = "Origin";

	public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
	public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
	public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

	public static final String EMAIL = "email";

	public static final String X_API_KEY = "x-api-key";

	private RequestHeaderHelper() {
	}

	public static List<String> exposedHeaders() {
		return Arrays.asList(AUTHORIZATION);
	}

	public static List<String> allowedHeaders() {
		return Arrays.asList(AUTHORIZATION, AUTH_TOKEN, CONTENT_TYPE, ACCEPT, CACHE_CONTROL, X_REQUESTED_WITH, ORIGIN,
				ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_ORIGIN,
				ACCESS_CONTROL_REQUEST_HEADERS, ACCESS_CONTROL_REQUEST_METHOD, EMAIL, X_API_KEY);
	}
}
