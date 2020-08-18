package org.estatio.module.coda.dom.elements;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.base.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = InvoiceItemCodaElementsLink.class
)
public class InvoiceItemCodaElementsLinkRepository extends UdoDomainRepositoryAndFactory<InvoiceItemCodaElementsLink> {

    public InvoiceItemCodaElementsLinkRepository() {
        super(InvoiceItemCodaElementsLinkRepository.class, InvoiceItemCodaElementsLink.class);
    }

    @Programmatic
    public InvoiceItemCodaElementsLink findUnique(final IncomingInvoiceItem incomingInvoiceItem){
        return uniqueMatch(
                "findUnique",
                "incomingInvoiceItem", incomingInvoiceItem);
    }

    @Programmatic
    public InvoiceItemCodaElementsLink upsert(
            final IncomingInvoiceItem incomingInvoiceItem,
            final CodaElement codaElement4,
            final CodaElement codaElement5) {
        final InvoiceItemCodaElementsLink existingLinkIfAny = findUnique(incomingInvoiceItem);
        if (existingLinkIfAny == null){
            return create(incomingInvoiceItem, codaElement4,codaElement5);
        } else {
            existingLinkIfAny.setCodaElement4(codaElement4);
            existingLinkIfAny.setCodaElement5(codaElement5);
            return existingLinkIfAny;
        }
    }

    @Programmatic
    private InvoiceItemCodaElementsLink create(
            final IncomingInvoiceItem incomingInvoiceItem,
            final CodaElement codaElement4,
            final CodaElement codaElement5) {
        final InvoiceItemCodaElementsLink link = new InvoiceItemCodaElementsLink(incomingInvoiceItem, codaElement4, codaElement5);
        repositoryService.persistAndFlush(link);
        return link;
    }

    @Inject RepositoryService repositoryService;
}
