package org.estatio.canonical.financial.bankaccount.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.schema.common.v1.OidDto;

import lombok.Getter;
import lombok.Setter;

/**
 * Designed to be usable both as a view model (exposed from RO) and within the Camel ESB.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "owner",
    "reference",
    "externalReference",
    "name",
    "iban",
    "accountNumber",
    "branchCode",
    "bank",
    "nationalBankCode",
    "nationalCheckCode"
})
@XmlRootElement(name = "bankAccountDto")
public class BankAccountDto {

    public BankAccountDto() {}

    @XmlElement(required = true, defaultValue = "1")
    public final String getMajorVersion() {
        return "1";
    }

    @XmlElement(required = true, defaultValue = "0")
    public String getMinorVersion() {
        return "0";
    }

    /**
     * Of type Party
     */
    @XmlElement(required = true)
    @Getter @Setter
    protected OidDto owner;

    @XmlElement(required = true)
    @Getter @Setter
    protected String reference;

    @Getter @Setter
    protected String externalReference;

    @XmlElement(required = true)
    @Getter @Setter
    protected String name;

    @Getter @Setter
    protected String iban;

    @Getter @Setter
    protected String accountNumber;

    @Getter @Setter
    protected String branchCode;

    /**
     * Of type Bank
     */
    @Getter @Setter
    protected OidDto bank;

    @Getter @Setter
    protected String nationalBankCode;

    @Getter @Setter
    protected String nationalCheckCode;

}
