package utilities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import base.Base;
import base.DriverContext;

/**
 * Provide screenshot taking action for other keyword implemented in JAVA.
 * This class will compress image for the screenshot.
 * All keywords should utilize this class to take screenshot but not create  with "new".
 */
public class ScreenShotUtil {
	private ScreenShotUtil(){

	}
	
	private static ThreadLocal<Integer> noOfStep = new ThreadLocal<>();

	private static final String FILE_TYPE = "jpg";

//	public static void setScenario(Scenario scenario) {
//		ScreenShotUtil.myScenario = scenario;
//	}

//	private static Scenario myScenario;

	/**
	 * Capture screen and compress the captured image into 5-5-5 RGB image.
	 * @param element This is instance for taking screenshot.
	 * @return byte array for the compressed image.
	 */
	private static byte[] doCaptureScreen(TakesScreenshot element){
		byte[] rawData = element.getScreenshotAs(OutputType.BYTES);

		ByteArrayOutputStream result = new ByteArrayOutputStream();
		
		//compress the png data first. Otherwise the screen shot will be very big.
		BufferedImage originalImage = null;
		try {
		    originalImage = ImageIO.read(new ByteArrayInputStream(rawData));
		    
		    if(originalImage == null){
		    	return rawData;
		    }
		    
		    BufferedImage compressImage = new BufferedImage(originalImage.getWidth(), 
		    												originalImage.getHeight(),
		    												BufferedImage.TYPE_USHORT_555_RGB);
		    
		    Graphics2D g2d = (Graphics2D) compressImage.getGraphics();
		    g2d.drawImage(originalImage, 0, 0, null);
		    
		    ImageIO.write(compressImage, FILE_TYPE, result);
		    
		} catch (IOException e) {
			throw new RuntimeException("exception happen when the program try to compress screen shot.", e);
		}
		
		return result.toByteArray();
		
	}

	public static synchronized void saveScreenShotForStep(String fileName){
		String adjustedFileName = adjustFileName(fileName);

		byte[] picData = doCaptureScreen((TakesScreenshot) DriverContext.getDriver());

		String folder = System.getProperty("user.dir") + File.separator + "output" + File.separator + "screenshots" + File.separator + Base.getCaseID();
		File subfolder = new File(folder);

		if(!subfolder.exists()){
			subfolder.mkdirs();
		}

		File file = new File(subfolder, adjustedFileName);

		try (FileOutputStream output = new FileOutputStream(file)) {
			output.write(picData);
//			ExtentCucumberAdapter.addTestStepLog(fileName+"[##]");
			//use relative path
			String replacedString = folder + File.separator;
//			ExtentCucumberAdapter.addTestStepScreenCaptureFromPath(file.getPath());//, fileName
			addScreenShot();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch(IOException ie){
			throw new RuntimeException(ie);
		}

		increaseNoOfSteps();
	}

	public static String adjustFileName(String filename){
		DecimalFormat format = new DecimalFormat("0000");

		String prefix = format.format(noOfStep.get());

		return prefix + "_" + filename.replace(" ", "_") + "." + FILE_TYPE;
	}

	public static void resetNoOfSteps(){
		noOfStep.set(1);
	}

	private static void increaseNoOfSteps(){
		noOfStep.set(noOfStep.get() + 1);
	}

	public static void addScreenShot(){
//		TakesScreenshot ts = (TakesScreenshot) DriverContext.getDriver();
//		byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
//		
//		myScenario.attach(screenshot, "image/png","image");
		
		byte[] picData = DriverContext.getDriver().getScreenshotAs(OutputType.BYTES);
		Base.scenario.attach(picData, "image/png", "image");
	}

}
