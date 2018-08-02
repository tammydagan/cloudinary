package com.example.cloudinary.servlet.controller;

import com.example.cloudinary.servlet.exception.MyServletException;
import com.example.cloudinary.servlet.model.ApiErrorJson;
import com.example.cloudinary.servlet.model.Errors;
import com.example.cloudinary.servlet.service.ResizeService;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;


@RestController
@Validated
public class ResizeController
{

	@Autowired
	private ResizeService resizeService;


	@RequestMapping(value = "/thumbnail", method = RequestMethod.GET)
	public void resize(@Context HttpServletRequest request, @Context HttpServletResponse response,
	                   @QueryParam("url") @URL String url,
	                   @QueryParam("width") @Min(1) Integer width,
	                   @QueryParam("height") @NotNull @Min(1) Integer height) throws MyServletException
	{


		url = url.replaceAll(" ", "%20");
		BufferedImage outputImage = resizeService.handleResizeRequest(url, width, height);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		try
		{
			ImageIO.write(outputImage, "jpeg", response.getOutputStream());
		}
		catch (IOException ex)
		{
			throw new MyServletException(Errors.UNEXPECTED_ERROR);
		}
	}


	//error handlers

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseBody
	ApiErrorJson handleConstraintViolationException(HttpServletResponse resp, ConstraintViolationException ex) {
		return new ApiErrorJson(Errors.INVALID_URL_PARAMETERS);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MyServletException.class)
	@ResponseBody ApiErrorJson handleMyServletException(HttpServletResponse resp, MyServletException ex) {
		return new ApiErrorJson(ex.getErrors());
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseBody ApiErrorJson handleMethodArgumentTypeMismatchException(HttpServletResponse resp, MethodArgumentTypeMismatchException ex) {
		return new ApiErrorJson(Errors.INVALID_URL_PARAMETERS);
	}


	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MalformedURLException.class)
	@ResponseBody
	ApiErrorJson
	handleMalformedURLException(HttpServletResponse resp, MalformedURLException ex) {
		return new ApiErrorJson(Errors.INVALID_URL);
	}

}
