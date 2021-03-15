package org.estatio.module.capex.dom.payment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Comparator;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.party.dom.Party;

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
                        + "FROM org.estatio.module.capex.dom.payment.PaymentLine "
                        + "WHERE invoice == :invoice "),
        @Query(
                name = "findByInvoiceAndBatchApprovalState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.payment.PaymentLine "
                        + "WHERE invoice == :invoice "
                        + "   && batch.approvalState == :approvalState "),
        @Query(
                name = "findByInvoiceAndBatchApprovalState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.payment.PaymentLine "
                        + "WHERE invoice == :invoice "
                        + "   && batch.approvalState == :approvalState "),
        @Query(
                name = "findFromRequestedExecutionDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.capex.dom.payment.PaymentLine "
                        + "WHERE batch.requestedExecutionDate >= :fromRequestedExecutionDate ")
})
@Uniques({
        @Unique(
                name = "PaymentLine_batch_sequence", members = {"batch", "sequence"}
        )
})
@DomainObject(
        objectType = "payment.PaymentLine"
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
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
        return String.format("%s: %s for %s",
                getCreditorBankAccount().getIban(),
                new DecimalFormat("0.00").format(getAmount()),
                getInvoice().getInvoiceNumber());
    }

    @Column(allowsNull = "false", name = "batchId")
    @Getter @Setter
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
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
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private BankAccount creditorBankAccount;


    @NotPersistent
    public Currency getCurrency() {
        return getInvoice().getCurrency();
    }

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
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
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
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    private IncomingInvoice invoice;


    @Override
    @PropertyLayout(hidden = Where.ALL_TABLES)
    public ApplicationTenancy getApplicationTenancy() {
        return invoice.getApplicationTenancy();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public boolean getUpstreamCreditNoteFound(){
        return incomingInvoiceRepository.findCreditNotesInStateOf(getCreditorBankAccount(), IncomingInvoiceApprovalState.upstreamStates());
    }

    @Programmatic
    public void remove() {
        remove(this);
    }

    public static class CreditorBankAccountComparator implements Comparator<PaymentLine> {
        @Override
        public int compare(PaymentLine l1, PaymentLine l2) {
            return l1.getCreditorBankAccount().compareTo(l2.getCreditorBankAccount());
        }
    }

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;
}
