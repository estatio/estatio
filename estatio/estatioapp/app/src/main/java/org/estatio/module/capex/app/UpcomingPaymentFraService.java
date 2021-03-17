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
import org.estatio.module.financial.dom.BankAccount;

import static org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository.AT_PATHS_FRA_OFFICE;
import static org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState.APPROVED;
import static org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState.COMPLETED;
import static org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState.PENDING_BANK_ACCOUNT_CHECK;
import static org.estatio.module.invoice.dom.PaymentMethod.BANK_TRANSFER;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "org.estatio.module.capex.app.UpcomingPaymentService")
public class UpcomingPaymentFraService {

    @Programmatic
    public List<UpcomingPaymentTotal> getUpcomingPaymentsFra(){

        List<IncomingInvoice> invoiceSelectionForUpcomingPayments = new ArrayList<>();
        invoiceSelectionForUpcomingPayments.addAll(
                incomingInvoiceRepository.findByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                    AT_PATHS_FRA_OFFICE, COMPLETED, BANK_TRANSFER));
        invoiceSelectionForUpcomingPayments.addAll(
                incomingInvoiceRepository.findByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                        AT_PATHS_FRA_OFFICE, APPROVED, BANK_TRANSFER));
        invoiceSelectionForUpcomingPayments.addAll(
                incomingInvoiceRepository.findByAtPathPrefixesAndApprovalStateAndPaymentMethod(
                        AT_PATHS_FRA_OFFICE, PENDING_BANK_ACCOUNT_CHECK, BANK_TRANSFER));

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
