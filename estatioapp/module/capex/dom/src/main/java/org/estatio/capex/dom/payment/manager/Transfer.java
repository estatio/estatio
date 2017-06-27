package org.estatio.capex.dom.payment.manager;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.capex.dom.payment.PaymentBatch;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.party.Party;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "transfer")
@XmlType(
        propOrder = {
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
public class Transfer implements Comparable<Transfer> {

    public Transfer() {
    }

    @Getter @Setter
    private PaymentBatch paymentBatch;
    @Getter @Setter
    private IncomingInvoice invoice;
    @Getter @Setter
    private BigDecimal transferAmount;
    @Getter @Setter
    private String remittanceInformation;

    public Party getCreditor() {
        return getInvoice().getSeller();
    }

    public IncomingInvoiceType getInvoiceType() {
        return getInvoice().getType();
    }

    public String getInvoiceNumber() {
        return getInvoice().getInvoiceNumber();
    }

    public BigDecimal getInvoiceGrossAmount() {
        return getInvoice().getGrossAmount();
    }

    public BankAccount getDebtorBankAccount() {
        return getPaymentBatch() != null ? getPaymentBatch().getDebtorBankAccount() : null;
    }

    private String getDebtorBankAccountReference() {
        if (getDebtorBankAccount() == null) {
            return null;
        }
        if (getDebtorBankAccount().getOwner() == null) {
            return null;
        }
        return getDebtorBankAccount().getOwner().getReference();
    }

    private String getCreditorReference() {
        if (getCreditor() == null) {
            return null;
        }
        return getCreditor().getReference();
    }

    @Override
    public int compareTo(final Transfer other) {
        return ComparisonChain.start()
                .compare(getDebtorBankAccountReference(), other.getDebtorBankAccountReference(),
                        Ordering.natural().nullsFirst())
                .compare(getCreditorReference(), other.getCreditorReference(),
                        Ordering.natural().nullsFirst())
                .compare(getInvoiceNumber(), other.getInvoiceNumber())
                .result();
    }
}
