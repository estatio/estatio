package org.estatio.module.capex.app.invoice;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.party.dom.Party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "incomingInvoice.IncomingInvoiceTemplateViewModel")
@NoArgsConstructor
@AllArgsConstructor
public class IncomingInvoiceTemplateViewModel {

    public IncomingInvoiceTemplateViewModel(final IncomingInvoice incomingInvoice) {
        this.supplier = incomingInvoice.getSeller();
        this.type = incomingInvoice.getType();
        this.netAmount = incomingInvoice.getNetAmount();
    }

    public String title() {
        StringBuilder buf = new StringBuilder();
        buf.append(getSupplier().getName()).append(": ");

        if (getType() != null) {
            buf.append(getType());
        }

        if (getType() != null && getProperty() != null) {
            buf.append("/");
        }

        if (getProperty() != null) {
            buf.append(getProperty().getName());
        }

        buf.append(", ").append(getNetAmount().toString());

        return buf.toString();
    }

    @Getter @Setter
    private Party supplier;

    @Getter @Setter
    private IncomingInvoiceType type;

    @Getter @Setter
    private Property property;

    @Getter @Setter
    private BigDecimal netAmount;


}
