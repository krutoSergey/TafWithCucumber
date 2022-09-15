package model.pages.zakupkiGov;

import model.pages.zakupkiGov.navigation.ZGNavId;

public class ZGMainPage extends AbstractZGPage {

    public ZGMainPage() {
        super(ZGNavId.MAIN_PAGE);
    }

    public void openPage() {
        goToHomePage();
    }
}
