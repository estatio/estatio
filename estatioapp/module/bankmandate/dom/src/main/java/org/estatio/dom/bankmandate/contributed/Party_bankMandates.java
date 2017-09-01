package org.estatio.dom.bankmandate.contributed;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.agreement.AgreementRepository;
import org.estatio.dom.agreement.role.AgreementRoleTypeRepository;
import org.estatio.dom.agreement.type.AgreementTypeRepository;
import org.estatio.dom.bankmandate.BankMandate;
import org.estatio.dom.bankmandate.BankMandateAgreementRoleTypeEnum;
import org.estatio.dom.bankmandate.BankMandateAgreementTypeEnum;
import org.estatio.dom.party.Party;

@Mixin
public class Party_bankMandates {

    private final Party party;

    public Party_bankMandates(Party party) {
        this.party = party;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<BankMandate> $$() {
        return agreementRepository.findByAgreementTypeAndRoleTypeAndParty(
                BankMandateAgreementTypeEnum.MANDATE.findUsing(agreementTypeRepository),
                BankMandateAgreementRoleTypeEnum.DEBTOR.findUsing(agreementRoleTypeRepository),
                party).stream().map(x -> (BankMandate) x).collect(Collectors.toList());
    }

    @Inject AgreementRepository agreementRepository;

    @Inject AgreementTypeRepository agreementTypeRepository;

    @Inject AgreementRoleTypeRepository agreementRoleTypeRepository;

}
