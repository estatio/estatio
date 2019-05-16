package org.estatio.module.coda.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.util.CountryUtil;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocHeadRepository;

@Mixin(method="prop")
public class IncomingInvoice_codaDocHead {

    private final IncomingInvoice incomingInvoice;
    public IncomingInvoice_codaDocHead(final IncomingInvoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @Property
    public CodaDocHead prop() {
        return queryResultsCache.execute(
                () -> codaDocHeadRepository.findByIncomingInvoice(incomingInvoice),
                getClass(), "prop", incomingInvoice
        );
    }
    public boolean hideProp() {
        return !CountryUtil.isItalian(incomingInvoice);
    }

    @Inject
    CodaDocHeadRepository codaDocHeadRepository;

    @Inject
    QueryResultsCache queryResultsCache;
}
