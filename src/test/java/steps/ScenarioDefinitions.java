package steps;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import model.WebDriverFactory;
import org.openqa.selenium.WebDriver;

import java.util.Collection;
import java.util.HashMap;

public class ScenarioDefinitions {
    final private static HashMap<Integer, Collection<String>> scenarios = new HashMap<Integer, Collection<String>>();
    final private static String TEST_IDENTIFIER_PREFIX = "@ENAC-";

    @Before
    public void beforeHook(Scenario scenario) {
        Collection<String> testTags = scenario.getSourceTagNames(); // can be null
        Thread currentThread = Thread.currentThread();
        int threadID = currentThread.hashCode();
        synchronized(ScenarioDefinitions.class) {
            scenarios.put(threadID, testTags);
        }
    }

    public Collection<String> getScenarioTags(){
        Thread currentThread = Thread.currentThread();
        int threadID = currentThread.hashCode();
        Collection<String> tag;
        synchronized(ScenarioDefinitions.class) {
            tag = scenarios.get(threadID);
        }
        return tag;
    }

    public String getTestId() {
        String testId = null;
        Collection<String> tagList = getScenarioTags();
        for (String s : tagList)
            if(s.startsWith(TEST_IDENTIFIER_PREFIX)) {
                testId = s.substring(1);
                break;
            }
        return testId;
    }

    @After
    public void afterTest(){
        WebDriver driver = WebDriverFactory.getWebDriver();
        if(driver != null){
            driver.quit();
        }
    }

    @AfterStep("@SuspendRightAfterUnsuspend")
    public void afterStep() {

    }
}

