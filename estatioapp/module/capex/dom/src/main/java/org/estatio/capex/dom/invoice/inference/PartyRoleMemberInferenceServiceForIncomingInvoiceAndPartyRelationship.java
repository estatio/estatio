package org.estatio.capex.dom.invoice.inference;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.relationship.PartyRelationship;
import org.estatio.dom.party.relationship.PartyRelationshipRepository;
import org.estatio.dom.party.relationship.PartyRelationshipTypeEnum;
import org.estatio.dom.party.role.PartyRoleMemberInferenceServiceAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleMemberInferenceServiceForIncomingInvoiceAndPartyRelationship
        extends PartyRoleMemberInferenceServiceAbstract<PartyRelationshipTypeEnum, IncomingInvoice> {

    public PartyRoleMemberInferenceServiceForIncomingInvoiceAndPartyRelationship() {
        super(IncomingInvoice.class,
                PartyRelationshipTypeEnum.MAIL_ROOM,
                PartyRelationshipTypeEnum.COUNTRY_ADMINISTRATOR,
                PartyRelationshipTypeEnum.COUNTRY_DIRECTOR,
                PartyRelationshipTypeEnum.TREASURER
                );
    }

    protected final List<Person> doInfer(
            final PartyRelationshipTypeEnum partyRoleType,
            final IncomingInvoice incomingInvoice) {

        final Organisation buyer = inferOrganisation(incomingInvoice);
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

    private Organisation inferOrganisation(
            final IncomingInvoice incomingInvoice) {

        final Party party = incomingInvoice.getBuyer();
        return party instanceof Organisation
                ? (Organisation) party
                : null;
    }


    @Inject
    PartyRelationshipRepository partyRelationshipRepository;

}
