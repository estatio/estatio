package org.estatio.dom.bankmandate.canonical.v1;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.dto.DtoFactoryAbstract;
import org.estatio.canonical.bankmandate.v1.BankAccountsAndMandatesDto;
import org.estatio.canonical.bankmandate.v1.BankMandateDto;
import org.estatio.canonical.financial.v1.BankAccountDto;
import org.estatio.agreement.dom.AgreementRole;
import org.estatio.agreement.dom.AgreementRoleRepository;
import org.estatio.agreement.dom.AgreementRoleType;
import org.estatio.agreement.dom.AgreementRoleTypeRepository;
import org.estatio.agreement.dom.AgreementType;
import org.estatio.agreement.dom.AgreementTypeRepository;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.BankMandateConstants;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountRepository;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.financial.bankaccount.canonical.v1.BankAccountDtoFactory;
import org.estatio.dom.party.Party;

@DomainService(
        nature = NatureOfService.DOMAIN
)
public class PartyBankAccountsAndMandatesDtoFactory extends DtoFactoryAbstract {

    @Programmatic
    public BankAccountsAndMandatesDto newDto(final Party party) {

        final BankAccountsAndMandatesDto dto = new BankAccountsAndMandatesDto();

        final List<FinancialAccount> financialAccountList = financialAccountRepository.findAccountsByTypeOwner(FinancialAccountType.BANK_ACCOUNT, party);
        final List<BankAccountDto> bankAccountDtos =
                financialAccountList.stream()
                        .map(x -> bankAccountDtoFactory.newDto((BankAccount) x))
                        .collect(Collectors.toList());

        dto.setBankAccounts(bankAccountDtos);

        final AgreementType bankMandateAt = agreementTypeRepository.find(BankMandateConstants.AT_MANDATE);
        final AgreementRoleType debtorOfMandate = agreementRoleTypeRepository.findByAgreementTypeAndTitle(bankMandateAt, BankMandateConstants.ART_DEBTOR);
        final List<AgreementRole> agreementRoles = agreementRoleRepository.findByPartyAndType(party, debtorOfMandate);

        final List<BankMandateDto> mandateDtos =
                agreementRoles.stream()
                        .map(x -> x.getAgreement())
                        .map(x -> bankMandateDtoFactory.newDto((BankMandate) x))
                        .collect(Collectors.toList());

        dto.setBankMandates(mandateDtos);

        return dto;
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
