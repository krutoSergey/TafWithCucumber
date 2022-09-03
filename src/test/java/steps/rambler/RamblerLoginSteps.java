package steps.rambler;

import enums.Users;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import model.page.rambler.LoginPage;
import model.page.rambler.SearchPage;
import org.junit.jupiter.api.Assertions;
import steps.BaseSteps;

public class RamblerLoginSteps extends BaseSteps {

    private static LoginPage loginPage = new LoginPage();
    private static SearchPage searchPage = new SearchPage();

    @Step
    @Given("Open rambler login page")
    public void open_rambler_login_page() {
        loginPage.startLogin();
    }

    @Step
    @Then("Check that rambler login page is opened")
    public void check_that_rambler_login_page_is_opened() {
        Assertions.assertTrue(loginPage.isOpened());
    }

    @Step
    @When("Login to rambler with user {}")
    public void loginToRamblerWithUser(Users user) {
        loginPage.loginAs(user);
    }

    @Step
    @Then("Check that user successfully logged in")
    public void checkThatUserSuccessfullyLoggedIn() {
        String login = getContext().getUserContext().getLogin();
        searchPage.isLoggedInAs(login);
    }
}
