package com.example.cloudinary;

import com.example.cloudinary.servlet.controller.ResizeController;
import com.example.cloudinary.servlet.model.ApiErrorJson;
import com.example.cloudinary.servlet.model.Errors;
import com.example.cloudinary.servlet.service.ResizeService;
import com.example.cloudinary.utils.TestUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.imageio.ImageIO;
import javax.ws.rs.core.MediaType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@WebMvcTest(ResizeController.class)
@ContextConfiguration(classes=CloudinaryApplication.class)
//@WebAppConfiguration
public class CloudinaryApplicationTests {

	private MockMvc mockMvc;

	@Autowired
	private ResizeController resizeController;


	@Test
	public void contextLoads() {
	}


	@Before
	public void init(){
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders
		 .standaloneSetup(resizeController)
		 .build();
	}


	//invalid endpoint
	@Test
	public void testInvalidEndpoint() throws Exception
	{

		mockMvc.perform(get("/sdfsdf"))
		 .andExpect(status().isNotFound());

	}

	//valid endpoint, no parameters
	@Test
	public void testNoParameters() throws Exception
	{

		mockMvc.perform(get("/thumbnail")
		 .contentType(MediaType.APPLICATION_JSON))
		 .andExpect(status().isBadRequest())
		 .andExpect(content().json(TestUtil.asJsonString(new ApiErrorJson(Errors.INVALID_URL_PARAMETERS))));
	}

	//invalid URLs
	@Test
	public void testUrlNotAnImage() throws Exception
	{

		mockMvc.perform(get("/thumbnail").param("url", TestUtil.NOT_AN_IMAGE_URL)
		 .param("width", "120")
		 .param("height", "450")
		 .contentType(MediaType.APPLICATION_JSON))
		 .andExpect(status().isBadRequest())
		 .andExpect(content().json(TestUtil.asJsonString(new ApiErrorJson(Errors.URL_IS_NOT_AND_IMAGE))));
	}

	@Test
	public void testUrlDoesNotExist() throws Exception
	{

		mockMvc.perform(get("/thumbnail").param("url", TestUtil.INVALID_URL)
		 .param("width", "120")
		 .param("height", "450")
		 .contentType(MediaType.APPLICATION_JSON))
		 .andExpect(status().isBadRequest())
		 .andExpect(content().json(TestUtil.asJsonString(new ApiErrorJson(Errors.URL_NOT_FOUND))));
	}


	@Test
	public void testInvalidWidthDouble() throws Exception
	{

		mockMvc.perform(get("/thumbnail").param("url", TestUtil.CAT_URL)
		 .param("width", "120.22")
		 .param("height", "450")
		 .contentType(MediaType.APPLICATION_JSON))
		 .andExpect(status().isBadRequest())
		 .andExpect(content().json(TestUtil.asJsonString(new ApiErrorJson(Errors.INVALID_URL_PARAMETERS))));
	}

	@Test
	public void testInvalidWidthNegative() throws Exception
	{

		mockMvc.perform(get("/thumbnail").param("url", TestUtil.CAT_URL)
		 .param("width", "-120")
		 .param("height", "450")
		 .contentType(MediaType.APPLICATION_JSON))
		 .andExpect(status().isBadRequest())
		 .andExpect(content().json(TestUtil.asJsonString(new ApiErrorJson(Errors.INVALID_URL_PARAMETERS))));
	}

	@Test
	public void testSameSize() throws Exception
	{

		testSameSize(TestUtil.CAT_URL, 640, 426);
		testSameSize(TestUtil.ARMIN_URL, 640, 234);
		testSameSize(TestUtil.BMP_IMAGE, 640, 345);
		testSameSize(TestUtil.DOWNLOADABLE_BMP_IMAGE, 640, 456);
		testSameSize(TestUtil.TIFF_IMAGE, 340, 466);
		testSameSize(TestUtil.DOG_URL, 440, 234);
		testSameSize(TestUtil.PNG_IMAGE, 540, 426);
	}



	@Test
	public void testOriginalAndManipulatedExistJPG() throws Exception
	{
		testEndToEnd(TestUtil.ARMIN_URL, 500, 400);

	}

	@Test
	public void testOriginalAndManipulatedExistBMP() throws Exception
	{
		testEndToEnd(TestUtil.BMP_IMAGE, 500, 400);
	}

	@Test
	public void testOriginalAndManipulatedExistdDownloadableBMP() throws Exception
	{
		testEndToEnd(TestUtil.DOWNLOADABLE_BMP_IMAGE, 500, 400);
	}


	@Test
	public void testOriginalAndManipulatedExistPNG() throws Exception
	{
		testEndToEnd(TestUtil.PNG_IMAGE, 500, 400);
	}

	@Test
	public void testOriginalAndManipulatedExistTIFF() throws Exception
	{
		testEndToEnd(TestUtil.TIFF_IMAGE, 500, 400);
	}

	@Test
	public void multipleThreadsTest() throws InterruptedException
	{

		ExecutorService executor = Executors.newCachedThreadPool();

		CountDownLatch countDownLatch = new CountDownLatch(20);
		for (int i=1; i<21; i++)
		{
			final int counter = i;
			executor.submit(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						testEndToEnd(TestUtil.ARMIN_URL, counter * 120, counter * 80);
						countDownLatch.countDown();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
		}

		executor.awaitTermination(10, TimeUnit.SECONDS);
		Assert.assertTrue(countDownLatch.getCount() == 0);
		executor.shutdownNow();
	}


	private void testEndToEnd(String url, int width, int height) throws Exception
	{
		mockMvc.perform(get("/thumbnail").param("url", url)
		 .param("width", Integer.toString(width))
		 .param("height", Integer.toString(height)))
		 .andExpect(status().isOk())
		 .andExpect(content().contentType(org.springframework.http.MediaType.IMAGE_JPEG));

		String uuid = ResizeService.getOriginalUuid(url);
		String manipulatedUuid = ResizeService.getManipulatedUuid(url, width, height);
		String outputPathOriginal = ResizeService.getOutputPathOriginal(uuid);
		String outputPathManipulated = ResizeService.getOutputPathManipulated(manipulatedUuid);

		File fileManipulated = new File(outputPathManipulated);
		Assert.assertTrue(fileManipulated.exists());
		File fileJpeg = new File(outputPathOriginal);
		Assert.assertTrue(fileJpeg.exists());
	}



	private void testSameSize(String url, int width, int height) throws Exception
	{

		mockMvc.perform(get("/thumbnail").param("url", url)
		 .param("width", Integer.toString(width))
		 .param("height", Integer.toString(height)))
		 .andExpect(status().isOk())
		 .andExpect(content().contentType(org.springframework.http.MediaType.IMAGE_JPEG));


		String manipulatedUuid = ResizeService.getManipulatedUuid(url, width, height);
		String outputPathManipulated = ResizeService.getOutputPathManipulated(manipulatedUuid);
		File outputFile = new File(outputPathManipulated);

		Assert.assertTrue(outputFile.exists());
		BufferedImage outputImage = ImageIO.read(outputFile);
		Assert.assertNotNull(outputImage);
		Assert.assertEquals(outputImage.getHeight(), height);
		Assert.assertEquals(outputImage.getWidth(), width);
	}


}
