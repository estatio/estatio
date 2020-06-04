package org.estatio.module.capex.app.invoicedownload;

import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.joda.time.LocalDate;


import javax.annotation.Nullable;
import java.math.BigDecimal;


@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.app.invoicedownload.FullyApprovedInvoiceItaDownload"
)
@Getter @Setter
public class FullyApprovedInvoiceItaDownload {

    @MemberOrder(sequence = "1") @Nullable
    private final String property;
    @MemberOrder(sequence = "2") @Nullable
    private final String codaDocHead;
    @MemberOrder(sequence = "3") @Nullable
    private final String supplier;
    @MemberOrder(sequence = "4") @Nullable
    private final String invoiceNumber;
    @MemberOrder(sequence = "5") @Nullable
    private final BigDecimal grossAmount;
    @MemberOrder(sequence = "6") @Nullable
    private final String lastApprovedBy;
    @MemberOrder(sequence = "7") @Nullable
    private final LocalDate lastApprovedOn;
    @MemberOrder(sequence = "8") @Nullable
    private final String payStatus;
    @MemberOrder(sequence = "9") @Nullable
    private final String invoiceApprovalState;


    public FullyApprovedInvoiceItaDownload(
            final IncomingInvoice invoice,
            final CodaDocHead codaDocHead
    ){
        this.property = invoice.getProperty().getReference();
        this.codaDocHead = String.format("(%s, %s, %s)", codaDocHead.getCmpCode(), codaDocHead.getDocCode(), codaDocHead.getDocNum());
        this.supplier = String.format("%s %s", invoice.getSeller().getReference(), invoice.getSeller().getName());
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.grossAmount = invoice.getGrossAmount();
        this.lastApprovedBy = invoice.getMostRecentApproval()
                .map(IncomingInvoice.ApprovalString::getCompletedBy)
                .orElse(null);
        this.lastApprovedOn = invoice.getMostRecentApproval()
                .map(IncomingInvoice.ApprovalString::getCompletedOnAsDate)
                .orElse(null);
        this.payStatus = codaDocHead.getStatPay();
        this.invoiceApprovalState = invoice.getApprovalState().toString();
    }

}
