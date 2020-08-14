package org.estatio.module.coda.dom.elements.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.coda.dom.elements.CodaElement;
import org.estatio.module.coda.dom.elements.CodaElementLevel;
import org.estatio.module.coda.dom.elements.CodaElementRepository;
import org.estatio.module.coda.dom.elements.InvoiceItemCodaElementsLink;
import org.estatio.module.coda.dom.elements.InvoiceItemCodaElementsLinkRepository;

@Mixin
public class IncomingInvoiceItem_changeCodaElement4 {

    private final IncomingInvoiceItem item;

    public IncomingInvoiceItem_changeCodaElement4(IncomingInvoiceItem item) {
        this.item = item;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public IncomingInvoiceItem $$(final CodaElement codaElement) {
        final InvoiceItemCodaElementsLink existingLink = repository.findUnique(item);
        repository.upsert(item, codaElement, existingLink!=null ? existingLink.getCodaElement5() : null);
        return item;
    }

    public List<CodaElement> choices0$$(){
        return codaElementRepository.findByLevel(CodaElementLevel.LEVEL_4);
    }

    @Inject InvoiceItemCodaElementsLinkRepository repository;

    @Inject CodaElementRepository codaElementRepository;

}
