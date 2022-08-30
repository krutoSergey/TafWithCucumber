package steps;

import enums.Users;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import model.page.LoginPage;
import model.page.SearchPage;
import org.junit.jupiter.api.Assertions;

public class RamblerLoginSteps extends BaseSteps {

    private static LoginPage loginPage = new LoginPage();
    private static SearchPage searchPage = new SearchPage();

    @Given("Open rambler login page")
    public void open_rambler_login_page() {
        loginPage.startLogin();
    }

    @Then("Check that rambler login page is opened")
    public void check_that_rambler_login_page_is_opened() {
        Assertions.assertTrue(loginPage.isOpened());
    }

    @When("Login to rambler with user {}")
    public void loginToRamblerWithUser(Users user) {
        loginPage.loginAs(user);
    }

    @Then("Check that user successfully logged in")
    public void checkThatUserSuccessfullyLoggedIn() {
        String login = getContext().getUserContext().getLogin();
        searchPage.isLoggedInAs(login);
    }
}
