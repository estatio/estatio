package org.estatio.capex.dom.coda;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.capex.dom.coda.contributions.CodaMappingManager;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY, objectType = "org.estatio.capex.dom.coda.CodaMappingMenu")
@DomainServiceLayout(named = "Invoices In")
public class CodaMappingMenu {

    public CodaMappingManager AllCodaMappings() {
        return new CodaMappingManager();
    }

}
