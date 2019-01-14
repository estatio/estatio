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
import org.isisaddons.module.excel.dom.util.Mode;

import org.estatio.module.capex.app.DebtorBankAccountService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.PaymentBatchRepository;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocHeadRepository;
import org.estatio.module.financial.dom.BankAccount;
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

        List<PaymentBatchItaImportLine> lines = excelService.fromExcel(spreadsheet, PaymentBatchItaImportLine.class, "Sheet 1", Mode.RELAXED);

        BankAccount buyerBankAccount = debtorBankAccountService.uniqueDebtorAccountToPay(ecpBuyerCompany);
        if (buyerBankAccount!=null) {
            PaymentBatch newBatch = paymentBatchRepository.findOrCreateNewByDebtorBankAccount(buyerBankAccount);
            Lists.newArrayList(newBatch.getLines()).stream().forEach(l->l.remove());
            for (PaymentBatchItaImportLine line : lines){
                final CodaDocHead docHeadIfAny = codaDocHeadRepository.findByCmpCodeAndDocCodeAndDocNum(ecpBuyerCompany.getReference(), line.getCodiceDocumento(), line.getNumeroDocumento());
                if (docHeadIfAny!=null) {
                    final IncomingInvoice invoiceFromLine = docHeadIfAny.getIncomingInvoice();
                    if (invoiceFromLine != null) newBatch.addLineIfRequired(invoiceFromLine);
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
