package JUnitTests;

import contextMy.TAContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import utils.ScreenshotExtension;
import model.WebDriverFactory;

@ExtendWith(ScreenshotExtension.class)
public class BaseTest {
    protected WebDriver driver;

    protected BaseTest() {
        driver = WebDriverFactory.getWebDriver();
    }

    protected TAContext getContext() {
        return TAContext.getInstance();
    }

//
//    @BeforeEach
//    public void createDriver(){
//        driver.set(WebDriverFactory.getWebDriver());
//    }

    //Closed by ScreenshotExtension class methods
    @AfterEach
    public void disposeDriver(){
        if(driver != null){
            driver.quit();
        }
    }
}
