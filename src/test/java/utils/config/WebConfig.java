package utils.config;

import lombok.Data;
import model.WebDriverFactory;

@Data
public class WebConfig {
    private String baseUrl_rambler;
    private String baseUrl_zakupki_gov;
    private WebDriverFactory.BrowserType browserType;
    private String webDriverPath;
}
