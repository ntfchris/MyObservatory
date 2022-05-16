package base;

import org.json.JSONObject;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

public class DriverContext{
	
	public static AppiumDriver<MobileElement> driver;
	private static String deviceModel;
	private static String deviceOS;
	private static String deviceName;
	
	
	public static void setDriver(AppiumDriver<MobileElement> inputDriver)
	{
		driver = inputDriver;
	}
	
	public static AppiumDriver<MobileElement> getDriver()
	{
		return driver;
	}
	
	public static void setDeviceModel()
	{
		String result = driver.executeScript("seetest:client.getDeviceProperty('device.model');").toString();
        JSONObject jsonResult = new JSONObject(result);
        deviceModel = jsonResult.get("text").toString();
	}
	
	public static String getDeviceModel()
	{
        return deviceModel;
	}
	
	public static void setDeviceOS()
	{
		String result = driver.executeScript("seetest:client.getDeviceProperty('device.os');").toString();
        JSONObject jsonResult = new JSONObject(result);
        deviceOS = jsonResult.get("text").toString();
	}
	
	public static String getDeviceOS()
	{
        return deviceOS;
	}

	public static void setDeviceName()
	{
		String result = driver.executeScript("seetest:client.getDeviceProperty('device.name');").toString();
        JSONObject jsonResult = new JSONObject(result);
        deviceName = jsonResult.get("text").toString();
	}
	
	public static String getDeviceName()
	{
        return deviceName;
	}
	
}