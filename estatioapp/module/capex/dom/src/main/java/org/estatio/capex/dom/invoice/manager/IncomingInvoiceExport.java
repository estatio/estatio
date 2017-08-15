package org.estatio.capex.dom.invoice.manager;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ViewModel;

import org.estatio.capex.dom.coda.CodaElement;
import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.asset.FixedAsset;

import lombok.Getter;
import lombok.Setter;

@ViewModel()
@Getter @Setter
public class IncomingInvoiceExport {

    @MemberOrder(sequence = "1")
    private final String buyerReference;
    @MemberOrder(sequence = "2")
    private final String buyerName;
    @MemberOrder(sequence = "3")
    private final String sellerName;
    @MemberOrder(sequence = "4")
    private final String sellerReference;
    @MemberOrder(sequence = "5")
    private final String sellerIban ;
    @MemberOrder(sequence = "6")
    private final LocalDate invoiceDate;
    @MemberOrder(sequence = "7")
    private final String invoiceNumber;
    @MemberOrder(sequence = "8")
    private final String invoiceStatus;
    @MemberOrder(sequence = "9")
    private final BigDecimal netAmount;
    @MemberOrder(sequence = "10")
    private final BigDecimal vatAmount;
    @MemberOrder(sequence = "11")
    private final BigDecimal grossAmount;
    @MemberOrder(sequence = "12")
    private final String invoiceType;
    @MemberOrder(sequence = "13")
    private final String propertyReference;
    @MemberOrder(sequence = "14")
    private final String propertyName;
    @MemberOrder(sequence = "15")
    private final String projectReference;
    @MemberOrder(sequence = "16")
    private final String chargeReference;
    @MemberOrder(sequence = "17")
    private final String chargeName;
    @MemberOrder(sequence = "18")
    private final String vatCode;
    @MemberOrder(sequence = "19")
    private final String codaElementCode;
    @MemberOrder(sequence = "20")
    private final String codaElementName;
    @MemberOrder(sequence = "21")
    private final String documentNumber;
    @MemberOrder(sequence = "22")
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
        this.sellerIban = invoice.getBankAccount().getIban();
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
