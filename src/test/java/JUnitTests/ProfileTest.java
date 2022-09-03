package JUnitTests;

import enums.Users;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import model.page.rambler.LoginPage;

@DisplayName("Тесты на юзер профайл")
@Feature("User profile feature")
public class ProfileTest extends BaseTest{

    @Test
    @DisplayName("Успешное открытие юзер профайла")
    @Story("JIRA-3")
    public void openProfile(){
        Assertions.assertTrue(
                new LoginPage()
                        .startLogin()
                        .loginAs(Users.RAMBLER_STANDARD_USER)
                        .openProfile()
                        .isProfileOpened()
        );
    }
}
