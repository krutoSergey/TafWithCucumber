package JUnitTests;

import enums.Users;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import model.page.rambler.LoginPage;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты на логин")
@Feature("Login feature")
public class LoginTest extends BaseTest{

    private static LoginPage loginPage = new LoginPage();

    @Test
    @DisplayName("Успешный логин")
    @Story("JIRA-1")
    @Description("Тест на проверку успешного логина при правилном введенном логине и пароле")
    public void login(){
        Users.User user = Users.RAMBLER_STANDARD_USER.getUser();
        Assertions.assertTrue(
                loginPage
                        .startLogin()
                        .loginAs(Users.RAMBLER_STANDARD_USER)
                        .isLoggedInAs(user.getUsername())
        );
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Story("JIRA-1")
    public void loginWithBadPassword(){
        Users.User user = Users.RAMBLER_STANDARD_USER.getUser();
        Assertions.assertFalse(
                loginPage
                        .startLogin()
                        .loginAs(user.getUsername(), "WRONG_VALUE")
                        .isLoggedInAs(user.getUsername())
        );
        assertThat(loginPage.getErrorMessage().contains("Неправильная почта или пароль"));
    }

    @Test
    @DisplayName("Страница логина доступна")
    @Story("JIRA-2")
    public void openLoginPage(){
        Assertions.assertTrue(
                loginPage.startLogin().isOpened()
        );
    }
}
