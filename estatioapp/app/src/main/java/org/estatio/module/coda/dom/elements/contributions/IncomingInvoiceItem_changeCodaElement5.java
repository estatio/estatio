package org.estatio.module.coda.dom.elements.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.accountingaudit.IncomingInvoiceAccountingState;
import org.estatio.module.coda.dom.elements.CodaElement;
import org.estatio.module.coda.dom.elements.CodaElementLevel;
import org.estatio.module.coda.dom.elements.CodaElementRepository;
import org.estatio.module.coda.dom.elements.InvoiceItemCodaElementsLink;
import org.estatio.module.coda.dom.elements.InvoiceItemCodaElementsLinkRepository;

@Mixin
public class IncomingInvoiceItem_changeCodaElement5 {

    private final IncomingInvoiceItem item;

    public IncomingInvoiceItem_changeCodaElement5(IncomingInvoiceItem item) {
        this.item = item;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public IncomingInvoiceItem $$(final CodaElement codaElement) {
        final InvoiceItemCodaElementsLink existingLink = repository.findUnique(item);
        repository.upsert(item, existingLink!=null ? existingLink.getCodaElement4() : null, codaElement);
        return item;
    }

    public List<CodaElement> choices0$$(){
        return codaElementRepository.findByLevel(CodaElementLevel.LEVEL_5);
    }

    public String disable$$(){
        final IncomingInvoice invoice = (IncomingInvoice) item.getInvoice();
        if (invoice.getAccountingState()== IncomingInvoiceAccountingState.AUDITED){
            return "The invoice is audited";
        }
        return null;
    }

    @Inject InvoiceItemCodaElementsLinkRepository repository;

    @Inject CodaElementRepository codaElementRepository;

}
