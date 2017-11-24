package org.estatio.module.bankmandate.contributions;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.agreement.dom.AgreementRepository;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.bankmandate.dom.BankMandate;
import org.estatio.module.bankmandate.dom.BankMandateAgreementRoleTypeEnum;
import org.estatio.module.bankmandate.dom.BankMandateAgreementTypeEnum;
import org.estatio.module.party.dom.Organisation;

@Mixin(method="act")
public class Organisation_bankMandates {

    private final Organisation organisation;

    public Organisation_bankMandates(Organisation organisation) {
        this.organisation = organisation;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<BankMandate> act() {
        final List<Agreement> agreements = agreementRepository
                .findByAgreementTypeAndRoleTypeAndParty(
                        BankMandateAgreementTypeEnum.MANDATE.findUsing(agreementTypeRepository),
                        BankMandateAgreementRoleTypeEnum.DEBTOR.findUsing(agreementRoleTypeRepository),
                        organisation);
        return agreements.stream()
                .filter(BankMandate.class::isInstance)
                .map(BankMandate.class::cast)
                .collect(Collectors.toList());
    }

    @Inject
    AgreementRepository agreementRepository;

    @Inject
    AgreementTypeRepository agreementTypeRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;

}
