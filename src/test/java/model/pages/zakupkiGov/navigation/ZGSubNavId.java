package model.pages.zakupkiGov.navigation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import model.pages.SubNavId;

@Getter
@AllArgsConstructor
public enum ZGSubNavId implements SubNavId {

    KONTACTS_REESTR_KONTRACTS("//a[contains(@class, '_contract') and @data-law='FZ44']"),
    KONTACTS_REESTR_TREATY("//a[contains(@class, '_contract') and @data-law='FZ223']");


    private String subNavItemId;
}
