package org.estatio.module.capex.app.credittransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.module.capex.dom.documents.LookupAttachedPdfService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceRepository;
import org.estatio.module.capex.dom.payment.CreditTransfer;
import org.estatio.module.capex.dom.payment.PaymentLine;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "org.estatio.module.capex.app.credittransfer.CreditTransferExportService"
)
public class CreditTransferExportService {

    @Programmatic
    public boolean isFirstUseBankAccount(final CreditTransfer creditTransfer){
        List<IncomingInvoice> otherInvoicesForThisBankAccount =
                incomingInvoiceRepository.findByBankAccount(creditTransfer.getSellerBankAccount())
                .stream()
                .filter(x->!invoicesInTransfer(creditTransfer).contains(x))
                .collect(Collectors.toList());
        if (otherInvoicesForThisBankAccount.isEmpty()) return true;
        return false;
    }

    private List<IncomingInvoice> invoicesInTransfer(final CreditTransfer creditTransfer){
        List<IncomingInvoice> result = new ArrayList<>();
        for (PaymentLine line : creditTransfer.getLines()){
            result.add(line.getInvoice());
        }
        return result;
    }

    @Programmatic
    public String getApprovalStateTransitionSummary(final IncomingInvoice invoice) {
        StringBuilder builder = new StringBuilder();
        Boolean first = true;
        for (IncomingInvoice.ApprovalString approvalString : invoice.getApprovals()){
            if (!first) builder.append(" " + Character.toString((char)10)); // line break within a cell
            builder.append(approvalString.getCompletedBy());
            first = false;
        }
        return builder.toString();
    }

    @Programmatic
    public String getInvoiceDocumentName(final IncomingInvoice invoice) {
        final Optional<Document> document = lookupAttachedPdfService.lookupIncomingInvoicePdfFrom(invoice);
        return document.isPresent() ? document.get().getName() : null;
    }

    @Inject
    LookupAttachedPdfService lookupAttachedPdfService;

    @Inject
    IncomingInvoiceRepository incomingInvoiceRepository;

}
