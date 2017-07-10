package org.estatio.capex.dom.payment;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.party.Party;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "PaymentLine"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.payment.PaymentLine "
                        + "WHERE invoice == :invoice "),
        @Query(
                name = "findByInvoiceAndBatchApprovalState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.payment.PaymentLine "
                        + "WHERE invoice == :invoice "
                        + "   && batch.approvalState == :approvalState ")
})
@Uniques({
        @Unique(
                name = "PaymentLine_batch_sequence", members = {"batch", "sequence"}
        )
})
@DomainObject(
        objectType = "payment.PaymentLine"
)
public class PaymentLine extends UdoDomainObject2<PaymentLine> {

    public PaymentLine() {
        super("batch, sequence, invoice");
    }

    public PaymentLine(
            final PaymentBatch batch,
            final int sequence,
            final IncomingInvoice invoice,
            final BigDecimal transferAmount,
            final String remittanceInformation){
        this();
        this.batch = batch;
        this.sequence = sequence;
        this.invoice = invoice;
        this.creditorBankAccount = invoice.getBankAccount();
        this.remittanceInformation = remittanceInformation;
        this.amount = transferAmount;
    }

    public String title() {
        return String.format("%s: %s to %s",
                getBatch().getDebtorBankAccount().getIban(),
                new DecimalFormat("0.00").format(getAmount()),
                getCreditorBankAccount().getIban());
    }

    @Column(allowsNull = "false", name = "batchId")
    @Getter @Setter
    private PaymentBatch batch;

    /**
     * Document > PmtInf > CdtTrfTxInf > PmtId > EndToEndId
     *
     * appends to the PaymentBatch's PmtInfId somehow
     */
    @Getter @Setter
    private int sequence;



    /**
     * Document > PmtInf > CdtTrfTxInf > CdtrAcct > Id > IBAN
     * Document > PmtInf > CdtTrfTxInf > CdtrAgt > FinInstnId > BIC
     */
    @Column(allowsNull = "false", name = "creditorBankAccountId")
    @Getter @Setter
    private BankAccount creditorBankAccount;

    /**
     * Document > PmtInf > CdtTrfTxInf > Amt > InstdAmt
     */
    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal amount;


    /**
     * Document > PmtInf > CdtTrfTxInf > Cdtr > Nm
     * Document > PmtInf > CdtTrfTxInf > Cdtr > PstlAdr > Ctry
     */
    public Party getCreditor() {
        return getCreditorBankAccount().getOwner();
    }

    /**
     * Document > PmtInf > CdtTrfTxInf > RmtInf > Ustrd
     *
     * some combination of:
     * invoice number, invoice date, invoice total amount, invoice payment amount, and invoice discount amount.
     */
    @Column(allowsNull = "true", length = 255)
    @Getter @Setter
    private String remittanceInformation;

    /**
     * Used to default the {@link #getAmount()} (though this can be adjusted by the user if they wish).
     */
    @Column(allowsNull = "false", name = "invoiceId")
    @Getter @Setter
    private IncomingInvoice invoice;


    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return invoice.getApplicationTenancy();
    }

}
