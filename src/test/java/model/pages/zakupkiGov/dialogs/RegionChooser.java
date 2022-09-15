package model.pages.zakupkiGov.dialogs;

import model.elements.Button;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RegionChooser extends AbstractDialog {
    @FindBy(id = "modal-region")
    private WebElement dialog;

    @FindBy(xpath = "(//div[@id='modal-region']//button)[1]")
    private Button cancel;

    @FindBy(xpath = "(//div[@id='modal-region']//button)[2]")
    private Button save;

    @FindBy(id = "locationSelect")
    private WebElement regionDropDown;

    public RegionChooser() {
        super();
    }

    public boolean isOpened() {
        return dialog.isDisplayed();
    }

    public void save() {
        save.click();
    }

    public void cancel() {
        cancel.click();
    }
}
