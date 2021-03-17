package org.estatio.module.coda.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.util.CountryUtil;
import org.estatio.module.coda.dom.doc.CodaDocLine;
import org.estatio.module.coda.dom.doc.CodaDocLineRepository;

@Mixin(method="prop")
public class IncomingInvoiceItem_analysisLine {

    private final IncomingInvoiceItem incomingInvoiceItem;
    public IncomingInvoiceItem_analysisLine(final IncomingInvoiceItem incomingInvoiceItem) {
        this.incomingInvoiceItem = incomingInvoiceItem;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @Property
    public CodaDocLine prop() {
        return queryResultsCache.execute(
                () -> codaDocLineRepository.findByIncomingInvoiceItem(incomingInvoiceItem),
                getClass(), "prop"
        );
    }

    public boolean hideProp() {
        return !CountryUtil.isItalian(incomingInvoiceItem.getInvoice());
    }

    @Inject
    CodaDocLineRepository codaDocLineRepository;
    @Inject
    QueryResultsCache queryResultsCache;
}
