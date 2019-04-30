package org.estatio.module.coda.dom.doc;

import java.util.Map;
import java.util.stream.Stream;

import org.incode.module.document.dom.impl.paperclips.Paperclip;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.orderinvoice.OrderItemInvoiceItemLink;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.charge.dom.Charge;

import lombok.Getter;

public class Existing {

    @Getter
    private final CodaDocHead codaDocHeadIfAny;
    @Getter
    private final IncomingInvoice incomingInvoiceIfAny;
    @Getter
    private final Map<Integer, IncomingInvoiceItem> itemByLineNumberIfAny;
    @Getter
    private final Map<Integer, Project> projectByLineNumberIfAny;
    @Getter
    private final Map<Integer, Charge> chargeByLineNumberIfAny;
    @Getter
    private final OrderItemInvoiceItemLink orderItemLinkIfAny;
    @Getter
    private final String documentNameIfAny;
    @Getter
    private final Paperclip paperclipIfAny;

    public Existing(
            final CodaDocHead codaDocHeadIfAny,
            final DerivedObjectLookup derivedObjectLookup) {

        this.codaDocHeadIfAny = codaDocHeadIfAny;

        if(this.codaDocHeadIfAny == null) {
            this.incomingInvoiceIfAny = null;
            this.itemByLineNumberIfAny = null;
            this.projectByLineNumberIfAny = null;
            this.chargeByLineNumberIfAny = null;
            this.orderItemLinkIfAny = null;
            this.documentNameIfAny = null;
            this.paperclipIfAny = null;
        } else {
            this.incomingInvoiceIfAny = codaDocHeadIfAny.getIncomingInvoice();
            this.itemByLineNumberIfAny = codaDocHeadIfAny.getAnalysisInvoiceItemByLineNumber();
            this.projectByLineNumberIfAny = codaDocHeadIfAny.getAnalysisProjectByLineNumber();
            this.chargeByLineNumberIfAny = codaDocHeadIfAny.getAnalysisChargeByLineNumber();

            this.orderItemLinkIfAny = derivedObjectLookup.linkIfAnyFrom(codaDocHeadIfAny);
            this.documentNameIfAny = derivedObjectLookup.documentNameIfAnyFrom(codaDocHeadIfAny);
            this.paperclipIfAny = derivedObjectLookup.paperclipIfAnyFrom(codaDocHeadIfAny);
        }
    }
}
