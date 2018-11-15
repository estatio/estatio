package org.estatio.module.coda.dom.costcentre;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "coda.CostCentreMenu"
)
public class CostCentreMenu {

    @Inject
    CostCentreRepository repository;

    @Action(semantics = SemanticsOf.SAFE)
    public List<CostCentre> allCostCentres() {
        return repository.listAll();
    }

}
