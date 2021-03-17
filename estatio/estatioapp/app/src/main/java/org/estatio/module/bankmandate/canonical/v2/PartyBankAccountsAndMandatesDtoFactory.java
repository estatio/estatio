package org.estatio.module.bankmandate.canonical.v2;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.canonical.bankaccountsandmandates.v2.BankAccountsAndMandatesDto;
import org.estatio.canonical.bankmandate.v2.BankMandateType;
import org.estatio.canonical.financial.v2.BankAccountType;
import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementRole;
import org.estatio.module.agreement.dom.AgreementRoleRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementType;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.BankMandateAgreementRoleTypeEnum;
import org.estatio.module.bankmandate.dom.BankMandateAgreementTypeEnum;
import org.estatio.module.base.platform.applib.DtoFactoryAbstract;
import org.estatio.module.financial.canonical.v2.BankAccountDtoFactory;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.financial.dom.FinancialAccountType;
import org.estatio.module.party.dom.Party;

/**
 * This code is currently (as of EST-1854) unused - we have removed the ability to sync arbitrary Parties (with all of
 * their Bank accounts and bank mandates) because it's no longer safe to assume that the party is a tenant; it could be
 * a supplier.
 */
@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "bankmandate.canonical.v2.PartyBankAccountsAndMandatesDtoFactory"
)
public class PartyBankAccountsAndMandatesDtoFactory extends DtoFactoryAbstract<Party, BankAccountsAndMandatesDto> {

    public PartyBankAccountsAndMandatesDtoFactory() {
        super(Party.class, BankAccountsAndMandatesDto.class);
    }

    @Programmatic
    public BankAccountsAndMandatesDto newDto(final Party party) {

        final BankAccountsAndMandatesDto dto = new BankAccountsAndMandatesDto();
        dto.setMajorVersion("2");
        dto.setMinorVersion("0");

        final List<FinancialAccount> bankAccounts = findBankAccountsFor(party);
        final List<BankAccountType> bankAccountDtos = asBankAccountDtos(bankAccounts);
        dto.setBankAccounts(bankAccountDtos);

        final List<Agreement> bankMandates = findBankMandatesFor(party);
        final List<BankMandateType> mandateDtos = asBankMandateDtos(bankMandates);
        dto.setBankMandates(mandateDtos);

        return dto;
    }

    private List<FinancialAccount> findBankAccountsFor(final Party party) {
        return financialAccountRepository.findAccountsByTypeOwner(FinancialAccountType.BANK_ACCOUNT, party);
    }

    private List<BankAccountType> asBankAccountDtos(final List<FinancialAccount> bankAccounts) {
        return bankAccounts.stream()
                .map(BankAccount.class::cast)
                .map(x -> bankAccountDtoFactory.newDto(x))
                .map(BankAccountType.class::cast)
                .collect(Collectors.toList());
    }

    private List<Agreement> findBankMandatesFor(final Party party) {
        final AgreementType bankMandateAt =
                agreementTypeRepository.find(BankMandateAgreementTypeEnum.MANDATE);
        final AgreementRoleType debtorOfMandate =
                agreementRoleTypeRepository.findByAgreementTypeAndTitle(
                        bankMandateAt, BankMandateAgreementRoleTypeEnum.DEBTOR.getTitle());
        final List<AgreementRole> agreementRoles =
                agreementRoleRepository.findByPartyAndType(party, debtorOfMandate);

        return agreementRoles.stream().map(AgreementRole::getAgreement).collect(Collectors.toList());
    }

    private List<BankMandateType> asBankMandateDtos(final List<Agreement> bankMandates) {
        return bankMandates.stream()
                .map(BankMandate.class::cast)
                .map(x -> bankMandateDtoFactory.newDto(x))
                .map(BankMandateType.class::cast)
                .collect(Collectors.toList());
    }

    @Inject
    FinancialAccountRepository financialAccountRepository;
    @Inject
    BankAccountDtoFactory bankAccountDtoFactory;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;
    @Inject
    AgreementTypeRepository agreementTypeRepository;
    @Inject
    AgreementRoleRepository agreementRoleRepository;
    @Inject
    BankMandateDtoFactory bankMandateDtoFactory;

}
