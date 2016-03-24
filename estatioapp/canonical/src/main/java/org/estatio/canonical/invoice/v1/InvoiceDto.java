package org.estatio.canonical.invoice.v1;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import com.google.common.collect.Lists;

import org.apache.isis.schema.common.v1.OidDto;

import org.estatio.canonical.VersionedDto;

import lombok.Getter;
import lombok.Setter;

/**
 * Designed to be usable both as a view model (exposed from RO) and within the Camel ESB.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "majorVersion",
        "minorVersion",
        "atPath",
        "buyerParty",
        "sellerParty",
        "invoiceDate",
        "dueDate",
        "invoiceNumber",
        "collectionNumber",
        "paidByMandate",
        "paidByMandateBankAccount",
        "agreementReference",
        "fixedAssetReference",
        "fixedAssetExternalReference",
        "items",
        "netAmount",
        "vatAmount",
        "grossAmount"
})
@XmlRootElement(name = "invoiceDto")
public class InvoiceDto implements VersionedDto {

    @XmlElement(required = true, defaultValue = "1")
    public final String getMajorVersion() {
        return "1";
    }

    @XmlElement(required = true, defaultValue = "0")
    public String getMinorVersion() {
        return "0";
    }

    @XmlElement(required = true)
    @Getter @Setter
    protected String atPath;

    @XmlElement(required = true)
    @Getter @Setter
    protected OidDto buyerParty;

    @XmlElement(required = true)
    @Getter @Setter
    protected OidDto sellerParty;

    @Getter @Setter
    private XMLGregorianCalendar invoiceDate;

    @XmlElement(required = true)
    @Getter @Setter
    private XMLGregorianCalendar dueDate;

    @Getter @Setter
    private String invoiceNumber;

    @Getter @Setter
    private String collectionNumber;

    @Getter @Setter
    protected OidDto paidByMandate;

    /**
     * Of type FinancialAccount
     */
    @Getter @Setter
    protected OidDto paidByMandateBankAccount;

    @Getter @Setter
    private String agreementReference;

    @Getter @Setter
    private String fixedAssetReference;

    @Getter @Setter
    private String fixedAssetExternalReference;

    @XmlElementWrapper
    @XmlElement(name = "item")
    @Getter @Setter
    protected List<InvoiceItemDto> items = Lists.newArrayList();


    @Getter @Setter
    @XmlElement(required = true, defaultValue = "0.00")
    protected BigDecimal netAmount;

    @Getter @Setter
    @XmlElement(required = true, defaultValue = "0.00")
    protected BigDecimal grossAmount;

    @Getter @Setter
    @XmlElement(required = true, defaultValue = "0.00")
    protected BigDecimal vatAmount;

}
