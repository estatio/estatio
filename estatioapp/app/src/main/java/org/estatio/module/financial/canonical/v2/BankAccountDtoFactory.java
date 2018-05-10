package org.estatio.module.financial.canonical.v2;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.dto.DtoMappingHelper;

import org.estatio.canonical.financial.v2.BankAccountDto;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.financial.dom.BankAccount;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "financial.canonical.v2.BankAccountDtoFactory"
)
public class BankAccountDtoFactory extends DtoFactoryAbstract {

    @Programmatic
    public BankAccountDto newDto(final BankAccount bankAccount) {
        final BankAccountDto dto = new BankAccountDto();

        dto.setSelf(mappingHelper.oidDtoFor(bankAccount));
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

    @Inject
    DtoMappingHelper mappingHelper;
}
