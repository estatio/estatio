package org.estatio.module.coda.dom.doc;

import java.util.Optional;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.charge.dom.Charge;

import lombok.Data;

@Data
public class LineData {

    private final Optional<IncomingInvoiceItem> invoiceItemIfAny;
    private final Optional<Project> projectIfAny;
    private final Optional<Charge> chargeIfAny;

    public LineData(
            final CodaDocLine codaDocLine) {
        final IncomingInvoiceItem incomingInvoiceItem = codaDocLine.getIncomingInvoiceItem();
        if(incomingInvoiceItem != null) {
            invoiceItemIfAny = Optional.of(incomingInvoiceItem);
            projectIfAny = Optional.ofNullable(codaDocLine.getExtRefProject());
            chargeIfAny = Optional.ofNullable(codaDocLine.getExtRefWorkTypeCharge());
        } else {
            invoiceItemIfAny = Optional.empty();
            projectIfAny = Optional.empty();
            chargeIfAny = Optional.empty();
        }
    }
}
