package org.estatio.module.docflow.dom;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "docflow.DocFlowZipMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices In",
        menuOrder = "71.1"
)
public class DocFlowZipMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public DocFlowZip lookupDocFlowZip(
            final long sdId
    ) {
        final DocFlowZip docFlowZip =
                docFlowZipRepository.findBySdId(sdId);
        if(docFlowZip == null) {
            messageService.informUser(String.format(
                    "No DocFlow zip found for '%d'",
                    sdId));
        }
        return docFlowZip;
    }


    @Inject
    DocFlowZipRepository docFlowZipRepository;
    @Inject
    MessageService messageService;

}
