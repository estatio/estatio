package org.estatio.module.capex.app.invoice;

import java.math.BigDecimal;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.estatio.module.base.dom.apptenancy.ApplicationTenancyLevel;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.financial.dom.BankAccount;

import lombok.Getter;
import lombok.Setter;
import static org.estatio.module.capex.dom.util.FinancialAmountUtil.addHandlingNulls;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.app.invoice.UpcomingPaymentTotal"
)
public class UpcomingPaymentTotal implements HasAtPath {

    public UpcomingPaymentTotal(){};

    public UpcomingPaymentTotal(final BankAccount bankAccount){
        this.debtorBankAccount = bankAccount;
    }

    @Getter @Setter
    private BankAccount debtorBankAccount;

    @Getter @Setter
    private BigDecimal upcomingAmountForCompleted;

    @Getter @Setter
    private BigDecimal upcomingAmountForApprovedByManager;

    @Getter @Setter
    private BigDecimal upcomingAmountForPendingBankAccountCheck;

    @Getter @Setter
    private BigDecimal totalUpcomingAmount;

    public void addValue(final IncomingInvoice invoice){
        switch (invoice.getApprovalState()){
        case COMPLETED:
            setUpcomingAmountForCompleted(addHandlingNulls(getUpcomingAmountForCompleted(), invoice.getGrossAmount()));
            break;
        case APPROVED:
            setUpcomingAmountForApprovedByManager(addHandlingNulls(getUpcomingAmountForApprovedByManager(), invoice.getGrossAmount()));
            break;
        case PENDING_BANK_ACCOUNT_CHECK:
            setUpcomingAmountForPendingBankAccountCheck(addHandlingNulls(getUpcomingAmountForPendingBankAccountCheck(), invoice.getGrossAmount()));
            break;
        }
        setTotalUpcomingAmount(addHandlingNulls(getTotalUpcomingAmount(), invoice.getGrossAmount()));
    }

    @Programmatic
    @Override
    public String getAtPath() {
        return getDebtorBankAccount() != null
                ? getDebtorBankAccount().getAtPath()
                : ApplicationTenancyLevel.ROOT.getPath();
    }

}
