package model.pages.rambler;

import contextMy.TAContext;
import io.github.bonigarcia.wdm.WebDriverManager;
import model.WebDriverFactory;
import model.elements.ElementsDecorator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.JSExecutor;
import utils.ScreenshotExtension;
import utils.config.TestConfigFactory;

import java.time.Duration;

@ExtendWith(ScreenshotExtension.class)
public abstract class BasePage {
    private static TestConfigFactory config = TestConfigFactory.getInstance();
    protected static final String BASE_URL = config.getWebConfig().getBaseUrl();
    private static final Duration DEFAULT_TIMEOUT_SECONDS = Duration.ofSeconds(10);

    protected WebDriverWait wait;
    protected JSExecutor js;
    protected WebDriver driver;


//    @BeforeEach
//    void setup() {
//        driver = WebDriverFactory.getWebDriver();
//    }

//    @BeforeEach
//    public void createDriver(){
//        driver.set(WebDriverFactory.getWebDriver());
//    }

    public BasePage(){
        this.driver = WebDriverFactory.getWebDriver();
        this.wait = new WebDriverWait(getDriver(), DEFAULT_TIMEOUT_SECONDS);
        this.js = new JSExecutor(getDriver());

        ElementLocatorFactory factory = new DefaultElementLocatorFactory(getDriver());
        ElementsDecorator decorator = new ElementsDecorator(factory, getDriver());
        PageFactory.initElements(decorator, this);
    }

    protected TAContext getContext() {
        return TAContext.getInstance();
    }

    protected void waitAndClick(WebElement locator){
//        new Actions(driver).click(wait.until(ExpectedConditions.elementToBeClickable(locator)));
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected WebElement waitVisibility(WebElement locator){
        return wait.until(ExpectedConditions.visibilityOf(locator));
    }

    public void switchToAnotherTab(){
        getDriver().switchTo().window(
                getDriver().getWindowHandles().stream()
                        .filter(h -> !h.equals(getDriver().getWindowHandle()))
                        .findFirst().get()
        );
    }

    public WebDriver getDriver(){
        return driver;
    }
}
