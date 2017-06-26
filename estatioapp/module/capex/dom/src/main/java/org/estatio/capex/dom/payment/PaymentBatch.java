package org.estatio.capex.dom.payment;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalStateTransition;
import org.estatio.capex.dom.state.State;
import org.estatio.capex.dom.state.StateTransition;
import org.estatio.capex.dom.state.StateTransitionType;
import org.estatio.capex.dom.state.Stateful;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.asset.Property;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccount;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccountRepository;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.bankaccount.BankAccount;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "PaymentBatch"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByDebtorBankAccountAndApprovalState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.payment.PaymentBatch "
                        + "WHERE debtorBankAccount == :debtorBankAccount "
                        + "   && approvalState     == :approvalState "
                        + "ORDER BY createdOn DESC "
        ),
        @Query(
                name = "findByDebtorBankAccount", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.payment.PaymentBatch "
                        + "WHERE debtorBankAccount == :debtorBankAccount "
                        + "ORDER BY createdOn DESC "
        ),
        @Query(
                name = "findByApprovalState", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.payment.PaymentBatch "
                        + "WHERE approvalState  == :approvalState "
                        + "ORDER BY createdOn DESC "
        )
})
@DomainObject(
        objectType = "payment.PaymentBatch"
)
public class PaymentBatch extends UdoDomainObject2<PaymentBatch> implements Stateful {

    public PaymentBatch() {
        super("createdOn, debtorBankAccount");
    }

    public PaymentBatch(
            final DateTime createdOn,
            final BankAccount debtorBankAccount,
            final PaymentBatchApprovalState approvalState){
        this();
        this.createdOn = createdOn;
        this.debtorBankAccount = debtorBankAccount;
        this.approvalState = approvalState;
    }

    // TODO: derive somehow...
    // Document > PmtInf > PmtInfId


    /**
     * Document > CstmrCdtTrfInitn > GrpHdr > CreDtTm
     */
    @Column(allowsNull = "false")
    @Getter @Setter
    private DateTime createdOn;

    /**
     * Document > PmtInf > DbtrAcct > Id > IBAN
     * Document > PmtInf > DbtrAgt > FinInstnId > BIC
     */
    @Column(allowsNull = "false", name = "debtorBankAccountId")
    @Getter @Setter
    private BankAccount debtorBankAccount;

    /**
     * Document > PmtInf > ReqdExctnDt
     */
    @Column(allowsNull = "true")
    @Getter @Setter
    private DateTime requestedExecutionDate;

    /**
     * Document > PmtInf > CdtTrfTxInf (* many)
     */
    @Persistent(mappedBy = "batch", dependentElement = "true")
    @Getter @Setter
    private SortedSet<PaymentLine> lines = new TreeSet<>();

    @Programmatic
    public boolean hasUniqueAccountToPay(final IncomingInvoice invoice) {
        Property propertyIfAny = invoice.getProperty();
        List<FinancialAccount> matchingAccounts = financialAccountsFor(propertyIfAny);
        return matchingAccounts.size() == 1;
    }

    @Programmatic
    public List<FinancialAccount> financialAccountsFor(final Property propertyIfAny) {
        if(propertyIfAny == null) {
            return Collections.emptyList();
        }
        List<FixedAssetFinancialAccount> fafrList = fixedAssetFinancialAccountRepository.findByFixedAsset(propertyIfAny);
        return fafrList.stream()
                .map(FixedAssetFinancialAccount::getFinancialAccount)
                .filter(fa -> fa == getDebtorBankAccount())
                .collect(Collectors.toList());
    }

    @Programmatic
    public boolean accepts(final IncomingInvoice unmatchedInvoice) {
        Property propertyIfAny = unmatchedInvoice.getProperty();
        if(propertyIfAny == null) {
            // TODO: need to think further on this
            return false;
        }
        List<FixedAssetFinancialAccount> fafrList = fixedAssetFinancialAccountRepository.findByFixedAsset(propertyIfAny);
        for (FixedAssetFinancialAccount fixedAssetFinancialAccount : fafrList) {
            FinancialAccount financialAccount = fixedAssetFinancialAccount.getFinancialAccount();
            if(financialAccount == getDebtorBankAccount()) {
                updateFor(unmatchedInvoice);
                return true;
            }
        }
        return false;
    }

    @Programmatic
    public boolean contains(final IncomingInvoice invoice) {
        return lineFor(invoice) != null;
    }

    @Programmatic
    public PaymentLine updateFor(final IncomingInvoice incomingInvoice) {
        PaymentLine line = lineFor(incomingInvoice);
        if (line == null) {
            final int nextSequence = getLines().size() + 1;
            line = new PaymentLine(this, nextSequence, incomingInvoice);
            serviceRegistry2.injectServicesInto(line);
            getLines().add(line);
        }
        return line;
    }

    private PaymentLine lineFor(final IncomingInvoice invoice) {
        for (final PaymentLine line : getLines()) {
            if(line.getCreditorBankAccount() == invoice.getBankAccount()) {
                return line;
            }
        }
        return null;
    }

    @Inject
    ServiceRegistry2 serviceRegistry2;


    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return getDebtorBankAccount().getApplicationTenancy();
    }



    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false")
    private PaymentBatchApprovalState approvalState;

    @Override
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > S getStateOf(
            final Class<ST> stateTransitionClass) {
        if(stateTransitionClass == PaymentBatchApprovalStateTransition.class) {
            return (S) approvalState;
        }
        return null;
    }

    @Override
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > void setStateOf(
            final Class<ST> stateTransitionClass, final S newState) {
        if(stateTransitionClass == PaymentBatchApprovalStateTransition.class) {
            setApprovalState( (PaymentBatchApprovalState) newState );
        }
    }

    @Programmatic
    public String reasonDisabledDueToState() {
        PaymentBatchApprovalState currentState = getApprovalState();
        return currentState == PaymentBatchApprovalState.NEW ?
                null :
                "Cannot modify because payment batch is in state of " + currentState;
    }

    @Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

}
