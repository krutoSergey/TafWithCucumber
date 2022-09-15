package model.pages;

import contextMy.TAContext;
import io.cucumber.datatable.DataTable;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import model.WebDriverFactory;
import model.elements.ElementsDecorator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.JSExecutor;
import utils.ScreenshotExtension;

import java.io.File;
import java.text.DateFormatSymbols;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Log4j2
@ExtendWith(ScreenshotExtension.class)
public abstract class AbstractPage {
    private static final Duration DEFAULT_TIMEOUT_SECONDS = Duration.ofSeconds(10);
    protected WebDriverWait wait;
    protected JSExecutor js;
    protected WebDriver driver;

    private static final int ELEMENT_WAIT_DEFAULT = 30; // seconds
    public static final String DATE_PATTERN = "MM/dd/yyyy";
    public static final String LABEL_FOR_SELECTOR_EXACT = "//label[@for='%s']";
    public static final String LABEL_FOR_SELECTOR_FUZZY = "//label[contains(@for, '%s')]";
    public static final String LABEL_BY_SIBLING_INPUT_VALUE_EXACT = "//input[@value='%s']/../label";

    public AbstractPage(){
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

//    protected M2MCustomerContext getCustomerContext() {
//        return getContext().getCustomerContext();
//    }

//    protected M2MCustomerPortalUserContext getCustomerPortalUserContext() {
//        return getContext().getCustomerPortalUserContext();
//    }


    public void navigateToPage() {
        performNavigation();
    }

    protected abstract void performNavigation();

    public abstract void waitForAjax();

    public void waitForUrl(String url, long timeOutInSeconds) {
        try {
            new WebDriverWait(getDriver(), Duration.ofSeconds(timeOutInSeconds)).until(ExpectedConditions.urlContains(url));
        } catch (final TimeoutException e) {
            log.debug("Timed out while waiting for Url!");
            log.debug("Url that was waited for: " + url + ", actual Url: " + getDriver().getCurrentUrl());
        }
    }

    abstract protected void goToHomePage();

//    protected String getSupportPortalHomePageUrl() {
//        return M2MConfiguration.getProperty("supportportal.url");
//    }

    public void assertCurrentUrl(String url) {
        waitForUrl(url, 15);

        String currentUrl = getDriver().getCurrentUrl();
        if (currentUrl.contains("?")) {
            currentUrl = currentUrl.split("\\?")[0]; // remove possible url parameters
        }
        if (url.endsWith("/*")) {
            url = url.substring(0, url.lastIndexOf('/'));
            currentUrl = currentUrl.substring(0, currentUrl.lastIndexOf('/'));
        }
        final String errorMessage = "Url does not end with " + url + ", the full Url is: " + currentUrl;
        Assert.assertTrue(errorMessage, currentUrl.endsWith(url));
    }

    public void assertNewTabHasOpened(final String url) {
        getDriver().switchTo().window(new ArrayList<String>(getDriver().getWindowHandles()).get(1));
        assertCurrentUrl(url);
        getDriver().close();
    }

    public ExpectedCondition<WebElement> visibilityOfElementLocated(final By locator) {
        return visibilityOfElementLocated(findElement(locator, false));
    }

    public ExpectedCondition<WebElement> visibilityOfElementLocated(WebElement element) {
        return driver -> {
            try {
                if (element != null && !"hidden".equals(element.getCssValue("visibility")) && (element.isDisplayed() || "0".matches(element.getCssValue("opacity") + ".*?") || element.getSize().equals(new Dimension(0, 0)))) {
                    return element;
                } else {
                    return null;
                }
            } catch (final Exception e) {
                return null;
            }
        };
    }

    public boolean isPageInEnglish() {
        return readAttributeFromElementByTagName("html", "lang").equalsIgnoreCase("en");
    }

    public boolean elementExists(String id, boolean waitBefore) {
        return elementExists(By.id(id), waitBefore);
    }

    public boolean elementIsEnabled(String id, boolean waitBefore) {
        return getDriver().findElement(By.id(id)).isEnabled();
    }

    public boolean elementExists(By by, boolean waitBefore) {
        if (waitBefore) {
            waitForAjax();
            waitForElement(by, ELEMENT_WAIT_DEFAULT);
        }
        try {
            return getDriver().findElements(by).size() > 0;
        } catch (final Exception e) {
            log.error(e);
        }
        return false;
    }

    public WebElement findElement(By by, boolean throwException) throws NoSuchElementException {
        List<WebElement> webElement = null;
        boolean elementFound = false;
        int retryCount = 0;
        do {
            webElement = getDriver().findElements(by);
            if (webElement.size() > 0) {
                elementFound = true;
            } else {
                log.warn("Element by " + by + " not found. Retrying...");
                try {
                    Thread.sleep(300);
                } catch (final InterruptedException e1) {
                    log.error(e1);
                }
            }
        } while (retryCount++ < 3 && webElement.size() == 0);

        if (!elementFound && throwException) {
            throw new NoSuchElementException("Element by " + by + " not found.");
        }
        if (!throwException && !elementFound) {
            return null;
        }
        return webElement.get(0);
    }

    public WebElement findElement(By by) throws NoSuchElementException {
        return findElement(by, true);
    }

    protected List<WebElement> findElements(By by) {
        List<WebElement> webElementList;
        int retryCount = 0;
        do {
            webElementList = getDriver().findElements(by);
            if (webElementList.isEmpty()) {
                log.warn("Element list by " + by + " not found. Retrying...");
                try {
                    Thread.sleep(300);
                } catch (final InterruptedException e) {
                    log.error(e);
                }
            }
        } while (retryCount++ < 3 && webElementList.isEmpty());
        return webElementList;
    }

    public void waitForElement(By expression) {
        try {
            waitForAjax();
            new WebDriverWait(getDriver(), Duration.ofSeconds(ELEMENT_WAIT_DEFAULT)).until(visibilityOfElementLocated(expression));
        } catch (final TimeoutException e) {
            log.info("TimeoutException while waiting for element by expression: " + expression);
        }
    }

    public void waitForElement(By expression, int seconds) {
        try {
            waitForAjax();
            new WebDriverWait(getDriver(), Duration.ofSeconds(seconds)).until(visibilityOfElementLocated(expression));
        } catch (final TimeoutException e) {
            log.info("TimeoutException while waiting for element by expression: " + expression);
        }
    }

    public void waitForElement(WebElement webElement, int seconds) {
        try {
            waitForAjax();
            new WebDriverWait(getDriver(), Duration.ofSeconds(seconds)).until(visibilityOfElementLocated(webElement));
        } catch (final TimeoutException e) {
            log.info("TimeoutException while waiting for element by expression: " + webElement.toString());
        }
    }

    public void writeTextToInputField(String inputFieldId, CharSequence keys, boolean exactId, boolean ajaxElement) {
        writeTextToInputField(exactId ? By.id(inputFieldId) : By.xpath("//input[contains(@id, '" + inputFieldId + "')]"), keys, ajaxElement);
    }

    public void writeDateToInputField(String inputFieldId, LocalDate date) {
        writeDateToInputField(inputFieldId, false, date);
    }

    public void writeDateToInputField(String inputFieldId, boolean ajaxElement, LocalDate date) {

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        final String dateString = date.format(formatter);

        writeTextToInputField(By.id(inputFieldId), dateString, ajaxElement);
    }

    public void writeTextToInputFieldIfNotNull(String inputFieldId, CharSequence keys) {
        if (StringUtils.isNotEmpty(keys)) {
            writeTextToInputField(By.id(inputFieldId), keys, false);
        }
    }

    public void writeTextToInputField(String inputFieldId, CharSequence keys) {
        writeTextToInputField(By.id(inputFieldId), keys, false);
    }

    public void writeTextToInputField(String inputFieldId, CharSequence keys, int domIndex, boolean ajaxElement) {
        writeTextToInputField(By.xpath("(//input[contains(@id, '" + inputFieldId + "')])[" + domIndex + "]"), keys, ajaxElement);
    }

    public void writeTextToInputFieldWithTitle(String titleValue, CharSequence keys) {
        writeTextToInputField(By.xpath("//input[contains(@title, '" + titleValue + "')]"), keys, false);
    }

    public void writeTextToInputField(By by, CharSequence keys, boolean ajaxElement) {
        waitForElement(by, ELEMENT_WAIT_DEFAULT);
        writeTextToElement(by, ajaxElement, keys);
    }

    public void writeTextToTextArea(String textAreaId, CharSequence keys, boolean exactId) {
        writeTextToElement(exactId ? By.id(textAreaId) : By.xpath("//textarea[contains(@id, '" + textAreaId + "')]"), false, keys);
    }

    public void writeTextToTextAreaByName(String textAreaName, CharSequence keys, boolean exactId) {
        writeTextToElement(exactId ? By.xpath("//textarea[@name='" + textAreaName + "']") : By.xpath("//textarea[contains(@name, '" + textAreaName + "')]"), false, keys);
    }

    public void writeTextToElement(String id, CharSequence... keys) {
        writeTextToElement(By.id(id), false, keys);
    }

    /**
     * @param ajaxElement This parameter indicates that the element has some Ajax call or some Javascript associated to it. This is the case when we have date
     *          input fields or when there are some onclick listeners. If this parameter is true, then we have to do an additional wait after clearing the element
     *          or before sending keys to it to make sure that it receives the keys
     */
    public void writeTextToElement(By by, boolean ajaxElement, CharSequence... keys) {
        WebElement element = findElement(by);

        element.clear();

        // When we have a date input field, there is a javascript code running upon clearing the element. Therefore,
        // after clearing the element we need to wait for ajax before we continue
        if (ajaxElement) {
            waitForAjax();
        }

        int retryCount = 0;
        boolean sendKeysSuccessful;
        do {
            try {
                // If the text could not be cleared for some reason, then select the whole text using CTRL+A and override it
                if (element.getAttribute("value") != null && !element.getAttribute("value").isEmpty()) {
                    element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                }
                element.sendKeys(keys);
                sendKeysSuccessful = true;
            } catch (final StaleElementReferenceException e) {
                sendKeysSuccessful = false;
                log.warn("Stale element " + element.toString() + " recognized. Retry to send keys to element...");
                element = findElement(by);
                try {
                    Thread.sleep(300);
                } catch (final InterruptedException e1) {
                    log.error(e1);
                }
            }
        } while (retryCount++ < 10 && !sendKeysSuccessful);

        if (retryCount > 1) {
            log.info("We had to retry " + (retryCount - 1) + " times until we could successfully write text to element " + element.toString());
        }

        Assert.assertTrue("Could not write text " + Arrays.toString(keys) + " to element " + element.toString(), sendKeysSuccessful);

        // When we have a date input field, we need to remove the focus from the element so that any focus listeners can
        // run before we can continue interacting with other elements. Therefore, we are clicking on TAB to remove the focus
        // from the input data field.
        if (ajaxElement) {
            element.sendKeys(Keys.TAB);
        }
    }

    public void clickOnButtonWithId(String buttonId) {
        clickOnButtonWithId(buttonId, true);
    }

    public void clickOnButtonWithId(String buttonId, boolean exactId) {
        clickOnElementBy(exactId ? By.id(buttonId) : By.xpath("//button[contains(@id, '" + buttonId + "')]"));
    }

    public void clickOnButtonWithDataId(String buttonDataId) {

        clickOnElementBy(By.xpath("//button[@data-id='" + buttonDataId + "']"));
    }

    public void clickOnButtonWithDataTarget(String buttonDataTarget) {
        clickOnElementBy(By.xpath("//button[@data-target='" + buttonDataTarget + "']"));
    }

    public void clickOnButtonWithClass(String buttonClass) {
        clickOnButtonWithClass(buttonClass, false);
    }

    public void clickOnButtonWithClass(String buttonClass, boolean exactValue) {
        clickOnElementBy(exactValue ? By.xpath("//button[@class='" + buttonClass + "']") : By.xpath("//button[contains(@class, '" + buttonClass + "')]"));
    }

    public WebElement getButtonWithDataId(String buttonDataId, boolean exactId) {
        final By by = exactId ? By.xpath("//button[@data-id='" + buttonDataId + "']") : By.xpath("//button[contains(@data-id, '" + buttonDataId + "')]");

        return findElement(by);
    }

    public void clickOnButtonWithText(String buttonText) {
        clickOnElementBy(By.xpath("//button[contains(text(), '" + buttonText + "')]"));
    }

    public void clickOnButtonAsInputElementWithValue(String value) {
        clickOnElementBy(By.xpath("//input[@value='" + value + "']"));
    }

    public void clickOnButtonAsInputElementWithId(String buttonId, boolean exactId) {
        clickOnElementBy(exactId ? By.id(buttonId) : By.xpath("//input[contains(@id,'" + buttonId + "')]"));
    }

    public void clickOnButtonWithAriaLabel(String ariaLabel, boolean exactValue) {
        clickOnElementBy(exactValue ? By.xpath("//button[@aria-label='" + ariaLabel + "']") : By.xpath("//button[contains(@aria-label,'" + ariaLabel + "')]"));
    }

    public void clickOnSpanWithText(String spanText) {
        clickOnElementBy(By.xpath("//span[contains(text(), '" + spanText + "')]"));
    }

    public void clickOnSpanWithTextByIndex(String spanText, int elementIndex) {
        elementIndex = elementIndex + 1;
        clickOnElementBy(By.xpath("(//span[contains(text(), '" + spanText + "')])[" + elementIndex + "]"));
    }

    public void clickOnLastSpanWithText(String spanText) {
        List<WebElement> elements =  findElements(By.xpath("(//span[contains(text(), '" + spanText + "')])"));
        clickOnElementBy(By.xpath("(//span[contains(text(), '" + spanText + "')])[" + elements.size() + "]"));
    }

    public void clickOnLinkWithText(String buttonText) {
        clickOnLinkWithText(buttonText, true);
    }

    public void clickOnLinkWithText(String buttonText, boolean exactValue) {
        clickOnElementBy(exactValue ? By.xpath("//a[text()='" + buttonText + "']") : By.xpath("//a[contains(text(), '" + buttonText + "')]"));
    }

    public void clickOnLinkWithText(String buttonText, int elementIndex, boolean exactValue) {
        clickOnElementBy(exactValue ? By.xpath("//a[text()='" + buttonText + "'][" + elementIndex + "]") : By.xpath("//a[contains(text(), '" + buttonText + "')][" + elementIndex + "]"));
    }

    public void clickOnLinkWithClass(String className, boolean exactValue) {
        clickOnElementBy(exactValue ? By.xpath("//a[@class='" + className + "']") : By.xpath("//a[contains(@class,'" + className + "')]"));
    }

    public void clickOnLinkWithAriaControls(String ariaControls, boolean exactValue) {
        clickOnElementBy(exactValue ? By.xpath("//a[@aria-controls='" + ariaControls + "']") : By.xpath("//a[contains(@aria-controls,'" + ariaControls + "')]"));
    }

    public void clickOnLinkWithId(String id) {
        clickOnElementBy(By.id(id));
    }

    public void clickOnLinkWithId(String linkId, boolean exactId) {
        clickOnElementBy(exactId ? By.id(linkId) : By.xpath("//a[contains(@id, '" + linkId + "')]"));
    }

    public void clickOnLinkWithDataId(String dataId, boolean exactId) {
        clickOnElementBy(exactId ? By.xpath("//a[@data-id='" + dataId + "']") : By.xpath("//a[contains(@data-id, '" + dataId + "')]"));
    }

    public void clickOnLinkWithDataTarget(String dataTarget, boolean exactId) {
        clickOnElementBy(exactId ? By.xpath("//a[@data-target='" + dataTarget + "']") : By.xpath("//a[contains(@data-target, '" + dataTarget + "')]"));
    }

    public void clickOnLinkForDownload(By by) {

        final WebElement linkElement = findElement(by);

        // jenkins can't handle downloads with target="_blank"
        // see also: https://stackoverflow.com/questions/59676832/fail-to-download-file-with-opened-new-tab-in-headless-mode-on-linux
        final String attribute = linkElement.getAttribute("target");
        if ("_blank".equalsIgnoreCase(attribute)) {
            log.info("remove target=\"_blank\" attribute from link");
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].removeAttribute('target')", linkElement);
        }

        clickOnElementBy(by);
    }

    public void clickOnFirstEyeButtonInTableRow(String tableId) {
        clickOnFirstEyeButtonInTableRow(tableId, false);
    }

    public void clickOnFirstDeleteButtonInTableRow(String tableId, boolean exactId) {
        clickOnElementBy(exactId ? By.xpath("//table[@id='" + tableId + "']/tbody/tr/td[2]/a") : By.xpath("//table[contains(@id,'" + tableId + "')]/tbody/tr/td[2]/a"));
    }

    public void clickOnFirstEyeButtonInTableRow(String tableId, boolean exactId) {
        clickOnElementBy(exactId ? By.xpath("//table[@id='" + tableId + "']/tbody/tr/td/a") : By.xpath("//table[contains(@id,'" + tableId + "')]/tbody/tr/td/a"));
    }

    public void clickOnEyeButtonInTableRow(String tableId, int rowNumber) {
        clickOnElementBy(By.xpath("//table[@id='" + tableId + "']/tbody/tr[" + rowNumber + "]/td[1]/a"));
    }

    public void clickOnFirstEyeButtonInTableByClass(String tableClass) {
        clickOnElementBy(By.xpath("//table[contains(@class,'" + tableClass + "')]/tbody/tr/td/a"));
    }

    public void clickOnFirstPlayButtonFromTableByClass(String tableClass, String linkClass) {
        clickOnElementBy(By.xpath("//table[@class='" + tableClass + "']/tbody/tr/td/div/a[@class='" + linkClass + "']"));
    }

    public void clickOnElementBy(By by) {
        waitForElement(by, ELEMENT_WAIT_DEFAULT);
        scrollToElementAndClickIt(findElement(by));
    }

    public void clickOnElementsBy(By by) {

        final List<WebElement> foundElements = findElements(by);

        for (final WebElement element : foundElements) {
            scrollToElementAndClickIt(element);
        }
    }

    public void clickOnElement(WebElement webElement) {
        waitForElement(webElement, ELEMENT_WAIT_DEFAULT);
        scrollToElementAndClickIt(webElement);
    }

    public void clickOnCheckboxAsInput(String inputElementId) {
        clickOnElementBy(By.id(inputElementId));
    }

    public void clickOnLabelFor(String forValue) {
        clickOnLabelFor(forValue, false);
    }

    public void clickOnLabelFor(String forValue, boolean exactValue) {
        clickOnElementBy(By.xpath(exactValue ? String.format(LABEL_FOR_SELECTOR_EXACT, forValue) : String.format(LABEL_FOR_SELECTOR_FUZZY, forValue)));
    }

    public void clickOnLabelBySiblingInputValue(String value) {
        clickOnElementBy(By.xpath(String.format(LABEL_BY_SIBLING_INPUT_VALUE_EXACT, value)));
    }

    public void clickOnLabelsFor(String forValue) {
        clickOnElementsBy(By.xpath(String.format(LABEL_FOR_SELECTOR_FUZZY, forValue)));
    }

    /**
     * Note: The element should be an input element such as a checkbox, radio button or options in select elements. Otherwise this method will not work. Refer to
     * <a href="https://w3c.github.io/webdriver/webdriver-spec.html#is-element-selected"> to see which elements are supported by this method
     *
     * @param elementId id of the element which should be checked or unchecked
     * @param check if true, the element will be checked when the method returns. Otherwise the element will be unchecked when the method returns, regardless of
     *          whether the element was checked or unchecked before
     */
    public void checkOrUncheck(String elementId, boolean check) {
        checkOrUncheck(elementId, check, true);
    }

    /**
     * Note: The element should be an input element such as a checkbox, radio button or options in select elements. Otherwise this method will not work. Refer to
     * <a href="https://w3c.github.io/webdriver/webdriver-spec.html#is-element-selected"> to see which elements are supported by this method
     *
     * @param elementId id of the element which should be checked or unchecked
     * @param check if true, the element will be checked when the method returns. Otherwise the element will be unchecked when the method returns, regardless of
     *          whether the element was checked or unchecked before
     * @param exactId exactId
     */
    public void checkOrUncheck(String elementId, boolean check, boolean exactId) {
        final WebElement webElement = findElement(exactId ? By.id(elementId) : By.xpath("//input[contains(@id,'" + elementId + "')]"));
        if (check) {
            if (!webElement.isSelected()) {
                webElement.click();
            }
        } else {
            if (webElement.isSelected()) {
                webElement.click();
            }
        }
    }

    public void scrollToElementAndClickIt(WebElement element) {
        waitForAjax();

        try {
            scrollToElement(element);
            waitForElement(element, ELEMENT_WAIT_DEFAULT);
            element.click();
        } catch (final InvalidElementStateException e) {
            log.info(element.getLocation().getY() + " ----- window.scrollTo(" + element.getLocation().getX() + "," + (element.getLocation().getY() - 150) + ")");
            scrollToElement(element);
            final WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(60));
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (final WebDriverException wde) {
            if (StringUtils.containsIgnoreCase(wde.getMessage(), "Element is not clickable at point")) {
                log.warn("***** Execute JS Click on ID = " + element.getAttribute("id") + " *****");
                executeJavaScriptClick(element.getAttribute("id"));
            }
        } finally {
            try {
                waitForAjax();
            } catch (final TimeoutException e) {
                log.error("timeout waiting for Ajax");
            }
        }
    }

    public void scrollToElement(WebElement element) {
        int retryCount = 0;
        boolean scrolledSuccessfully;
        do {
            try {
                ((JavascriptExecutor) getDriver()).executeScript("window.scrollTo(" + element.getLocation().getX() + "," + (element.getLocation().getY() - 150) + ")");
                scrolledSuccessfully = true;
            } catch (final Exception e) {
                // log.debug("Could not scroll to element: " + element.toString() + ". Will retry to scroll. Retried until now " + retryCount + " times.", e);
                scrolledSuccessfully = false;
                try {
                    Thread.sleep(300);
                } catch (final InterruptedException e1) {
                    log.error(e1);
                }
            }
        } while (!scrolledSuccessfully && retryCount++ < 10);

        if (retryCount > 1) {
            if (scrolledSuccessfully) {
                log.info("We had to retry " + (retryCount - 1) + " times until we could successfully scroll to the element: " + element.toString());
            } else {
                log.info("We retried " + (retryCount - 1) + " times but still we could not successfully scroll to the element: " + element.toString());
            }
        }

        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            log.error(e);
        }
    }

    public void clickOnLinkWithTitle(String title) {
        clickOnElementBy(By.xpath("//a[@title='" + title + "']"));
    }

    public void selectOptionWithText(String value) {
        clickOnElementBy(By.xpath("//option[contains(text(), '" + value + "')]"));
    }

    public void selectText(By by, String text) {
        waitForAjax();
        waitForElement(by, ELEMENT_WAIT_DEFAULT);
        final Select select = new Select(findElement(by));

        select.selectByVisibleText(text);
    }

    public void selectText(String id, String text) {
        selectText(By.id(id), text);
    }

    public void selectTextContainingId(String id, String text) {
        selectText(By.xpath(".//select[contains(@id,'" + id + "')]"), text);
    }

    public void selectPartialTextByContainsId(String id, String text) {
        final Select select = new Select(findElement(By.xpath(".//select[contains(@id,'" + id + "')]")));
        select.getOptions().parallelStream().filter(option -> option.getText().toLowerCase().contains(text.toLowerCase())).findFirst().ifPresent(option -> select.selectByVisibleText(option.getText()));
    }

    public void selectMultipleValues(String selectIdContains, Long... values) {
        deselectAllByContainsId(selectIdContains);
        for (final Long value : values) {
            selectValueByContainsId(selectIdContains, value);
        }
    }

    public void selectMultipleValues(String selectIdContains, String... values) {
        deselectAllByContainsId(selectIdContains);
        for (final String value : values) {
            selectValueByContainsId(selectIdContains, value);
        }
    }

    public void deselectAllByContainsId(String id) {
        deselectAll(By.xpath(".//select[contains(@id,'" + id + "')]"));
    }

    public void selectValue(WebElement webElement, long value) {
        waitForAjax();
        waitForElement(webElement, ELEMENT_WAIT_DEFAULT);
        final Select select = new Select(webElement);

        select.selectByValue(Long.toString(value));
    }

    public void selectValue(WebElement webElement, String value) {
        waitForAjax();
        waitForElement(webElement, ELEMENT_WAIT_DEFAULT);
        final Select select = new Select(webElement);

        select.selectByValue(value);
    }

    public void selectValueByOptionIndex(String id, int optionIndex) {
        selectValueByOptionIndex(By.id(id), optionIndex);
    }

    public void selectValueByOptionIndexXpath(String xpath, int optionIndex) {
        selectValueByOptionIndex(By.xpath(xpath), optionIndex);
    }

    public void selectValueById(String id, long value) {
        selectValueById(id, String.valueOf(value));
    }

    public void selectValueByContainsId(String id, long value) {
        selectValueByContainsId(id, String.valueOf(value));
    }

    public void selectValueByTitle(String title, long value) {
        selectValueByTitle(title, String.valueOf(value));
    }

    public void selectValueByDataId(String dataId, long value) {
        selectValueByDataId(dataId, String.valueOf(value));
    }

    public void selectValueByLabel(String label, long value) {
        selectValueByLabel(label, String.valueOf(value));
    }

    public void selectValueById(String id, String value) {
        selectValue(By.id(id), value);
    }

    public void selectVisibleTextById(String id, String visibleText) {
        selectVisibleText(By.id(id), visibleText);
    }

    public void selectValueByContainsId(String id, String value) {
        selectValue(By.xpath(".//select[contains(@id,'" + id + "')]"), value);
    }

    public void selectValueByTitle(String title, String value) {
        selectValue(By.xpath(".//select" + "[@title = '" + title + "']"), value);
    }

    public void selectValueByDataId(String dataId, String value) {
        selectValue(By.xpath(".//select" + "[@data-id = '" + dataId + "']"), value);
    }

    public void selectValueByLabel(String label, String value) {
        selectValue(By.xpath("//label[text()='" + label + "']/ancestor::div//select[1]"), value);
    }

    /**
     * This method should be private because it is not intended to be used from outside of this class. The By object should not be used from outside. This way, we
     * have less xpath scattered around in the step classes
     *
     * @param by By
     * @param value value
     */
    private void selectValue(By by, String value) {
        waitForAjax();
        waitForElement(by, ELEMENT_WAIT_DEFAULT);
        final Select select = new Select(findElement(by));

        select.selectByValue(value);
    }

    /**
     * This method should be private because it is not intended to be used from outside of this class. The By object should not be used from outside. This way, we
     * have less xpath scattered around in the step classes
     *
     * @param by By
     * @param visibleText visibleText
     */
    private void selectVisibleText(By by, String visibleText) {
        waitForAjax();
        waitForElement(by, ELEMENT_WAIT_DEFAULT);
        final Select select = new Select(findElement(by));

        select.selectByVisibleText(visibleText);
    }

    /**
     * This method should be private because it is not intended to be used from outside of this class. The By object should not be used from outside. This way, we
     * have less xpath scattered around in the step classes
     *
     * <b>IMPORTANT NOTE:</b> This method does not look at the attribute "index" of the select element. Instead, it blindly iterates through the list of options
     * in the select element and selects the options at given index in the list.
     *
     * @param by By
     * @param optionIndex The first element has index 0
     */
    private void selectValueByOptionIndex(By by, int optionIndex) {
        waitForAjax();
        waitForElement(by, ELEMENT_WAIT_DEFAULT);
        final Select select = new Select(findElement(by));
        final List<WebElement> options = select.getOptions();
        if (options != null && !options.isEmpty()) {
            clickOnElement(options.get(optionIndex));
        }
    }

    public void deselectAll(By by) {
        waitForAjax();
        waitForElement(by, ELEMENT_WAIT_DEFAULT);
        final Select select = new Select(findElement(by));

        select.deselectAll();
    }

    public void executeJavaScriptClick(String elementId) {
        if (getDriver() instanceof JavascriptExecutor) {
            final String script = String.format("document.getElementById('%s').click();", elementId);
            ((JavascriptExecutor) getDriver()).executeScript(script);
        } else {
            throw new IllegalStateException("This driver does not support JavaScript!");
        }
    }

    public void goToPage(String url) {
        getDriver().get(url);
    }

    public void navigateTo(NavId navId, SubNavId subNavId) {
        clickOnElementBy(By.id(navId.getNavId()));
        clickOnElementBy(By.id(subNavId.getSubNavItemId()));
    }

    public void navigateTo(NavId navId) {
        clickOnElementBy(By.id(navId.getNavId()));
    }

    public void navigateToWithXpath(NavId navId, SubNavId subNavId) {
        clickOnElementBy(By.xpath(navId.getNavId()));
        clickOnElementBy(By.xpath(subNavId.getSubNavItemId()));
    }

    public void navigateToWithXpath(NavId navId) {
        clickOnElementBy(By.xpath(navId.getNavId()));
    }

//    public void setDatepickerDate(DataField inputField, int day, DatepickerMonths month, Integer year) {
//        setDatepickerDate(inputField.getFormFieldId(), day, month, year);
//    }
//
//    public void setDatepickerDate(String inputFieldId, int day, DatepickerMonths month, Integer year) {
//        final WebElement datepickerInput = findElement(By.id(inputFieldId));
//        scrollToElementAndClickIt(datepickerInput);
//
//        final By datePickerLocator = By.className("daterangepicker");
//        waitForElement(datePickerLocator, ELEMENT_WAIT_DEFAULT);
//        final WebElement datepickerContainer = findElement(datePickerLocator);
//
//        if (year != null) {
//            final Select yearSelect = new Select(datepickerContainer.findElement(By.className("yearselect")));
//
//            yearSelect.selectByVisibleText(String.valueOf(year));
//        }
//
//        if (month != null) {
//            final Select monthSelect = new Select(datepickerContainer.findElement(By.className("monthselect")));
//
//            final String monthName = getMonthNameLocalized(month.getMonthIndex());
//
//            monthSelect.selectByVisibleText(monthName);
//        }
//
//        final WebElement dayLink = datepickerContainer.findElement(By.xpath(String.format("//td[text()='%d']", day)));
//
//        dayLink.click();
//    }


    public String readFromElement(By by) {
        Assert.assertNotNull("Cannot read text from input element since no element has been specified.", by);

        waitForElement(by, ELEMENT_WAIT_DEFAULT);

        String value = null;
        final int maxRetries = 10;
        int retryCount = 0;

        // Try to get the text value using different ways. Sometimes the browsers need different ways of reading the text
        // value of an input element. Therefore, we are trying until we can get the value
        do {
            try {
                if (value == null) {
                    value = findElement(by).getText();
                    if (value == null || value.isEmpty()) {
                        value = findElement(by).getAttribute("innerText");
                        if (value == null || value.isEmpty()) {
                            value = findElement(by).getAttribute("text");
                            if (value == null || value.isEmpty()) {
                                value = findElement(by).getAttribute("value");
                            }
                        }
                    }
                }
            } catch (final StaleElementReferenceException e) {
                log.warn("Stale element " + findElement(by).toString() + " recognized. Retried " + retryCount + " times to find the element and read the value again...");
                findElement(by).getText();
                try {
                    Thread.sleep(300);
                } catch (final InterruptedException e1) {
                    log.error(e1);
                }
            }
        } while (retryCount++ < maxRetries);
        return value != null ? value.trim() : null;
    }

    public String readFromElement(String elementId) {
        return readFromElement(By.id(elementId));
    }

    public String readAttributeFromElement(String elementId, String attributeName) {
        return readAttributeFromElement(By.id(elementId), attributeName);
    }

    public String readAttributeFromElementByTagName(String elementTagName, String attributeName) {
        return readAttributeFromElement(By.tagName(elementTagName), attributeName);
    }

    public String readAttributeFromElement(By by, String attributeName) {
        return findElement(by).getAttribute(attributeName);
    }

    public String readFromInputElement(String inputElementId, boolean exactId) {
        if (exactId) {
            return readFromElement(By.id(inputElementId));
        } else {
            return readFromElement(By.xpath("//input[contains(@id, '" + inputElementId + "')]"));
        }
    }

    public String readFromTableByClassName(String tableClassName, int tableIndex, int rowIndex, int colIndex) {
        return readFromElement(By.xpath("(//table[@class='" + tableClassName + "'])[" + tableIndex + "]//tbody//tr[" + rowIndex + "]//td[" + colIndex + "]"));
    }

    public String readFromTableById(String tableId, int tableIndex, int rowIndex, int colIndex) {
        return readFromElement(By.xpath("(//table[@id='" + tableId + "'])[" + tableIndex + "]//tbody//tr[" + rowIndex + "]//td[" + colIndex + "]"));
    }

    public void refreshPage() {
        getDriver().navigate().refresh();
        waitForAjax();
    }

    public void openPanelIfClosed(String panelLinkHref) {
        if (isPanelClosed(panelLinkHref)) {
            openPanel(panelLinkHref);
        }
    }

    public boolean isPanelClosed(String panelLinkHref) {
        return readAttributeFromElement(By.xpath("//a[@href='" + panelLinkHref + "']"), "class").contains("collapsed");
    }

    public void openPanel(String panelLinkHref) {
        clickOnElementBy(By.xpath("//a[@href='" + panelLinkHref + "']"));
    }

    public String getFormattedDateForCurrentLocale(TemporalAccessor date, String pattern) {
        return DateTimeFormatter.ofPattern(pattern, isPageInEnglish() ? Locale.ENGLISH : Locale.GERMAN).format(date);
    }

    private String getMonthNameLocalized(int monthIdx) {
        final DateFormatSymbols dfs = new DateFormatSymbols(isPageInEnglish() ? Locale.ENGLISH : Locale.GERMAN);

        return dfs.getMonths()[monthIdx];
    }

    public boolean elementHasClass(WebElement element, String cssClass) {
        return element.getAttribute("class").contains(cssClass);
    }

    public DataTable createTableFromGui(WebElement table) {
        final WebElement tBody = table.findElement(By.tagName("tbody"));
        final List<WebElement> webRows = tBody.findElements(By.tagName("tr"));
        final List<List<String>> rows = new ArrayList<List<String>>();
        for (final WebElement webRow : webRows) {
            final List<WebElement> webCells = webRow.findElements(By.tagName("td"));
            final List<String> row = new ArrayList<String>();
            for (final WebElement cell : webCells) {
                row.add(cell.getText());
            }
            rows.add(row);
        }

        return DataTable.create(rows);
    }

    public List<String> getDropdownListFromGui(WebElement dropdownList) {
        final List<String> items = new ArrayList<String>();
        final List<WebElement> webElements = dropdownList.findElements(By.tagName("option"));
        webElements.forEach(item -> items.add(item.getText()));
        return items;
    }

    public List<String> getDropdownListFromGui(String dropdownList) {
        final List<String> items = new ArrayList<String>();
        final WebElement dList = findElement(By.id(dropdownList));

        final List<WebElement> webElements = dList.findElements(By.tagName("option"));
        webElements.forEach(item -> items.add(item.getText()));
        return items;
    }

    public WebDriver getDriver(){
        return driver;
    }
}
