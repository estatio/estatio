package org.estatio.module.bankmandate.canonical.v2;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.dto.DtoMappingHelper;

import org.estatio.canonical.bankaccountsandmandates.v2.BankAccountsAndMandatesDto;
import org.estatio.canonical.bankmandate.v2.BankMandateType;
import org.estatio.canonical.financial.v2.BankAccountType;
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

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "bankmandate.canonical.v2.PartyBankAccountsAndMandatesDtoFactory"
)
public class PartyBankAccountsAndMandatesDtoFactory extends DtoFactoryAbstract {

    @Programmatic
    public BankAccountsAndMandatesDto newDto(final Party party) {

        final BankAccountsAndMandatesDto dto = new BankAccountsAndMandatesDto();

        final List<FinancialAccount> financialAccountList = financialAccountRepository.findAccountsByTypeOwner(FinancialAccountType.BANK_ACCOUNT, party);
        final List<BankAccountType> bankAccountDtos =
                financialAccountList.stream()
                        .map(x -> bankAccountDtoFactory.newDto((BankAccount) x))
                        .map(BankAccountType.class::cast)
                        .collect(Collectors.toList());

        dto.setBankAccounts(bankAccountDtos);

        final AgreementType bankMandateAt = agreementTypeRepository.find(BankMandateAgreementTypeEnum.MANDATE);
        final AgreementRoleType debtorOfMandate = agreementRoleTypeRepository.findByAgreementTypeAndTitle(bankMandateAt, BankMandateAgreementRoleTypeEnum.DEBTOR.getTitle());
        final List<AgreementRole> agreementRoles = agreementRoleRepository.findByPartyAndType(party, debtorOfMandate);

        final List<BankMandateType> mandateDtos =
                agreementRoles.stream()
                        .map(AgreementRole::getAgreement)
                        .map(x -> bankMandateDtoFactory.newDto((BankMandate) x))
                        .map(BankMandateType.class::cast)
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
