package base;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;

//import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestContext;

import com.experitest.appium.SeeTestClient;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.cucumber.java.Scenario;
import test.TestRun;

public class Base {
	public static Scenario scenario;
	private static AppiumDriverLocalService service;
	private static String caseID;
	protected static SeeTestClient client;
	private static boolean original_flag, changed_flag;
	private static String error_msg = "Sorry! Test failed !";
	public static String platform;
	private static long start_time;
	private static String url;
	private static String run_mode = "local";
	private static String fail_reason = "";

	public static void setStartTime() {
		start_time = System.currentTimeMillis();
	}
	public static void setFailReason(String reason){
		fail_reason = reason;
	}
	public static String getFailReason(){
		return fail_reason ;
	}

	//no runtime
	public static String getRunTime() {
		long run_time = System.currentTimeMillis() - start_time;
		long currentMS = run_time % 1000;
		long totalSeconds = run_time / 1000;
		long currentSecond = totalSeconds % 60;
		long totalMinutes = totalSeconds / 60;
		long currentMinute = totalMinutes % 60;

		String use_time = String.valueOf(currentMinute) + "m" + String.valueOf(currentSecond) + "."
				+ String.valueOf(currentMS) + "s";
		return use_time;
	}

	public static String getRunMode(){
		return run_mode;
	}

	public static void setRunMode(String mode){
		run_mode = mode;
	}

	public static void setErrorMsg(String msg) {
		error_msg += " " + msg;
	}

	public static void setFlag(boolean value) {
		if (value) {
			original_flag = true;
			changed_flag = false;
		} else {
			original_flag = false;
			changed_flag = true;
		}
		client.setShowPassImageInReport(original_flag);
		client.setShowReport(original_flag);
	}

	public static SeeTestClient getClient() {
		return client;
	}

	public static void setScenario(Scenario scenario) {
		Base.scenario = scenario;
	}

	public static void saveCucumberScreenShotForStep(String fileName) {//public static synchronized void
//		String adjustedFileName = adjustFileName(fileName);

//		byte[] picData = doCaptureScreen(DriverContext.getDriver());
		byte[] picData = DriverContext.getDriver().getScreenshotAs(OutputType.BYTES);

		try {
			scenario.attach(picData, "image/png", fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addCapture(String msg) {
		addCapture(msg, 0);
	}

	// when test failed ,add error_msg
	public static void addCapture() {
		client.setShowReport(true);
		client.report(error_msg, false);
		error_msg = "Sorry! Test failed !";
		saveCucumberScreenShotForStep("error");
	}

	public static void addCapture(String msg, int time) {
		System.out.println("add capture for " + msg);
		saveCucumberScreenShotForStep(msg);
		if (changed_flag) {
			client.setShowPassImageInReport(changed_flag);
			client.setShowReport(changed_flag);
			try {
				Thread.sleep(time);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			client.report(msg, true);
			client.setShowReport(original_flag);
			client.setShowPassImageInReport(original_flag);
		} else {
			// client.report(msg, true);
		}

	}

	public static String getCaseID() {
		return caseID;
	}

	public static void setCaseID(String caseID) {
		Base.caseID = caseID;
	}

	public AppiumDriverLocalService startServer() {
		boolean flag = checkIfServerIsRunnning(4723);

		if (!flag) {
			service = AppiumDriverLocalService.buildDefaultService();
			service.start();
		}
		return service;
	}

	public static boolean checkIfServerIsRunnning(int port) {
		boolean isServerRunning = false;
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(port);

			serverSocket.close();
		} catch (IOException e) {
			isServerRunning = true;
		} finally {
			serverSocket = null;
		}
		return isServerRunning;
	}

	public static void setCapabilities() throws IOException {
		String reportDirectory = "reports/fonio";
		String reportFormat = "xml";
//		String fileSeparator = File.separator;
		String fileSeparator = "/";
		String projectBaseDirectory = System.getProperties().getProperty("user.dir");
		
		String appPackage = "Iocspc";

		ITestContext itc = TestRun.itc;
		// device setting
		String device = itc.getCurrentXmlTest().getParameter("device");
		platform = itc.getCurrentXmlTest().getParameter("platform");
		String useProxy = itc.getCurrentXmlTest().getParameter("useProxy");
		url = itc.getCurrentXmlTest().getParameter("url");
		String accessKey = itc.getCurrentXmlTest().getParameter("accessKey");

		if ("TRUE".equalsIgnoreCase(useProxy)) {
			System.setProperty("https.proxyHost", "10.7.192.136");
			System.setProperty("https.proxyPort", "10938");
		}

		if ("".equals(url)) {
			url = "https://aia.experitest.com/wd/hub";
		}
		if (url.contains("experitest")){
			run_mode = "cloud";
		}

//Added by DF		if (DriverContext.getDriver() == null) {

			DesiredCapabilities dc = new DesiredCapabilities();
			// dc.setCapability("report.disable", true);
			dc.setCapability("instrumentApp", true);
			
			//debug 的时候打开
//			dc.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 600);

			// report setting
			dc.setCapability("reportDirectory", reportDirectory);
			dc.setCapability("reportFormat", reportFormat);
			dc.setCapability("testName", caseID);

//			if (url.contains("experitest")) {
//				dc.setCapability(MobileCapabilityType.APP,"cloud:com.aiahk.idirect.uat/com.aiaconnect.MainActivity:4.316");
//			}
			
			dc.setCapability(MobileCapabilityType.UDID, device);
			dc.setCapability("platformName", platform);
			dc.setCapability("accessKey", accessKey);
		//TODO:QuickDebug
			if (platform.equalsIgnoreCase("ANDROID")) {
				System.out.println("Connected to Android device");
				dc.setCapability("appPackage", appPackage);
			
				DriverContext.setDriver((AppiumDriver<MobileElement>) (new AndroidDriver<MobileElement>(new URL(url), dc)));
			} else {
				System.out.println("Connected to IOS device");
//				appPackage = "com.accentuerlab.connect.uat";
				dc.setCapability(IOSMobileCapabilityType.BUNDLE_ID, appPackage);

				
				DriverContext.setDriver((AppiumDriver<MobileElement>) (new IOSDriver<MobileElement>(new URL(url), dc)));
			}

			System.out.println("=============create a new driver!=============");
			DriverContext.setDeviceModel();
			DriverContext.setDeviceOS();
			DriverContext.setDeviceName();

			client = new SeeTestClient(DriverContext.getDriver());


		setFlag(true);//打开全部截图

		if (!url.contains("experitest")) {
			client.setReporter(reportFormat, projectBaseDirectory + fileSeparator + reportDirectory + fileSeparator , caseID);
		}

		client.setSpeed("Fast");
		setStartTime();
	}

	public static void renameReportFolder(String status) {
		// pass,fail,skip
		String report_path = null;
		if (Base.getRunMode().equalsIgnoreCase("cloud")){
			report_path = client.generateReport(true);
		}else{
			report_path = client.generateReport(false);
		}
		System.out.println(Base.getRunMode());
		System.out.println(report_path);
		if (!url.contains("experitest")) {
			String parentDir = report_path.substring(0, report_path.lastIndexOf(File.separator));
			
			File newFolder = null;
			File fail_Folder = null;
			boolean delete_fail_flag = false;
			switch (status) {
				case "pass":
					newFolder = new File(parentDir + File.separator + caseID);
					try {
						fail_Folder = new File(parentDir + File.separator + caseID + "_FAIL");
						if (fail_Folder.exists() && fail_Folder.isDirectory()) {
							System.out.println("has past failed report!");
							delete_fail_flag = true;
						} else {
							System.out.println("does not has past failed report!");
						}
					} catch (Exception e) {
					}
					break;
				case "fail":
					newFolder = new File(parentDir + File.separator + caseID + "_FAIL");
					break;
				default:
					newFolder = new File(parentDir + File.separator + caseID + "_SKIP");
					break;
			}
			if (newFolder.exists() && newFolder.isDirectory()) {
				System.out.println("new folder name already exist,delete it");
				try {
					FileUtils.deleteDirectory(newFolder);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// newFolder.delete();
			}
			if (delete_fail_flag) {
				System.out.println("delete past fail report");
				try {
					FileUtils.deleteDirectory(fail_Folder);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			new File(report_path).renameTo(newFolder);
			System.out.println("report renamed to " + caseID);
		}

		//false : funcion off ,  true : funcion on

	}

	public void delay(long delaySec) {
		try {
			Thread.sleep(delaySec * 1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}


}