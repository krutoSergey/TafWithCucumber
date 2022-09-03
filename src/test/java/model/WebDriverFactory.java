package model;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.config.TestConfigFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebDriverFactory {
    private static TestConfigFactory config = TestConfigFactory.getInstance();
    private static final ThreadLocal<WebDriver> driver =
            ThreadLocal.withInitial(() -> setup());

    public static WebDriver getWebDriver(){
        return driver.get();
    }

    private static WebDriver setup() {
        switch (config.getWebConfig().getBrowser()){
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver();
            case CHROME:
            default:
                WebDriverManager.chromedriver().setup();
                return getChromeDriver();
        }
    }

    public enum Browser{
        CHROME, FIREFOX
    }

    private static ChromeDriver getChromeDriver(){
        String driverPath = config.getWebConfig().getWebDriverPath();

        Boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        if(isWindows) {
            driverPath += "chromedriver.exe";
        }
        else {
            driverPath += "chromedriver";
        }

//        System.setProperty("webdriver.chrome.driver", Resources.getResource(chromeBinaryName).getPath());
        System.setProperty("webdriver.chrome.driver", driverPath);
        return new ChromeDriver();
    }
}
