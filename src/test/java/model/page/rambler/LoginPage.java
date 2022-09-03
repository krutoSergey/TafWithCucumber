package model.page.rambler;

import enums.Users;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import model.elements.Button;
import model.elements.EditBox;
import model.elements.IFrame;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {
    @FindBy(xpath = "//*[text()='Вход']")
    private Button openLogin;
    @FindBy(xpath = "//div[@data-id-frame]/iframe")
    private IFrame loginIFrame;
    @FindBy(id = "login")
    private EditBox loginBox;
    @FindBy(id = "password")
    private EditBox passwordBox;
    @FindBy(xpath = "//*[text()='Войти']")
    private Button processLogin;

    @FindBy(xpath = "//*[@id='password']/../../div[2]")
    private WebElement loginErrorMessage;

    public LoginPage() {
        getDriver().get(BASE_URL);
    }

    public LoginPage startLogin() {
        openLogin.click();
        loginIFrame.switchTo();
        loginBox.waitVisibility();
        return this;
    }

    public LoginPage typeLogin(String email){
        loginBox.sendKeys(email);
        return this;
    }

    public LoginPage typePassword(String password){
        passwordBox.sendKeys(password);
        return this;
    }

    public SearchPage clickLogin(){
        processLogin.click();
        return new SearchPage();
    }

    public String getErrorMessage() {
        return getDriver().findElement(By.xpath("//*[@id='password']/../../div[2]")).getText();
    }

    public boolean isOpened(){
        return loginBox.waitVisibility().isDisplayed();
    }

    @Step("Логин в портал с логином {email} и паролем {password}")
    @Attachment()
    public SearchPage loginAs(String email, String password){
        typeLogin(email);
        typePassword(password);
        getContext().getUserContext().setLogin(email);
        getContext().getUserContext().setPassword(password);
        return clickLogin();
    }

    public SearchPage loginAs(Users user) {
        return loginAs(user.getUsername(), user.getPassword());
    }
}
