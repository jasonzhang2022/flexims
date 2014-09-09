package com.flexdms.flexims.rsutil;

import java.util.logging.Logger;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.exception.ExceptionUtils;

@Provider
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class ExceptionWriter implements ExceptionMapper<Exception> {

	public static final Logger LOGGER = Logger.getLogger(ExceptionWriter.class.getName());

	public ExceptionWriter() {

	}

	@Context
	Request request;

	@Override
	public Response toResponse(Exception exception) {
		try {
			exception.printStackTrace();
			Variant variant = request.selectVariant(RsHelper.VS);
			String message = ExceptionUtils.getRootCauseMessage(exception);
			if (message == null || message.length() == 0) {
				message = "No Error Message";
			}
			RSMsg ret = new RSMsg();
			ret.setMsg(message);
			if (exception instanceof HttpCodeException) {
				HttpCodeException codeException = (HttpCodeException) exception;
				ret.setStatuscode(codeException.getCode());
			} else if (exception instanceof WebApplicationException) {
				WebApplicationException exception2 = (WebApplicationException) exception;
				ret.setStatuscode(exception2.getResponse().getStatus());
			} else {
				ret.setStatuscode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			}

			ret.setMsg(message);

			return Response.status((int) ret.getStatuscode()).entity(ret).type(variant.getMediaType().toString()).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(e.getMessage()).type("text/plain").build();
		}

	}

}
