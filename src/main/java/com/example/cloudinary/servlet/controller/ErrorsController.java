package com.example.cloudinary.servlet.controller;

import com.example.cloudinary.servlet.exception.MyServletException;
import com.example.cloudinary.servlet.model.ApiErrorJson;
import com.example.cloudinary.servlet.model.Errors;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@RestController
@Validated
public class ErrorsController implements ErrorController
{

	@Override
	public String getErrorPath()
	{
		return "/error";
	}


	@RequestMapping(value = "/error", method = RequestMethod.GET)
	@ResponseBody ApiErrorJson onError(@Context HttpServletRequest request, @Context HttpServletResponse response)
	{

		if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode())
		{
			return new ApiErrorJson(Errors.ENDPOINT_DOES_NOT_EXIST);
		}
		if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
		{
			return new ApiErrorJson(Errors.REQUIRED_PARAMS_MISSING);
		}

		return new ApiErrorJson(Errors.UNEXPECTED_ERROR);
	}
}
