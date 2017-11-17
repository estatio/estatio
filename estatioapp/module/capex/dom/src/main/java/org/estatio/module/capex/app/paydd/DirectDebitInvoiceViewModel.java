package org.estatio.module.capex.app.paydd;

import java.math.BigDecimal;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.util.TitleBuffer;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;

import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DomainObject(
        // WORKAROUND: using fqcn as objectType because Isis' invalidation of cache in prototyping mode causing NPEs in some situations
        objectType = "org.estatio.capex.dom.paydd.manager.DirectDebitInvoiceViewModel"
)
@XmlRootElement(name = "directDebitInvoiceViewModel")
@XmlType(
        propOrder = {
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@Getter @Setter
@NoArgsConstructor
public class DirectDebitInvoiceViewModel implements Comparable<DirectDebitInvoiceViewModel> {


    DirectDebitInvoiceViewModel(final IncomingInvoice incomingInvoice) {
        dueDate = incomingInvoice.getDueDate();
        seller = incomingInvoice.getSeller();
        sellerBankAccount = incomingInvoice.getBankAccount();
        property = incomingInvoice.getProperty();
        grossAmount = incomingInvoice.getGrossAmount();
        number = incomingInvoice.getNumber();
        this.incomingInvoice = incomingInvoice;
    }

    public String title() {
        return new TitleBuffer()
                .append(dueDate.toString("dd-MM-yyyy"))
                .append(",", getSeller().getName())
                .append(" :", getGrossAmount())
                .toString();
    }


    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate dueDate;

    private Party seller;

    private BankAccount sellerBankAccount;

    private Property property;

    private BigDecimal grossAmount;

    private String number;

    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate invoiceDate;

    private IncomingInvoice incomingInvoice;

    @Override
    public int compareTo(final DirectDebitInvoiceViewModel o) {
        return Comparator.comparing(DirectDebitInvoiceViewModel::getDueDate).compare(this, o);
    }
}
