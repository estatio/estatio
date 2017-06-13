package org.estatio.capex.dom.invoice.inference;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.document.dom.impl.docs.Document;

import org.estatio.dom.party.Organisation;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.relationship.PartyRelationship;
import org.estatio.dom.party.relationship.PartyRelationshipRepository;
import org.estatio.dom.party.relationship.PartyRelationshipTypeEnum;
import org.estatio.dom.party.role.PartyRoleMemberInferenceServiceAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleMemberInferenceServiceForDocumentAndPartyRelationship
        extends PartyRoleMemberInferenceServiceAbstract<PartyRelationshipTypeEnum, Document> {

    public PartyRoleMemberInferenceServiceForDocumentAndPartyRelationship() {
        super(Document.class, PartyRelationshipTypeEnum.MAIL_ROOM);
    }

    protected final List<Person> doInfer(
            final PartyRelationshipTypeEnum partyRoleType,
            final Document document) {

        final Organisation buyer = inferOrganisation(document);
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
            final Document document) {

        // we can use the ECP naming convention to infer the buyer for France.
        // from is the Organisation acting as the buyer, eg ECP FR
        // to is the Person in the mailroom
        final String barcode = document.getName();

        return determineFrom(barcode);
    }


    private Organisation determineFrom(final String barcode) {
        // TODO
        return null;
    }


    @Inject
    PartyRelationshipRepository partyRelationshipRepository;

}
