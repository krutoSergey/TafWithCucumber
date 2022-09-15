package model.pages.zakupkiGov.dialogs;

import lombok.extern.log4j.Log4j2;
import model.WebDriverFactory;
import model.elements.ElementsDecorator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.JSExecutor;
import utils.ScreenshotExtension;

import java.time.Duration;

@Log4j2
@ExtendWith(ScreenshotExtension.class)
public abstract class AbstractDialog {
    private static final Duration DEFAULT_TIMEOUT_SECONDS = Duration.ofSeconds(10);
    protected WebDriverWait wait;
    protected JSExecutor js;
    protected WebDriver driver;

    public AbstractDialog(){
        this.driver = WebDriverFactory.getWebDriver();
        this.wait = new WebDriverWait(getDriver(), DEFAULT_TIMEOUT_SECONDS);
        this.js = new JSExecutor(getDriver());

        ElementLocatorFactory factory = new DefaultElementLocatorFactory(getDriver());
        ElementsDecorator decorator = new ElementsDecorator(factory, getDriver());
        PageFactory.initElements(decorator, this);
    }

    public WebDriver getDriver(){
        return driver;
    }
}
