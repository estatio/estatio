package org.estatio.capex.dom.invoice.inference;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.dom.financial.bankaccount.BankAccount;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.relationship.PartyRelationship;
import org.estatio.dom.party.relationship.PartyRelationshipRepository;
import org.estatio.dom.party.relationship.PartyRelationshipTypeEnum;
import org.estatio.dom.party.role.PartyRoleMemberInferenceServiceAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleMemberInferenceServiceForBankAccountAndPartyRelationship
        extends PartyRoleMemberInferenceServiceAbstract<PartyRelationshipTypeEnum, BankAccount> {

    public PartyRoleMemberInferenceServiceForBankAccountAndPartyRelationship() {
        super(BankAccount.class,
                PartyRelationshipTypeEnum.TREASURER
                );
    }

    protected final List<Person> doInfer(
            final PartyRelationshipTypeEnum partyRoleType,
            final BankAccount supplierBankAccount) {

        final Organisation buyer = inferOrganisation(supplierBankAccount);
        if(buyer == null) {
            return null;
        }

        final List<PartyRelationship> relationships =
                partyRelationshipRepository.findCurrentByFromAndType(buyer, partyRoleType);
        return relationships.stream()
                .filter(Person.class::isInstance)
                .map(Person.class::cast)
                .collect(Collectors.toList());
    }

    private Organisation inferOrganisation(final BankAccount supplierBankAccount) {

        // TODO ...
        // how get from the supplier's bank account to the org of the corresponding buyer
        // to determine the ECP buyer and therefore the treasurer handling that buyer's bank accounts?
        return null;
    }

    @Inject
    PartyRelationshipRepository partyRelationshipRepository;

}
