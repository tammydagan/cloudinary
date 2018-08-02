package com.example.cloudinary.servlet.service;

import com.example.cloudinary.servlet.exception.MyServletException;
import com.example.cloudinary.servlet.model.Errors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ResizeService
{

	public static String ORIGINAL_SUFFIX = "-original.jpeg";
	public static String MANIPULATED_SUFFIX = ".jpeg";
	public static String DIRECTORY = "C:\\MyServlet";

	private static final ConcurrentHashMap<String, Object> processingRequestsCache = new ConcurrentHashMap<>();

	private static final Logger logger = LogManager.getLogger(ResizeService.class);

	public static String getOutputPathOriginal(String uuid)
	{
		return DIRECTORY + "\\" + uuid + ORIGINAL_SUFFIX;
	}

	public static String getOutputPathManipulated(String manipulatedUuid)
	{
		return DIRECTORY + "\\" + manipulatedUuid + MANIPULATED_SUFFIX;
	}

	public static String getOriginalUuid(String url)
	{
		return UUID.nameUUIDFromBytes(url.getBytes()).toString();
	}

	public static String getManipulatedUuid(String url, int width, int height)
	{
		String manipulatedUuidToHash = url + ":w=" + width + ":h=" + height;
		return UUID.nameUUIDFromBytes(manipulatedUuidToHash.getBytes()).toString();
	}


	public BufferedImage handleResizeRequest(String url, int width, int height) throws MyServletException
	{

		String uuid = getOriginalUuid(url);
		logger.info("Handling request for url + " + url, " mapped to uuid: " + uuid);
		String manipulatedUuid = getManipulatedUuid(url, width, height);
		String outputPathOriginal = getOutputPathOriginal(uuid);
		String outputPathManipulated = getOutputPathManipulated(manipulatedUuid);


		BufferedImage imageFromUrl = getImageFromUrl(url, outputPathOriginal);
		//allow other threads to use the downloaded image
		return getManipulatedImage(imageFromUrl, outputPathManipulated, width, height);

	}


	private BufferedImage getImageFromUrl(String urlStr, String outputPath) throws MyServletException
	{
		logger.info("starting getImageFromUrl for url: " + urlStr);
		String originalUniqueName = outputPath.substring(outputPath.lastIndexOf("\\")+1);
		BufferedImage image = null;
		synchronized (processingRequestsCache.computeIfAbsent(originalUniqueName, k -> new Object()))
		{
			logger.info("locking download in getImageFromUrl for url: " + urlStr + " with file name " + originalUniqueName);
			URLConnection openConnection = null;

			try
			{
				//check whether the original file already exists
				File originalFile = new File(outputPath);
				if (!originalFile.exists())
				{
					String outputDirectory = outputPath.substring(0,outputPath.lastIndexOf("\\"));
					File f = new File(outputDirectory);
					if (!f.exists()) {
						new File(outputDirectory).mkdir();
					}

					URL url = new URL(urlStr);
					openConnection = url.openConnection();
					openConnection.setConnectTimeout(20000);
					openConnection.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
					openConnection.connect();
					String redirect = openConnection.getHeaderField("Location");

					if (redirect != null) //handle single redirect
					{
						openConnection = new URL(redirect).openConnection();
						openConnection.connect();
						redirect = openConnection.getHeaderField("Location");
						if (redirect != null) // not handling second redirect, if any
						{
							logger.error("error in getImageFromUrl - more than one redirect per url " + urlStr);
							throw new MyServletException(Errors.INVALID_INTERNAL_REDIRECT_NUMBER);
						}
					}

					if (openConnection.getContentLength() > 8000000) // assuming file size limitation
					{
						logger.error("return from getImageFromUrl - file size is too big for url " + urlStr);
						throw new MyServletException(Errors.FILE_SIZE_TOO_BIG);
					}


					image = ImageIO.read(openConnection.getInputStream());
					if (image == null)
					{
						logger.error("return from getImageFromUrl - couldn't read an image from this link for url " + urlStr);
						throw new MyServletException(Errors.URL_IS_NOT_AND_IMAGE);
					}

					//special handling for BMP, caught in PNG test
					if (image.getType() !=5)
					{
						BufferedImage imageCopy = new BufferedImage(image.getWidth(), image.getHeight(), 5);
						imageCopy.createGraphics().drawImage(image,0,0, image.getWidth(), image.getHeight(), null);
						image = imageCopy;
					}

					ImageIO.write(image, "JPEG", new File(outputPath));
				}
				else
				{
					logger.info("return from getImageFromUrl - file exists - download from URL not necessary for url " + urlStr);
					image = ImageIO.read(originalFile);;
				}
			}
			catch (MalformedURLException e)
			{
				logger.error("return from getImageFromUrl - URL cannot be parsed for url " + urlStr, e);
				throw new MyServletException(Errors.URL_CANNOT_BE_PARSED);
			}
			catch (SocketTimeoutException e)
			{
				logger.error("return from getImageFromUrl - connection timeout for url " + urlStr, e);
				throw new MyServletException(Errors.CONNECTION_TIMEOUT);
			}
			catch (IOException e)
			{
				logger.error("return from getImageFromUrl - Couldn't create a connection to the link, please recheck the link for url " + urlStr, e);
				throw new MyServletException(Errors.URL_NOT_FOUND);
			}
			finally
			{
				if (openConnection != null)
				{
					try
					{
						openConnection.getInputStream().close();
					}
					catch (IOException ex)
					{
						logger.error("ERROR closing image input stream for url " + urlStr, ex);
					}
				}
				processingRequestsCache.remove(originalUniqueName);
				logger.info("release lock in getImageFromUrl for object with key " + originalUniqueName + " for url "+ urlStr);
			}
		}

		return image;
	}



	private BufferedImage getManipulatedImage(BufferedImage originalImage, String outputPath, int scaledWidth, int scaledHeight) throws MyServletException
	{
		logger.info("starting getManipulatedImage for input path " + originalImage);
		String manipulatedUniqueName = outputPath.substring(outputPath.lastIndexOf("\\") + 1);
		BufferedImage outputImage = null;
		synchronized (processingRequestsCache.computeIfAbsent(manipulatedUniqueName, k -> new Object()))
		{
			logger.info("locking getManipulatedImage for file name " + manipulatedUniqueName);
			try
			{
				File file = new File(outputPath);
				if (!file.exists())
				{
					String outputDirectory = outputPath.substring(0, outputPath.lastIndexOf("\\"));
					File f = new File(outputDirectory);
					if (!f.exists())
					{
						try
						{
							new File(outputDirectory).mkdir();
						}
						catch (SecurityException e)
						{
							logger.error("cannot create directory ", e);
							throw new MyServletException(Errors.UNEXPECTED_ERROR);
						}
					}

					// creates output image
					outputImage = new BufferedImage(scaledWidth, scaledHeight, originalImage.getType());

					int originalWidth = originalImage.getWidth();
					int originalHeight = originalImage.getHeight();

					// scales the input image to the output image
					Graphics2D g2d = outputImage.createGraphics();

					int x = 0;
					int y = 0;
					int manipulatedWidth = scaledWidth;
					int manipulatedHeight = scaledHeight;
					boolean paddingAdded = false;
					float originalRatio = (float) originalWidth / (float) originalHeight;
					originalRatio = Math.round(originalRatio * 100) / 100F;

					// height and with are both bigger - scale up
					// one bigger one smaller - scale down according to the small one, then pad the rest
					// both are smaller - scale down according to the small one, then pad the rest to match
					if ((scaledWidth < originalWidth) || (scaledHeight < originalHeight)) //scale down
					{
						float manipulatedRatio = Math.round(((float) scaledWidth / (float) scaledHeight) * 100) / 100F;
						if (manipulatedRatio > originalRatio) //given height is bigger, scale down height
						{
							manipulatedWidth = Math.round(scaledHeight * originalRatio);
							x = (scaledWidth - manipulatedWidth) / 2;
							paddingAdded = true;
						}
						else if (manipulatedRatio < originalRatio)//given width is bigger, scale down width
						{
							//find closet ratio and fill with padding
							//width is fine, scale down height
							manipulatedHeight = Math.round(scaledWidth / originalRatio);
							y = (scaledHeight - manipulatedHeight) / 2;
							paddingAdded = true;
						}
						else
						{
							logger.info("ratios match, can scale down without padding for output " + outputPath + " with height " + scaledHeight + " and width " + scaledWidth);
						}
					}
					else // scale up
					{
						if (originalWidth < scaledWidth) // add padding for width
						{
							x = (scaledWidth - originalWidth) / 2;
							manipulatedWidth = originalWidth;
							paddingAdded = true;
						}
						if (originalHeight < scaledHeight) // add padding for height
						{
							y = (scaledHeight - originalHeight) / 2;
							manipulatedHeight = originalHeight;
							paddingAdded = true;
						}
					}

					if (paddingAdded)
					{

						g2d.setColor(Color.BLACK);
						if (y > 0 && scaledWidth > 0)
						{
							//bottom rect
							g2d.fillRect(0, 0, scaledWidth, y);
							//upper rect
							g2d.fillRect(0, scaledHeight-y, scaledWidth, y);
						}

						if (x > 0 && scaledHeight > 0)
						{
							//left rect
							g2d.fillRect(0, 0, x, scaledHeight);
							//right rect
							g2d.fillRect(scaledWidth-x, 0, x, scaledHeight);
						}
					}

					g2d.drawImage(originalImage, x, y, manipulatedWidth, manipulatedHeight, null);
					g2d.dispose();

					ImageIO.write(outputImage, "jpeg", file);
				}
				else
				{
					logger.info("return from getManipulatedImage - file exists - getManipulatedImage is not necessary for  " + outputPath);
					outputImage = ImageIO.read(file);
				}
			}
			catch (IOException ex)
			{
				logger.error("unexpected IO exception in getManipulatedImage for output path " + outputPath, ex);
				throw new MyServletException(Errors.UNEXPECTED_ERROR);
			}
			catch (Exception ex)
			{
				throw new MyServletException(Errors.UNEXPECTED_ERROR);
			}
			finally
			{
				processingRequestsCache.remove(manipulatedUniqueName);
				logger.info("release lock in resize for object with key " + manipulatedUniqueName);
			}
		}

		return outputImage;
	}
}
