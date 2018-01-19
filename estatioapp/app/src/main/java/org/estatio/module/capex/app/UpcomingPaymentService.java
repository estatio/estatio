package org.estatio.module.capex.app;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.module.capex.app.invoice.UpcomingPaymentTotal;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.PaymentMethod;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "org.estatio.module.capex.app.UpcomingPaymentService")
public class UpcomingPaymentService {

    @Programmatic
    public List<UpcomingPaymentTotal> getUpcomingPayments(){

        List<IncomingInvoice> invoiceSelectionForUpcomingPayments = new ArrayList<>();
        invoiceSelectionForUpcomingPayments.addAll(incomingInvoiceRepository.findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.COMPLETED, PaymentMethod.BANK_TRANSFER));
        invoiceSelectionForUpcomingPayments.addAll(incomingInvoiceRepository.findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.APPROVED, PaymentMethod.BANK_TRANSFER));
        invoiceSelectionForUpcomingPayments.addAll(incomingInvoiceRepository.findByApprovalStateAndPaymentMethod(IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK, PaymentMethod.BANK_TRANSFER));

        List<UpcomingPaymentTotal> upcomingPayments = new ArrayList<>();
        for (IncomingInvoice invoice : invoiceSelectionForUpcomingPayments){
            UpcomingPaymentTotal upcomingPaymentTotal;
            if (paymentTotalForBankAccount(upcomingPayments, invoice).isEmpty()){
                upcomingPaymentTotal = new UpcomingPaymentTotal(debtorBankAccountService.uniqueDebtorAccountToPay(invoice));
                upcomingPaymentTotal.addValue(invoice);
                upcomingPayments.add(upcomingPaymentTotal);
            } else {
                upcomingPaymentTotal = paymentTotalForBankAccount(upcomingPayments, invoice).get(0);
                upcomingPaymentTotal.addValue(invoice);
            }
        }

        return upcomingPayments;
    }

    private List<UpcomingPaymentTotal> paymentTotalForBankAccount(final List<UpcomingPaymentTotal> list, final IncomingInvoice incomingInvoice){
        BankAccount debtorBankAccountForInvoice = debtorBankAccountService.uniqueDebtorAccountToPay(incomingInvoice);
        if (debtorBankAccountForInvoice==null){
            return list.stream().filter(x->x.getDebtorBankAccount()==null).collect(Collectors.toList());
        } else {
            return list.stream().filter(x->x.getDebtorBankAccount()!=null && x.getDebtorBankAccount().equals(debtorBankAccountForInvoice)).collect(Collectors.toList());
        }
    }

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

    @Inject
    DebtorBankAccountService debtorBankAccountService;

}
