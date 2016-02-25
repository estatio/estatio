package org.estatio.canonical.financial.bankaccount;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.schema.common.v1.OidDto;

import lombok.Getter;
import lombok.Setter;

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

    @Getter @Setter
    protected OidDto bank;

    @Getter @Setter
    protected String nationalBankCode;

    @Getter @Setter
    protected String nationalCheckCode;


}
