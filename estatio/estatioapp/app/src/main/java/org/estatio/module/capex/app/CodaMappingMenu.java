package org.estatio.module.capex.app;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.imports.CodaMappingFraManager;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY, objectType = "org.estatio.capex.dom.coda.CodaMappingMenu")
@DomainServiceLayout(
        named = "Payments",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "70.6"
)
public class CodaMappingMenu {

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "map-o")
    public CodaMappingFraManager allCodaMappingsFra() {
        return new CodaMappingFraManager();
    }

}
