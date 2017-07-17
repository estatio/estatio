package org.estatio.capex.dom.payment;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.dom.financial.bankaccount.BankAccount;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = PaymentBatch.class
)
public class PaymentBatchRepository {

    @Programmatic
    public java.util.List<PaymentBatch> listAll() {
        return repositoryService.allInstances(PaymentBatch.class);
    }


    @Programmatic
    public List<PaymentBatch> findByDebtorBankAccountAndApprovalState(
            final BankAccount debtorBankAccount,
            final PaymentBatchApprovalState approvalState) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        PaymentBatch.class,
                        "findByDebtorBankAccountAndApprovalState",
                        "debtorBankAccount", debtorBankAccount,
                        "approvalState", approvalState));
    }

    @Programmatic
    public PaymentBatch findNewByDebtorBankAccount(final BankAccount debtorBankAccount) {
        List<PaymentBatch> paymentBatches =
                findByDebtorBankAccountAndApprovalState(debtorBankAccount, PaymentBatchApprovalState.NEW);
        return paymentBatches.size() == 1 ? paymentBatches.get(0) :  null;
    }

    @Programmatic
    public List<PaymentBatch> findByDebtorBankAccount(
            final BankAccount debtorBankAccount) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        PaymentBatch.class,
                        "findByDebtorBankAccount",
                        "debtorBankAccount", debtorBankAccount));
    }

    @Programmatic
    public List<PaymentBatch> findNewBatches() {
        return findByApprovalState(PaymentBatchApprovalState.NEW);
    }

    @Programmatic
    public List<PaymentBatch> findCompletedBatches() {
        return findByApprovalState(PaymentBatchApprovalState.COMPLETED);
    }

    @Programmatic
    public List<PaymentBatch> findByApprovalState(final PaymentBatchApprovalState approvalState) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        PaymentBatch.class,
                        "findByApprovalState",
                        "approvalState", approvalState));
    }


    @Programmatic
    public PaymentBatch findOrCreateNewByDebtorBankAccount(final BankAccount debtorBankAccount) {
        PaymentBatch paymentBatchIfAny = findNewByDebtorBankAccount(debtorBankAccount);

        if (paymentBatchIfAny != null) {
            return paymentBatchIfAny;
        }
        return create(clockService.nowAsDateTime(), debtorBankAccount, PaymentBatchApprovalState.NEW);
    }


    @Programmatic
    public List<PaymentBatch> findByCreatedOnBetween(DateTime startDate, DateTime endDate) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        PaymentBatch.class,
                        "findByCreatedOnBetween",
                        "startDate", startDate,
                        "endDate", endDate));
    }

    @Programmatic
    public PaymentBatch create(
            final DateTime createdOn,
            final BankAccount debtorBankAccount,
            final PaymentBatchApprovalState approvalState) {
        final PaymentBatch paymentBatch = new PaymentBatch(createdOn, debtorBankAccount, approvalState);
        serviceRegistry2.injectServicesInto(paymentBatch);
        repositoryService.persistAndFlush(paymentBatch);
        return paymentBatch;
    }

    @Programmatic
    public void delete(final PaymentBatch paymentBatch) {
        repositoryService.removeAndFlush(paymentBatch);
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    ClockService clockService;

}
