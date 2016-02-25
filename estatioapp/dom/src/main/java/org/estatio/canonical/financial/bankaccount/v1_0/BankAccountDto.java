package org.estatio.canonical.financial.bankaccount.v1_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.schema.common.v1.OidDto;

import org.estatio.dom.DtoMappingHelper;
import org.estatio.dom.financial.bankaccount.BankAccount;

import lombok.Getter;
import lombok.Setter;

/**
 * Designed to be usable both as a view model and in an ESB (Camel).
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
    public BankAccountDto(final BankAccount bankAccount, final DtoMappingHelper mappingHelper) {
        setAccountNumber(bankAccount.getAccountNumber());
        setBank(mappingHelper.oidDtoFor(bankAccount.getBank()));
        setBranchCode(bankAccount.getBranchCode());
        setExternalReference(bankAccount.getExternalReference());
        setIban(bankAccount.getIban());
        setName(bankAccount.getName());
        setNationalBankCode(bankAccount.getNationalBankCode());
        setNationalCheckCode(bankAccount.getNationalCheckCode());
        setOwner(mappingHelper.oidDtoFor(bankAccount.getOwner()));
        setReference(bankAccount.getReference());
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

    @Getter @Setter
    protected OidDto bank;

    @Getter @Setter
    protected String nationalBankCode;

    @Getter @Setter
    protected String nationalCheckCode;


}
