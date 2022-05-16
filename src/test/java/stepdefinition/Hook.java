package stepdefinition;

import base.Base;
import base.DriverContext;
import com.aventstack.extentreports.service.ExtentService;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.Result;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public class Hook extends Base {

	@Before
	public void beforeScenario(Scenario scenario) throws IOException {
		Base.setScenario(scenario);
		String testResultFolder = ExtentService.getScreenshotFolderName().split("/")[1];
		System.setProperty("currReportFolder", testResultFolder);
		Collection<String> tags = scenario.getSourceTagNames();
		for(String tag: tags){
			System.out.println(tag);
		}
		Base.setCaseID(scenario.getName());
		// System.out.println("This will run before the Scenario");
		System.out.println("-----------------------------------");
		System.out.println("Starting - " + scenario.getName());
		System.out.println("-----------------------------------");

		String platform = scenario.getName().endsWith("IOS")? "IOS" : "ANDROID";
		setCapabilities();
	}
	

	@After
	public void afterScenario(Scenario scenario) {
		System.out.println("-----------------------------------");
		System.out.println(scenario.getName() + " Status - " + scenario.getStatus());
		System.out.println("Run time - " + getRunTime());
		System.out.println("-----------------------------------");
			

//		if ("failed".equals(scenario.getStatus())) {
		if ("FAILED".equals(scenario.getStatus().toString())) {

//			}
			client.setReportStatus("failed", "Test case failed");
			renameReportFolder("fail");
		}else {
			client.setReportStatus("passed", "Test case passed");
			renameReportFolder("pass");
		}
		 
//
		try {
			DriverContext.getDriver().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			DriverContext.getDriver().quit(); // Added by DF
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			DriverContext.setDriver(null); // Added by DF
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterStep
	public void afterStep(Scenario scenario) {

		if ("FAILED".equals(scenario.getStatus().toString())) {
			Base.saveCucumberScreenShotForStep("error");
		}

	}

	private static Throwable getError(Scenario scenario){
		Field field = FieldUtils.getField(((Scenario) scenario).getClass(), "stepResults", true);
		field.setAccessible(true);

		try{
			ArrayList<Result> results = (ArrayList<Result>) field.get(scenario);
			for(Result result : results){
				if(result.getError() != null){
					return result.getError();
				}
			}
		}catch (Exception e){
			System.out.println("fail to get error");
		}

		return null;
	}
}
