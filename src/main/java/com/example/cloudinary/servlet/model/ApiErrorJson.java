package com.example.cloudinary.servlet.model;

import com.example.cloudinary.servlet.exception.MyServletException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.ws.rs.core.Response.Status;
import java.io.Serializable;
import java.util.Objects;


public class ApiErrorJson implements Serializable
{

	public final int internalErrorCode;
	public final String errorMessage;
	public final Status httpStatus;



	public ApiErrorJson(Errors errors) {

		this.internalErrorCode = errors.getInternalErrorCode();
		this.httpStatus = errors.getHttpStatusCode();
		this.errorMessage = errors.getDetails();
	}


	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (!(o instanceof ApiErrorJson))
			return false;
		ApiErrorJson errorInfo = (ApiErrorJson) o;
		return internalErrorCode == errorInfo.internalErrorCode &&
		 Objects.equals(errorMessage, errorInfo.errorMessage) &&
		 httpStatus == errorInfo.httpStatus;
	}

	@Override
	public int hashCode()
	{

		return Objects.hash(internalErrorCode, errorMessage, httpStatus);
	}

	@Override
	public String toString()
	{
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
