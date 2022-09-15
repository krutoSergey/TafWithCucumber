package cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
//@CucumberOptions(plugin = {"pretty", "html:target/result/index.html"},
@CucumberOptions(plugin = {"pretty", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"},
        features = "src/test/resources/features/rambler",
        glue = "steps",
        tags = "@local",
        snippets = CucumberOptions.SnippetType.CAMELCASE
)

public class RemoteCucumberRunner {
}
