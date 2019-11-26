package org.estatio.module.capex.app.invoicedownload;

import java.math.BigDecimal;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Nature;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.capex.dom.coda.CodaElement;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.project.Project;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.app.invoicedownload.IncomingInvoiceExport"
)
@Getter @Setter
public class IncomingInvoiceExport {

    @MemberOrder(sequence = "1") @Nullable
    private final String codaElement2;
    @MemberOrder(sequence = "2") @Nullable
    private final String buyerReference;
    @MemberOrder(sequence = "3") @Nullable
    private final String buyerName;
    @MemberOrder(sequence = "4") @Nullable
    private final String codaElement1;
    @MemberOrder(sequence = "5") @Nullable
    private final String sellerName;
    @MemberOrder(sequence = "6") @Nullable
    private final String sellerReference;
    @MemberOrder(sequence = "7") @Nullable
    private final String codaElement6;
    @MemberOrder(sequence = "8") @Nullable
    private final String sellerIban ;
    @MemberOrder(sequence = "9") @Nullable
    private final LocalDate invoiceDate;
    @MemberOrder(sequence = "10") @Nullable
    private final String invoiceNumber;
    @MemberOrder(sequence = "11") @Nullable
    private final String invoiceStatus;
    @MemberOrder(sequence = "12") @Nullable
    private final BigDecimal netAmount;
    @MemberOrder(sequence = "13") @Nullable
    private final BigDecimal vatAmount;
    @MemberOrder(sequence = "14") @Nullable
    private final BigDecimal grossAmount;
    @MemberOrder(sequence = "15") @Nullable
    private final String invoiceType;
    @MemberOrder(sequence = "16") @Nullable
    private final String propertyReference;
    @MemberOrder(sequence = "17") @Nullable
    private final String propertyName;
    @MemberOrder(sequence = "18") @Nullable
    private final String codaElement3;
    @MemberOrder(sequence = "19") @Nullable
    private final String projectReference;
    @MemberOrder(sequence = "20") @Nullable
    private final String chargeReference;
    @MemberOrder(sequence = "21") @Nullable
    private final String chargeName;
    @MemberOrder(sequence = "22") @Nullable
    private final String vatCode;
    @MemberOrder(sequence = "23") @Nullable
    private final String codaElement5;
    @MemberOrder(sequence = "24") @Nullable
    private final String codaElementName;
    @MemberOrder(sequence = "25") @Nullable
    private final String documentNumber;
    @MemberOrder(sequence = "26") @Nullable
    private final String comments;


    public IncomingInvoiceExport(
            final IncomingInvoiceItem item,
            final String documentNumber,
            final CodaElement codaElement,
            final String comments
    ){
        IncomingInvoice invoice = (IncomingInvoice) item.getInvoice();
        this.codaElement2 = "STAT";
        this.buyerReference = invoice.getBuyer().getReference();
        this.buyerName = invoice.getBuyer().getName();
        this.codaElement1 = invoice.getBuyer().getReference().concat("EUR");
        this.sellerReference = invoice.getSeller().getReference();
        this.sellerName = invoice.getSeller().getName();
        this.codaElement6 = getCodaElement6FromSellerReference(invoice.getSeller().getReference());

        this.invoiceDate = invoice.getInvoiceDate();
        this.sellerIban = invoice.getBankAccount()!=null ? invoice.getBankAccount().getIban() : null; // since on manual invoices and credit notes bank account is optional
        this.invoiceStatus = invoice.getApprovalState().name();
        this.invoiceNumber = invoice.getInvoiceNumber();

        final IncomingInvoiceType incomingInvoiceType = item.getIncomingInvoiceType();
        this.invoiceType = incomingInvoiceType !=null ? incomingInvoiceType.name() : ""; // since the mapping is determined by the item type rather than the invoice type

        final FixedAsset property = item.getFixedAsset();
        this.propertyReference = property == null ? "" :property.getReference();
        this.propertyName = property == null ? "" :property.getName();
        this.codaElement3 = deriveCodaElement3FromPropertyAndIncomingInvoiceType(property, incomingInvoiceType);

        final Project project = item.getProject();
        this.projectReference = project == null ? "" : project.getReference();

        this.chargeReference = item.getCharge() == null ? "" : item.getCharge().getReference();
        this.chargeName =  item.getCharge() == null ? "" : item.getCharge().getName();
        this.vatCode = item.getTax() == null ? "" : item.getTax().getReference();
        this.netAmount = item.getNetAmount();
        this.vatAmount = item.getVatAmount() == null ? BigDecimal.ZERO : item.getVatAmount();
        this.grossAmount = item.getGrossAmount();
        this.documentNumber = documentNumber;

        this.codaElement5 = codaElement == null ? "" : codaElement.getCode();
        this.codaElementName = codaElement == null ? "": codaElement.getName();

        this.comments = comments == null ? "" : comments;
    }

    public static String getCodaElement6FromSellerReference(final String sellerReference){
        if (sellerReference.startsWith("FR")) {
            return "FRFO".concat(sellerReference.substring(2));
        }
        if (sellerReference.startsWith("BE")){
            return "BEFO".concat(sellerReference.substring(2));
        }
        return null;
    }

    public static String deriveCodaElement3FromPropertyAndIncomingInvoiceType(final FixedAsset property, final IncomingInvoiceType incomingInvoiceType){
        if (incomingInvoiceType==IncomingInvoiceType.CORPORATE_EXPENSES){
            return "FRGGEN0";
        }
        if (incomingInvoiceType==IncomingInvoiceType.LOCAL_EXPENSES){
            return "FRGPAR0";
        }
        if (property!=null){
            return property.getExternalReference();
        }
        return null;
    }

}
