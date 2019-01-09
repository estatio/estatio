package org.estatio.module.coda.app.paymentbatch;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.value.Blob;

import org.isisaddons.module.excel.dom.ExcelService;
import org.isisaddons.module.excel.dom.util.Mode;

import org.estatio.module.capex.app.DebtorBankAccountService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.payment.PaymentBatch;
import org.estatio.module.capex.dom.payment.PaymentBatchRepository;
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
            for (PaymentBatchItaImportLine line : lines){
                final IncomingInvoice invoiceFromLine = codaDocHeadRepository.findByCmpCodeAndDocCodeAndDocNum(ecpBuyerCompany.getReference(), line.getCodiceDocumento(), line.getNumeroDocumento()).getIncomingInvoice();
                if (invoiceFromLine!=null) newBatch.addLineIfRequired(invoiceFromLine);
            }
            return newBatch;
        }
        return null;
    }

    @Inject DebtorBankAccountService debtorBankAccountService;

    @Inject PaymentBatchRepository paymentBatchRepository;

    @Inject ExcelService excelService;

    @Inject CodaDocHeadRepository codaDocHeadRepository;

}
