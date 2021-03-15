package org.estatio.module.coda.app.paymentbatch;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.WorksheetSpec;
import org.isisaddons.module.excel.dom.util.Mode;

import org.estatio.module.capex.app.DebtorBankAccountService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.PaymentBatchRepository;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocHeadRepository;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.party.dom.Party;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "org.estatio.module.coda.app.paymentbatch.PaymentBatchItaUploadService"
)
public class PaymentBatchItaUploadService {

    @Programmatic
    public PaymentBatch importPaymentBatch(
            final Party ecpBuyerCompany,
            final Blob spreadsheet){

        List<List<?>> res = excelService.fromExcel(
                spreadsheet,
                sheetName -> {
                    if(sheetName!=null) {
                        return new WorksheetSpec(
                                PaymentBatchItaImportLine.class,
                                sheetName,
                                Mode.RELAXED);
                    }
                    else
                        return null;
                }
        );
        List<PaymentBatchItaImportLine> lines = (List) res.get(0);

        BankAccount buyerBankAccount = debtorBankAccountService.uniqueDebtorAccountToPay(ecpBuyerCompany);
        if (buyerBankAccount!=null) {
            PaymentBatch newBatch = paymentBatchRepository.findOrCreateNewByDebtorBankAccount(buyerBankAccount);
            Lists.newArrayList(newBatch.getLines()).stream().forEach(l->l.remove());
            for (PaymentBatchItaImportLine line : lines){
                final CodaDocHead docHeadIfAny = codaDocHeadRepository.findByCmpCodeAndDocCodeAndDocNum(ecpBuyerCompany.getReference(), line.getCodiceDocumento(), line.getNumeroDocumento());
                if (docHeadIfAny!=null) {
                    final IncomingInvoice invoiceFromLine = docHeadIfAny.getIncomingInvoice();
                    if (invoiceFromLine != null) {
                        if (invoiceFromLine.getPaymentMethod()!=null && invoiceFromLine.getPaymentMethod()!=PaymentMethod.BANK_TRANSFER) {
                            messageService.warnUser(String.format("Invoice number %s of %s has payment method %s and should not be in this payment batch.", invoiceFromLine.getInvoiceNumber(), invoiceFromLine.getSeller().getName(), invoiceFromLine.getPaymentMethod().title()));
                        } else {
                            if (invoiceFromLine.getApprovalState()==IncomingInvoiceApprovalState.PAID) messageService.warnUser(String.format("NOTE: Invoice number %s of %s is marked PAID in Estatio!", invoiceFromLine.getInvoiceNumber(), invoiceFromLine.getSeller().getName()));
                            if (invoiceFromLine.getApprovalState()!=IncomingInvoiceApprovalState.PAYABLE) messageService.warnUser(String.format("NOTE: Invoice number %s of %s is NOT in a state of PAYABLE", invoiceFromLine.getInvoiceNumber(), invoiceFromLine.getSeller().getName()));
                            newBatch.addLineIfRequired(invoiceFromLine);
                        }
                    }
                }
            }
            return newBatch;
        } else {
            messageService.warnUser(String.format("Could not determine which bank account to use for %s", ecpBuyerCompany.getReference()));
        }
        return null;
    }

    @Inject DebtorBankAccountService debtorBankAccountService;

    @Inject PaymentBatchRepository paymentBatchRepository;

    @Inject ExcelService excelService;

    @Inject CodaDocHeadRepository codaDocHeadRepository;

    @Inject MessageService messageService;

}
