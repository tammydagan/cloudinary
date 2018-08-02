package com.example.cloudinary;

import com.example.cloudinary.servlet.controller.ResizeController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan
public class CloudinaryApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(CloudinaryApplication.class, args);

	}
}








