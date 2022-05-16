package page;


import base.DriverContext;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSBy;
import io.appium.java_client.pagefactory.iOSFindAll;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.ScreenShotUtil;

import java.time.LocalDateTime;
import java.util.Date;

public class NineDayForecastPage {

    private AppiumDriver<MobileElement> driver = DriverContext.getDriver();

    public void nineDayForecast(){
        Assert.assertTrue(isElementPresent(By.xpath("//*[@text='MyObservatory' and @class='UIAStaticText']"),10));
        ScreenShotUtil.addScreenShot();
    }

    public void openObseravtory(){
        clickElement("//*[@text='Menu, left side panel']");
        clickElement("//*[@text='9-Day Forecast']");
        ScreenShotUtil.addScreenShot();
    }

    public void checkTomorrowForecast(){
        Assert.assertTrue(isElementPresent(By.xpath("(//*[@class='UIATable']/*[@class='UIAView' and ./*[@class='UIAStaticText'] and ./*[@class='UIAImage'] and ./*[@class='UIAView']])[17]"),10));
        ScreenShotUtil.addScreenShot();
    }

    public static boolean isElementPresent(By by, int timeout) {
        // WebDriverWait wait = new WebDriverWait(driver, timeout);
        try {
            new WebDriverWait(DriverContext.driver, timeout).until(ExpectedConditions.presenceOfElementLocated(by));
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public static void clickElement(String xPath) {
        WebDriver driver = DriverContext.getDriver();
        driver.findElements(By.xpath(xPath)).stream().filter(WebElement::isDisplayed).findFirst().get().click();
    }



}
