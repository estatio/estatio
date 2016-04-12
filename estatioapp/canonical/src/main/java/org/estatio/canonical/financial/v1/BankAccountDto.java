package org.estatio.canonical.financial.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.schema.common.v1.OidDto;

import org.estatio.canonical.HasSelfDto;
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
    "self",
    "ownerParty",
    "reference",
    "externalReference",
    "name",
    "iban",
    "bic",
    "accountNumber",
    "branchCode",
    "bankParty",
    "nationalBankCode",
    "nationalCheckCode"
})
@XmlRootElement(name = "bankAccountDto")
public class BankAccountDto implements VersionedDto, HasSelfDto {

    public BankAccountDto() {}

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
    protected OidDto self;

    @XmlElement(required = true)
    @Getter @Setter
    protected OidDto ownerParty;

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
    protected String bic;

    @Getter @Setter
    protected String accountNumber;

    @Getter @Setter
    protected String branchCode;

    @Getter @Setter
    protected OidDto bankParty;

    @Getter @Setter
    protected String nationalBankCode;

    @Getter @Setter
    protected String nationalCheckCode;

}
