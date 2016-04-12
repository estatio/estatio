package org.estatio.canonical.bankmandate.v1;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.estatio.canonical.VersionedDto;
import org.estatio.canonical.financial.v1.BankAccountDto;

import lombok.Getter;
import lombok.Setter;

/**
 * Designed to be usable both as a view model (exposed from RO) and within the Camel ESB.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "majorVersion",
    "minorVersion",
    "bankAccounts",
    "bankMandates"
})
@XmlRootElement(name = "bankAccountsAndMandatesDto")
public class BankAccountsAndMandatesDto implements VersionedDto {

    @XmlElement(required = true, defaultValue = "1")
    public final String getMajorVersion() {
        return "1";
    }

    @XmlElement(required = true, defaultValue = "0")
    public String getMinorVersion() {
        return "0";
    }

    @XmlElementWrapper
    @XmlElement(required = true)
    @Getter @Setter
    protected List<BankAccountDto> bankAccounts;

    @XmlElementWrapper
    @XmlElement(required = true)
    @Getter @Setter
    protected List<BankMandateDto> bankMandates;

}
