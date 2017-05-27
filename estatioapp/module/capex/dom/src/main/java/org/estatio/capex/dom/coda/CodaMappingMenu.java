package org.estatio.capex.dom.coda;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;

@DomainService(nature = NatureOfService.VIEW_MENU_ONLY, objectType = "not_needed_here")
@DomainServiceLayout(named = "Invoices In")
public class CodaMappingMenu {

    public List<CodaMapping> allMappings(){
        return repository.allMappings();
    }

    @Inject CodaMappingRepository repository;

}
