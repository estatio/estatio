package org.estatio.module.coda.dom.doc;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.message.MessageService;

import org.estatio.module.application.app.CodaCmpCodeService;
import org.estatio.module.application.app.CodaDocCodeService;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "coda.CodaDocHeadMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices In",
        menuOrder = "65.4"
)
public class CodaDocHeadMenu {

    @Action(semantics = SemanticsOf.SAFE)
    public CodaDocHead lookupCodaDocument(
            final String cmpCode,
            final String docCode,
            final String docNum
    ) {
        final CodaDocHead docHead =
                codaDocHeadRepository.findByCmpCodeAndDocCodeAndDocNum(cmpCode, docCode, docNum);
        if(docHead == null) {
            messageService.informUser(String.format(
                    "No Coda document found for '%s | %s | %s'",
                    cmpCode, docCode, docNum));
        }
        return docHead;
    }
    public List<String> choices0LookupCodaDocument() {
        return codaCmpCodeService.listAll();
    }
    public String default0LookupCodaDocument() {
        return codaCmpCodeService.listAll().get(0);
    }
    public List<String> choices1LookupCodaDocument() {
        return codaDocCodeService.listAll();
    }
    public String default1LookupCodaDocument() {
        return codaDocCodeService.listAll().get(0);
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(hidden = Where.EVERYWHERE)
    public void retrieveCodaDoc(
            final String cmpCode,
            final String docCode,
            final int docNum) {
        // no-op, just for the interaction to be picked up by camel
        // needs to live in a service other than of type DOMAIN in order for the wrapperFactory to find the method
    }

    @Inject
    CodaDocHeadRepository codaDocHeadRepository;
    @Inject
    MessageService messageService;

    @Inject
    CodaCmpCodeService codaCmpCodeService;
    @Inject
    CodaDocCodeService codaDocCodeService;

}
