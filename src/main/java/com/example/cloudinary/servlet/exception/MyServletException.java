package com.example.cloudinary.servlet.exception;

import com.example.cloudinary.servlet.model.Errors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.ws.rs.WebApplicationException;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class MyServletException extends WebApplicationException
{
	private Errors errors;

	public MyServletException(Errors errors)
	{

		super(errors.getDetails(), errors.getHttpStatusCode());
		this.errors = errors;
	}

	public Errors getErrors()
	{
		return errors;
	}

}
