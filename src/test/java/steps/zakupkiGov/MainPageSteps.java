package steps.zakupkiGov;

import io.cucumber.java.en.Given;
import io.cucumber.java.ru.Дано;
import model.pages.zakupkiGov.ZGMainPage;
import model.pages.zakupkiGov.navigation.ZGNavId;

public class MainPageSteps {

    private static ZGMainPage mainPage = new ZGMainPage();

    @Given("Open zakupki main page")
    public void openZakupkiMainPage() {
        mainPage.openPage();
        if(mainPage.getRegionChooser().isOpened()) {
            mainPage.getRegionChooser().save();
//            mainPage.getDriver().findElement(By.xpath("//div[@id='modal-region']"));
        }
        mainPage.navigateToWithXpath(ZGNavId.ALL_SECTIONS);
    }

    @Given("Do nothing")
    public void doNothing() {
        System.out.println("Hello there !");
    }

    @Дано("Открыть главную страницу гос закупок")
    public void открытьГлавнуюСтраницуГосЗакупок() {
        mainPage.openPage();
        if(mainPage.getRegionChooser().isOpened()) {
            mainPage.getRegionChooser().save();
//            mainPage.getDriver().findElement(By.xpath("//div[@id='modal-region']"));
        }
        mainPage.navigateToWithXpath(ZGNavId.ALL_SECTIONS);
    }
}
