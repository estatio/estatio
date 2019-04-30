package org.estatio.module.coda.dom.doc;

import java.util.Map;

import org.incode.module.document.dom.impl.paperclips.Paperclip;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;

import lombok.Getter;

public class Memento {

    @Getter
    private final CodaDocHead codaDocHeadIfAny;
    @Getter
    private final IncomingInvoice incomingInvoiceIfAny;
    @Getter
    private final Map<Integer, LineData> lineDataByLineNumberIfAny;
    @Getter
    private final String documentNameIfAny;
    @Getter
    private final Paperclip paperclipIfAny;

    public Memento(
            final CodaDocHead codaDocHeadIfAny,
            final DerivedObjectLookup derivedObjectLookup) {

        this.codaDocHeadIfAny = codaDocHeadIfAny;

        if(this.codaDocHeadIfAny == null) {
            this.incomingInvoiceIfAny = null;
            this.lineDataByLineNumberIfAny = null;
            this.documentNameIfAny = null;
            this.paperclipIfAny = null;
        } else {
            this.incomingInvoiceIfAny = codaDocHeadIfAny.getIncomingInvoice();
            this.lineDataByLineNumberIfAny = codaDocHeadIfAny.getLineDataByLineNumber();
            this.documentNameIfAny = derivedObjectLookup.documentNameIfAnyFrom(codaDocHeadIfAny);
            this.paperclipIfAny = derivedObjectLookup.paperclipIfAnyFrom(codaDocHeadIfAny);
        }
    }

}
