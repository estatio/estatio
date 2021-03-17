package org.estatio.module.financial.canonical.v2;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.canonical.financial.v2.BankAccountDto;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.financial.dom.BankAccount;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "financial.canonical.v2.BankAccountDtoFactory"
)
public class BankAccountDtoFactory extends DtoFactoryAbstract<BankAccount, BankAccountDto> {

    public BankAccountDtoFactory(){
        super(BankAccount.class, BankAccountDto.class);
    }

    @Programmatic
    public BankAccountDto newDto(final BankAccount bankAccount) {
        final BankAccountDto dto = new BankAccountDto();
        dto.setMajorVersion("2");
        dto.setMinorVersion("0");

        dto.setSelf(mappingHelper.oidDtoFor(bankAccount));
        dto.setAtPath(bankAccount.getAtPath());

        dto.setAccountNumber(bankAccount.getAccountNumber());
        dto.setBankParty(mappingHelper.oidDtoFor(bankAccount.getBank()));
        dto.setBranchCode(bankAccount.getBranchCode());
        dto.setExternalReference(bankAccount.getExternalReference());
        dto.setIban(bankAccount.getIban());
        dto.setBic(bankAccount.getBic());
        dto.setName(bankAccount.getName());
        dto.setNationalBankCode(bankAccount.getNationalBankCode());
        dto.setNationalCheckCode(bankAccount.getNationalCheckCode());
        dto.setOwnerParty(mappingHelper.oidDtoFor(bankAccount.getOwner()));
        dto.setReference(bankAccount.getReference());
        return dto;
    }

}
