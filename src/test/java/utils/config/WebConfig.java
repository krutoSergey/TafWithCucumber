package utils.config;

import lombok.Data;
import model.WebDriverFactory;

@Data
public class WebConfig {
    private String baseUrl;
    private WebDriverFactory.BrowserType browserType;
    private String webDriverPath;
}
