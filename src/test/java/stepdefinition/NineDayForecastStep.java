package stepdefinition;

import base.Base;
import base.DriverContext;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import page.NineDayForecastPage;

public class NineDayForecastStep extends Base {
    NineDayForecastPage nineDayForecastPage = new NineDayForecastPage();
	@Given("User opens myObseravtory app")
    public void openObseravtory(){
        nineDayForecastPage.openObseravtory();
    }

    @And("User goes to 9-Day forecast")
    public void nineDayForecast(){
        nineDayForecastPage.nineDayForecast();
    }

    @Then("User checks tomorrow forecast")
    public void userChecksTomorrowForecast() {
	    nineDayForecastPage.checkTomorrowForecast();
    }
}
