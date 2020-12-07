package org.estatio.module.coda.contributions.codadocument;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.coda.dom.codadocument.CodaDocumentLine;
import org.estatio.module.coda.dom.codadocument.CodaDocumentLinkRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@Mixin(method="coll")
public class CodaDocumentLine_invoice {

    private final CodaDocumentLine codaDocumentLine;
    public CodaDocumentLine_invoice(final CodaDocumentLine codaDocumentLine) {
        this.codaDocumentLine = codaDocumentLine;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @Property
    public List<InvoiceForLease> coll() {
        return codaDocumentLinkRepository.findInvoiceLinkByDocumentLine(codaDocumentLine)
                .stream()
                .map(l->l.getInvoice())
                .collect(Collectors.toList());
    }


    @Inject CodaDocumentLinkRepository codaDocumentLinkRepository;

}
