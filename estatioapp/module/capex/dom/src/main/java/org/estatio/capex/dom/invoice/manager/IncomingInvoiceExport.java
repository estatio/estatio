package org.estatio.capex.dom.invoice.manager;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ViewModel;

import org.estatio.capex.dom.coda.CodaElement;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.project.Project;
import org.estatio.module.asset.dom.FixedAsset;

import lombok.Getter;
import lombok.Setter;

@ViewModel()
@Getter @Setter
public class IncomingInvoiceExport {

    @MemberOrder(sequence = "1") @Nullable
    private final String buyerReference;
    @MemberOrder(sequence = "2") @Nullable
    private final String buyerName;
    @MemberOrder(sequence = "3") @Nullable
    private final String sellerName;
    @MemberOrder(sequence = "4") @Nullable
    private final String sellerReference;
    @MemberOrder(sequence = "5") @Nullable
    private final String sellerIban ;
    @MemberOrder(sequence = "6") @Nullable
    private final LocalDate invoiceDate;
    @MemberOrder(sequence = "7") @Nullable
    private final String invoiceNumber;
    @MemberOrder(sequence = "8") @Nullable
    private final String invoiceStatus;
    @MemberOrder(sequence = "9") @Nullable
    private final BigDecimal netAmount;
    @MemberOrder(sequence = "10") @Nullable
    private final BigDecimal vatAmount;
    @MemberOrder(sequence = "11") @Nullable
    private final BigDecimal grossAmount;
    @MemberOrder(sequence = "12") @Nullable
    private final String invoiceType;
    @MemberOrder(sequence = "13") @Nullable
    private final String propertyReference;
    @MemberOrder(sequence = "14") @Nullable
    private final String propertyName;
    @MemberOrder(sequence = "15") @Nullable
    private final String projectReference;
    @MemberOrder(sequence = "16") @Nullable
    private final String chargeReference;
    @MemberOrder(sequence = "17") @Nullable
    private final String chargeName;
    @MemberOrder(sequence = "18") @Nullable
    private final String vatCode;
    @MemberOrder(sequence = "19") @Nullable
    private final String codaElementCode;
    @MemberOrder(sequence = "20") @Nullable
    private final String codaElementName;
    @MemberOrder(sequence = "21") @Nullable
    private final String documentNumber;
    @MemberOrder(sequence = "22") @Nullable
    private final String comments;


    public IncomingInvoiceExport(
            final IncomingInvoiceItem item,
            final String documentNumber,
            final CodaElement codaElement,
            final String comments
    ){
        IncomingInvoice invoice = (IncomingInvoice) item.getInvoice();

        this.buyerReference = invoice.getBuyer().getReference();
        this.buyerName = invoice.getBuyer().getName();
        this.sellerReference = invoice.getSeller().getReference();
        this.sellerName = invoice.getSeller().getName();

        this.invoiceDate = invoice.getInvoiceDate();
        this.sellerIban = invoice.getBankAccount()!=null ? invoice.getBankAccount().getIban() : null; // since on manual invoices and credit notes bank account is optional
        this.invoiceStatus = invoice.getApprovalState().name();
        this.invoiceNumber = invoice.getInvoiceNumber();

        this.invoiceType = item.getIncomingInvoiceType()!=null ? item.getIncomingInvoiceType().name() : ""; // since the mapping is determined by the item type rather than the invoice type

        final FixedAsset property = item.getFixedAsset();
        this.propertyReference = property == null ? "" :property.getReference();
        this.propertyName = property == null ? "" :property.getName();

        final Project project = item.getProject();
        this.projectReference = project == null ? "" : project.getReference();

        this.chargeReference = item.getCharge() == null ? "" : item.getCharge().getReference();
        this.chargeName =  item.getCharge() == null ? "" : item.getCharge().getName();
        this.vatCode = item.getTax() == null ? "" : item.getTax().getReference();
        this.netAmount = item.getNetAmount();
        this.vatAmount = item.getVatAmount() == null ? BigDecimal.ZERO : item.getVatAmount();
        this.grossAmount = item.getGrossAmount();
        this.documentNumber = documentNumber;

        this.codaElementCode = codaElement == null ? "" : codaElement.getCode();
        this.codaElementName = codaElement == null ? "": codaElement.getName();

        this.comments = comments == null ? "" : comments;
    }

}
