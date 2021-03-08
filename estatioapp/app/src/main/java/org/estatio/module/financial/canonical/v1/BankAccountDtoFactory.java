package org.estatio.module.financial.canonical.v1;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.dto.DtoMappingHelper;

import org.estatio.canonical.financial.v1.BankAccountDto;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.financial.dom.BankAccount;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class BankAccountDtoFactory extends DtoFactoryAbstract<BankAccount, BankAccountDto> {

    public BankAccountDtoFactory(){
        super(BankAccount.class, BankAccountDto.class);
    }

    /**
     * for testing.
     */
    public BankAccountDtoFactory(DtoMappingHelper mappingHelper) {
        this();
        this.mappingHelper = mappingHelper;
    }

    @Programmatic
    public BankAccountDto newDto(final BankAccount bankAccount) {
        final BankAccountDto dto = new BankAccountDto();

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
