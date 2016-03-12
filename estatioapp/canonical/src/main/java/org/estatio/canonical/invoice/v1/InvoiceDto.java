package org.estatio.canonical.invoice.v1;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
    "buyerParty",
    "sellerParty",
    "paidByMandate",
    "paidByMandateBankAccount",
    "items"
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
    protected OidDto buyerParty;

    @XmlElement(required = true)
    @Getter @Setter
    protected OidDto sellerParty;

    @XmlElement(required = true)
    @Getter @Setter
    protected OidDto paidByMandate;


    /**
     * Of type FinancialAccount
     */
    @XmlElement(required = true)
    @Getter @Setter
    protected OidDto paidByMandateBankAccount;


    @XmlElementWrapper
    @XmlElement(name = "item")
    @Getter @Setter
    protected List<InvoiceItemDto> items = Lists.newArrayList();

}
