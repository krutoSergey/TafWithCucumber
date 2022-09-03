package model.pages.rambler;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProfilePage extends BaseLoggedInPage {
    @FindBy(xpath = "//h1[text()='Simple Automation']")
    private WebElement profilePageTitle;

    public boolean isProfileOpened(){
        try {
            return waitVisibility(profilePageTitle).isDisplayed();
        } catch (NoSuchElementException e){
            e.printStackTrace();
            return false;
        }
    }
}
