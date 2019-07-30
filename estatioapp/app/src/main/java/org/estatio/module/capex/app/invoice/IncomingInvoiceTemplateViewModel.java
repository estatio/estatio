package org.estatio.module.capex.app.invoice;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.module.asset.dom.Property;
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

    // todo
    public String title() {
        return null;
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
