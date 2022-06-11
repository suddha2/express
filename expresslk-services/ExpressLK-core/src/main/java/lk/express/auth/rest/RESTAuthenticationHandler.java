package lk.express.auth.rest;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import lk.express.CRUD;
import lk.express.Context;
import lk.express.auth.AuthenticationHandler;
import lk.express.service.RestService.RestErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTAuthenticationHandler extends AuthenticationHandler {

	private static final Logger logger = LoggerFactory.getLogger(RESTAuthenticationHandler.class);

	private static final String AUTH_FAILED = "Authentication failed!";
	private static final String NOT_ALLOWED = "Method not allowed!";

	private String clazz;

	public RESTAuthenticationHandler(String serviveClass) {
		this.clazz = serviveClass;
	}

	public RESTAuthenticationHandler(Class<?> beanClass) {
		this.clazz = beanClass.getCanonicalName();
	}

	public boolean isAllowed(HttpHeaders httpHeaders, String methodName) {
		String username = getHeaderParam(httpHeaders, USERNAME);
		String token = getHeaderParam(httpHeaders, TOKEN);

		String user = authenticate(username, token);
		if (authorize(user, clazz, methodName)) {
			Context.getSessionData().empty().setUsername(user);
			logger.info("REST invocation, user: {}, service: {}, method: {}", new Object[] { username, clazz,
					methodName });
			return true;
		} else {
			String u = user == null ? "Anonymous user" : username;
			logger.info("Authorization failed for user: {} for service: {}, method: {}", new Object[] { u, clazz,
					methodName });
			return false;
		}
	}

	public Response buildErrorResponse(HttpHeaders httpHeaders, String methodName) {

		String username = getHeaderParam(httpHeaders, USERNAME);
		String token = getHeaderParam(httpHeaders, TOKEN);

		String user = null;
		if ((user = authenticate(username, token)) == null) {
			return Response.status(FORBIDDEN).entity(new RestErrorResponse(AUTH_FAILED)).build();
		} else {
			if (!authorize(user, clazz, methodName)) {
				return Response.status(METHOD_NOT_ALLOWED).entity(new RestErrorResponse(NOT_ALLOWED)).build();
			}
		}
		return null;
	}

	public boolean isAllowed(HttpHeaders httpHeaders, CRUD operation) {

		String username = getHeaderParam(httpHeaders, USERNAME);
		String token = getHeaderParam(httpHeaders, TOKEN);

		String user = authenticate(username, token);
		if (authorize(user, clazz, operation)) {
			Context.getSessionData().empty().setUsername(user);
			logger.info("REST invocation, user: {}, class: {}, operation: {}", new Object[] { username, clazz,
					operation });
			return true;
		} else {
			String u = user == null ? "Anonymous user" : username;
			logger.info("Authorization failed for user: {} for class: {}, operation: {}", new Object[] { u, clazz,
					operation });
			return false;
		}
	}

	public Response buildErrorResponse(HttpHeaders httpHeaders, CRUD operation) {

		String username = getHeaderParam(httpHeaders, USERNAME);
		String token = getHeaderParam(httpHeaders, TOKEN);

		String user = null;
		if ((user = authenticate(username, token)) == null) {
			return Response.status(FORBIDDEN).entity(new RestErrorResponse(AUTH_FAILED)).build();
		} else {
			if (!authorize(user, clazz, operation)) {
				return Response.status(METHOD_NOT_ALLOWED).entity(new RestErrorResponse(NOT_ALLOWED)).build();
			}
		}
		return null;
	}

	private String getHeaderParam(HttpHeaders httpHeaders, String param) {
		List<String> values = httpHeaders.getRequestHeader(param);
		if (values != null && values.size() > 0) {
			return values.get(0);
		}
		return null;
	}
}
