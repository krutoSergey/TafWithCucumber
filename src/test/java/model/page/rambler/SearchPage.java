package model.page.rambler;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SearchPage extends BaseLoggedInPage {

    @FindBy(xpath = "//img[contains(@src, 'news')]")
    private WebElement news;

    public SearchPage scrollToNews(){
        waitVisibility(loggedInEmail);
        js.scrollTo(news);
        return this;
    }
}
