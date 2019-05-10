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

    LineData(
            final CodaDocLine codaDocLine) {
        this(Optional.ofNullable(codaDocLine.getIncomingInvoiceItem()),
             Optional.ofNullable(codaDocLine.getExtRefProject()),
             Optional.ofNullable(codaDocLine.getExtRefWorkTypeCharge()));
    }

    LineData(
            final Optional<IncomingInvoiceItem> invoiceItemIfAny,
            final Optional<Project> projectIfAny,
            final Optional<Charge> chargeIfAny) {

        this.invoiceItemIfAny = invoiceItemIfAny;
        this.projectIfAny = projectIfAny;
        this.chargeIfAny = chargeIfAny;
    }
}
