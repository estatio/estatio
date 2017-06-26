package org.estatio.capex.dom.payment;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.capex.dom.payment.approval.PaymentBatchApprovalState;
import org.estatio.capex.dom.payment.manager.PaymentBatchManager;
import org.estatio.dom.assetfinancial.FixedAssetFinancialAccountRepository;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.BankAccountRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "payments.PaymentBatchMenu"
)
@DomainServiceLayout(
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        named = "Invoices In",
        menuOrder = "300"
)
public class PaymentBatchMenu {



    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(cssClassFa = "fa-magic")
    @MemberOrder(sequence = "300.10")
    public PaymentBatchManager preparePaymentBatches() {
        final PaymentBatchManager paymentBatchManager = new PaymentBatchManager();
        serviceRegistry2.injectServicesInto(paymentBatchManager);
        return paymentBatchManager.init();
    }

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "300.20")
    public List<PaymentBatch> findPaymentBatchByDebtorBankAccount(
            final BankAccount debtorBankAccount,
            final PaymentBatchApprovalState approvalState) {
        return paymentBatchRepository.findByDebtorBankAccountAndApprovalState(debtorBankAccount, approvalState);
    }


    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "300.90")
    public List<PaymentBatch> allPaymentBatches(){
        return paymentBatchRepository.listAll();
    }


    ///////////////////////////////////////////

    @Inject
    FixedAssetFinancialAccountRepository fixedAssetFinancialAccountRepository;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    BankAccountRepository bankAccountRepository;

    @Inject
    PaymentBatchRepository paymentBatchRepository;

    @Inject
    ClockService clockService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

}
