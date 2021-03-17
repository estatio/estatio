package org.estatio.module.capex.dom.invoice;

import java.math.BigInteger;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.schema.utils.jaxbadapters.JodaLocalDateStringAdapter;

import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalState;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.NONDURABLE,
        table = "IncomingInvoiceQueryHelper",
        schema = "v",
        extensions = {
                @Extension(vendorName = "datanucleus", key = "view-definition",
                        value = "CREATE VIEW \"v\".\"IncomingInvoiceQueryHelper\" " +
                                "( " +
                                "  {this.invoiceUuid}, " +
                                "  {this.invoiceApprovalState}, " +
                                "  {this.invoiceItemReportedDate}, " +

                                "  {this.invoiceItemFixedAssetReference}, " +
                                "  {this.invoiceItemType}, " +
                                "  {this.invoiceBuyerAtPath}, " +
                                "  {this.invoiceItemIsReversal}, " +
                                "  {this.invoiceItemSequence} " +
                                ") AS " +
                                "SELECT I.\"uuid\" as \"invoiceUuid\"" +
                                ", I.\"approvalState\" as \"invoiceApprovalState\"" +
                                ", II.\"reportedDate\" as \"invoiceItemReportedDate\"" +
                                ", FA.\"reference\" as \"invoiceItemFixedAssetReference\"" +
                                ", II.\"incomingInvoiceType\" as \"invoiceItemType\"" +
                                ", P.\"atPath\" as \"invoiceBuyerAtPath\"" +
                                ", CONVERT(CASE WHEN II.\"reversalOfInvoiceItemId\" is null THEN 0 ELSE 1 END, SQL_BOOLEAN) as \"invoiceItemIsReversal\"" +
                                ", II.\"sequence\" as \"invoiceItemSequence\" " +
                                " FROM \"dbo\".\"InvoiceItem\" II " +
                                " INNER JOIN \"dbo\".\"Invoice\" I on II.\"invoiceId\" = I.\"id\" " +
                                " LEFT OUTER JOIN \"dbo\".\"FixedAsset\" FA on II.\"fixedAssetId\" = FA.\"id\" " +
                                " LEFT OUTER JOIN \"dbo\".\"Party\" P ON I.\"buyerPartyId\" = P.\"id\" " +
                                " WHERE I.\"discriminator\" = 'incomingInvoice.IncomingInvoice'"
                )
        })
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByInvoiceItemReportedDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.capex.dom.invoice.IncomingInvoiceQueryHelper " +
                        "WHERE invoiceItemReportedDate == :invoiceItemReportedDate "),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetReferenceAndItemTypeAndReportedDateAndBuyerAtPath", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.capex.dom.invoice.IncomingInvoiceQueryHelper " +
                        "WHERE invoiceItemFixedAssetReference == :invoiceItemFixedAssetReference "
                        + " && invoiceItemType == :invoiceItemType "
                        + " && invoiceItemReportedDate == :invoiceItemReportedDate "
                        + " && invoiceBuyerAtPath != null "
                        + " && :atPaths.contains(invoiceBuyerAtPath.substring(0,4)) "),
        @javax.jdo.annotations.Query(
                name = "findByFixedAssetReferenceAndReportedDateAndBuyerAtPath", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.module.capex.dom.invoice.IncomingInvoiceQueryHelper " +
                        "WHERE invoiceItemFixedAssetReference == :invoiceItemFixedAssetReference "
                        + " && invoiceItemReportedDate == :invoiceItemReportedDate "
                        + " && invoiceBuyerAtPath != null "
                        + " && :atPaths.contains(invoiceBuyerAtPath.substring(0,4)) "),
})
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@XmlRootElement(name = "IncomingInvoiceQueryHelper")
@XmlType(
        propOrder = {
                "invoiceUuid",
                "invoiceApprovalState",
                "invoiceItemReportedDate",

                "invoiceItemFixedAssetReference",
                "invoiceItemType",
                "invoiceBuyerAtPath",
                "invoiceItemIsReversal",
                "invoiceItemSequence"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@DomainObject(objectType = "org.estatio.module.capex.dom.invoice.IncomingInvoiceQueryHelper")
@AllArgsConstructor
@Getter @Setter
public class IncomingInvoiceQueryHelper {

    private String invoiceUuid;

    private IncomingInvoiceApprovalState invoiceApprovalState;

    @XmlJavaTypeAdapter(JodaLocalDateStringAdapter.ForJaxb.class)
    private LocalDate invoiceItemReportedDate;

    private String invoiceItemFixedAssetReference;

    private IncomingInvoiceType invoiceItemType;

    private String invoiceBuyerAtPath;

    private boolean invoiceItemIsReversal;

    private BigInteger invoiceItemSequence;

}
