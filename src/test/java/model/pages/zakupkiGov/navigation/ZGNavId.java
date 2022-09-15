package model.pages.zakupkiGov.navigation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import model.pages.NavId;

@Getter
@AllArgsConstructor
public enum ZGNavId implements NavId {

    MAIN_PAGE("//header[contains(@class, 'header-mid')]",
    null),

    ALL_SECTIONS("//a[text()='Все разделы']",
            null),

    PURCHASES("//a[contains(text(),'Закупки')]",
            null),

    KONTRACTS_TREATS("Контракты и договоры",
            new ZGSubNavId[]{
            ZGSubNavId.KONTACTS_REESTR_KONTRACTS,
            ZGSubNavId.KONTACTS_REESTR_TREATY,
            });


    private String navId;
    private ZGSubNavId[] subNavIds;

    public static ZGNavId getNavIdForSubnavId(ZGNavId id) {
        for (ZGNavId navId : ZGNavId.values()) {
            for (ZGSubNavId subNavId : navId.getSubNavIds()) {
                if (subNavId.equals(id)) {
                    return navId;
                }
            }
        }
        return null;
    }
}
