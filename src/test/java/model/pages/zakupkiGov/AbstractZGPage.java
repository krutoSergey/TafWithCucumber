package model.pages.zakupkiGov;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import model.pages.AbstractPage;
import model.pages.NavId;
import model.pages.SubNavId;
import model.pages.zakupkiGov.dialogs.RegionChooser;
import model.pages.zakupkiGov.navigation.ZGNavId;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.config.TestConfigFactory;

import java.time.Duration;

@Log4j2
public abstract class AbstractZGPage extends AbstractPage {
    private static TestConfigFactory config = TestConfigFactory.getInstance();
    protected static final String BASE_URL = config.getWebConfig().getBaseUrl_zakupki_gov();
    protected RegionChooser regionChooser;

    @Getter
    protected NavId navId;

    @Getter
    protected SubNavId subNavId;

    public AbstractZGPage(ZGNavId mainPage) {
        super();
        this.navId = mainPage;
        this.subNavId = null;
    }

    @Override
    protected void performNavigation() {
        if (subNavId == null) {
            navigateToWithXpath(navId);
        }
        else {
            navigateToWithXpath(navId, subNavId);
        }
    }

    @Override
    public void waitForAjax() {
        new WebDriverWait(getDriver(), Duration.ofSeconds(180)).until((ExpectedCondition<Boolean>) driver -> {

            boolean waitingIsFinishedOrNotNecessary;

            try {

                boolean jScriptReady;

                if (driver != null) {

                    jScriptReady = ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");

                    waitingIsFinishedOrNotNecessary = jScriptReady;
                } else {
                    waitingIsFinishedOrNotNecessary = true;
                }
            } catch (final UnhandledAlertException e) {
                log.warn("WaitForAjax is skipped, since there is an alert window open, nothing to worry about");
                waitingIsFinishedOrNotNecessary = true;
            } catch (final Exception e) {
                log.error(e.getMessage(), e);
                waitingIsFinishedOrNotNecessary = true;
            }

            return waitingIsFinishedOrNotNecessary;
        });
    }

    @Override
    protected void goToHomePage() {
        goToPage(BASE_URL);
    }

    public void logout() {
//        clickOnLinkWithId("formLogout:buttonLogout");
//        try {
//            Thread.sleep(2000);
//        } catch (final InterruptedException e) {
//            log.error(e);
//        }
    }

    public RegionChooser getRegionChooser() {
        if (regionChooser == null) {
            regionChooser = new RegionChooser();
        }
        return regionChooser;
    }
}
