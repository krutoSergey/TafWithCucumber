package model.pages.rambler;

import io.qameta.allure.Step;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BaseLoggedInPage extends BasePage {
    @FindBy(xpath = "//span[contains(text(), '@rambler.ru')]")
    protected WebElement loggedInEmail;
    @FindBy(xpath = "//a[text()='Мой профиль']")
    protected WebElement profileLink;

    public boolean isLoggedInAs(String email){
        try {
            return waitVisibility(loggedInEmail).getText().equals(email);
        } catch (NoSuchElementException | TimeoutException e){
            e.printStackTrace();
            return false;
        }
    }

    @Step("Открыть профиль пользователя через верхнее меню")
    public ProfilePage openProfile(){
        waitAndClick(loggedInEmail);
        waitAndClick(profileLink);
        switchToAnotherTab();

        return new ProfilePage();
    }
}
