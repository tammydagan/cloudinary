package com.example.cloudinary.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedList;
import java.util.List;

public class TestUtil
{

	public static String CAT_URL = "https://i2.wp.com/beebom.com/wp-content/uploads/2016/01/Reverse-Image-Search-Engines-Apps-And-Its-Uses-2016.jpg?getManipulatedImage=640%2C426";
	public static String ARMIN_URL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSqDM6CMELnn1ift3TQmGC3wMlzRuPSvrsTgMd9AD4ZVLifkIG3";
	public static String DOG_URL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTNv1q0tGcfwOZq-TkGGD_hLyy9eNaGgTnesh3vIBdb3-mXrS0m";
	public static String NOT_AN_IMAGE_URL = "https://stackoverflow.com/questions/25564533/how-to-mock-remote-rest-api-in-unit-test-with-spring";
	public static String INVALID_URL = "http://assada.asdasdasd.asdasdasdasdasd";
	public static String URL_PREFIX = "http://localhost:8080/thumbnail?";
	public static String BMP_IMAGE = "https://www.hamiltonregionettes.com/cake.bmp";
	public static String GIF_IMAGE = "https://media.giphy.com/media/kEKcOWl8RMLde/giphy.gif";
	public static String PNG_IMAGE = "http://www.pngall.com/wp-content/uploads/2016/07/Birthday-Cake-PNG-File.png";
	public static String TIFF_IMAGE ="http://happybirthdaycakepic.com/pic-preview/Tiff/81/candles-happy-birthday-cake-for-Tiff";
	public static String DOWNLOADABLE_BMP_IMAGE = "http://scienceblogs.com/retrospectacle/wp-content/blogs.dir/463/files/2012/04/i-2a015911ab6a70066e82fd8170265951-all%20my%20base%20cake.bmp";

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
