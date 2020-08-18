package org.estatio.module.coda.dom.codalink.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.coda.dom.codalink.CodaDocLink;
import org.estatio.module.coda.dom.codalink.CodaDocLinkRepository;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;

@Mixin(method = "coll")
public class IncomingInvoice_codaDocLinks {

    private final IncomingInvoice incomingInvoice;

    public IncomingInvoice_codaDocLinks(IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public List<CodaDocLink> coll() {

        return repository.findByInvoice(incomingInvoice);
    }

    @Inject CodaDocLinkRepository repository;

}
