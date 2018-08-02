package com.example.cloudinary.servlet.model;

import javax.ws.rs.core.Response.Status;

public enum Errors
{

	INVALID_URL_PARAMETERS(1, Status.BAD_REQUEST, "invalid parameters"),
	MISSING_URL_PARAMETERS(2, Status.BAD_REQUEST, "missing parameters"),
	FILE_SIZE_TOO_BIG(3, Status.BAD_REQUEST, "file size too big"),
	URL_IS_NOT_AND_IMAGE(4, Status.UNSUPPORTED_MEDIA_TYPE, "URL is not an image"),
	URL_CANNOT_BE_PARSED(5, Status.UNSUPPORTED_MEDIA_TYPE, "URL cannot be parsed"),
	CONNECTION_TIMEOUT(6, Status.REQUEST_TIMEOUT, "Request timeout - parsing took too long"),
	URL_NOT_FOUND(7, Status.NOT_FOUND, "URL cannot be found"),
	INVALID_URL(8, Status.BAD_REQUEST, "invalid URL"),
	INVALID_INTERNAL_REDIRECT_NUMBER(9, Status.BAD_REQUEST, "invalid internal redirect number for URL"),
	UNEXPECTED_ERROR(10, Status.BAD_REQUEST, "couldn't complete the request, an unexpected error has occurred. Please try again later"),
	ENDPOINT_DOES_NOT_EXIST(11, Status.NOT_FOUND, "endpoint does not exist"),
	REQUIRED_PARAMS_MISSING(12, Status.BAD_REQUEST, "required params missing - make sure you add url, height and width");


	private int internalErrorCode;
	private Status httpStatusCode;
	private String details;

	Errors(int internalErrorCode, Status httpStatusCode, String details)
	{
		this.internalErrorCode = internalErrorCode;
		this.httpStatusCode = httpStatusCode;
		this.details = details;
	}

	public int getInternalErrorCode()
	{
		return internalErrorCode;
	}

	public Status getHttpStatusCode()
	{
		return httpStatusCode;
	}

	public String getDetails()
	{
		return details;
	}



}
