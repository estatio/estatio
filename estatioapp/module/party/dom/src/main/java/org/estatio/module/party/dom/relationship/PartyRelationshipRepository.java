package org.estatio.module.party.dom.relationship;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;

import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.module.party.dom.Organisation;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonGenderType;
import org.estatio.module.party.dom.PersonRepository;

@DomainService(repositoryFor = PartyRelationship.class, nature = NatureOfService.DOMAIN)
public class PartyRelationshipRepository extends UdoDomainRepositoryAndFactory<PartyRelationship> {

    public PartyRelationshipRepository() {
        super(PartyRelationshipRepository.class, PartyRelationship.class);
    }

    @Programmatic
    public List<PartyRelationship> findByParty(Party party) {
        return allMatches("findByParty", "party", party);
    }

    @Programmatic
    public List<PartyRelationship> findCurrentByFromAndType(
            final Organisation from,
            final PartyRelationshipTypeEnum relationshipType) {
        LocalDate now = clockService.now();
        return allMatches("findByFromAndTypeAndBetweenStartDateAndEndDate",
                "from", from,
                "relationshipType", relationshipType,
                "date", now);
    }


    @Programmatic
    public PartyRelationship newRelationship(
            final Party fromParty,
            final Party toParty,
            final String relationshipType,
            final @Parameter(optionality = Optionality.OPTIONAL) String description) {
        PartyRelationship relationship = getContainer().injectServicesInto(
                PartyRelationshipTypeEnum.createWithToTitle(fromParty, toParty, relationshipType));
        relationship.setFrom(fromParty);
        relationship.setTo(toParty);
        relationship.setDescription(description);
        persistIfNotAlready(relationship);
        return relationship;
    }

    // //////////////////////////////////////

    @Programmatic
    public PartyRelationship newRelatedPerson(
            final Party party,
            final String reference,
            final String initials,
            final String firstName,
            final String lastName,
            final PersonGenderType gender,
            final String relationshipType,
            final String description,
            final String phoneNumber,
            final String emailAddress
    ) {

        RandomCodeGenerator10Chars generator = new RandomCodeGenerator10Chars();
        String newReference = reference == null ? generator.generateRandomCode().toUpperCase() : reference;
        final ApplicationTenancy applicationTenancy = party.getApplicationTenancy();

        Person person = personRepository.newPerson(newReference, initials, firstName, lastName, gender, applicationTenancy);
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            communicationChannelRepository.newPhoneOrFax(person, CommunicationChannelType.PHONE_NUMBER, phoneNumber);
        }
        if (emailAddress != null && !emailAddress.isEmpty()) {
            communicationChannelRepository.newEmail(person, CommunicationChannelType.EMAIL_ADDRESS, emailAddress);
        }
        return newRelationship(party, person, relationshipType, description);
    }

    // //////////////////////////////////////

    @Programmatic
    public List<PartyRelationship> allRelationships() {
        return allInstances();
    }

    // //////////////////////////////////////

    @Programmatic
    @com.google.common.eventbus.Subscribe
    @org.axonframework.eventhandling.annotation.EventHandler
    public void on(final Party.DeleteEvent ev) {
        Party sourceParty = ev.getSource();
        Party replacementParty = ev.getReplacement();

        switch (ev.getEventPhase()) {
        case VALIDATE:
            break;
        case EXECUTING:
            for (PartyRelationship partyRelationship : findByParty(sourceParty)) {
                if (replacementParty == null) {
                    // Remove relationships when no replacement is provided
                    partyRelationship.doRemove();
                } else {
                    if (partyRelationship.getFrom().equals(sourceParty)) {
                        partyRelationship.setFrom(replacementParty);
                    }
                    if (partyRelationship.getTo().equals(sourceParty)) {
                        partyRelationship.setTo(replacementParty);
                    }
                }
            }
            break;
        default:
            break;
        }
    }

    /**
     * Stolen from org.apache.isis.core.runtime.authentication.standard...
     */
    public static class RandomCodeGenerator10Chars {

        private static final int NUMBER_CHARACTERS = 10;
        private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        public String generateRandomCode() {
            final StringBuilder buf = new StringBuilder(NUMBER_CHARACTERS);
            for (int i = 0; i < NUMBER_CHARACTERS; i++) {
                final int pos = (int) ((Math.random() * CHARACTERS.length()));
                buf.append(CHARACTERS.charAt(pos));
            }
            return buf.toString();
        }

    }

    // //////////////////////////////////////

    @Inject
    PersonRepository personRepository;

    @Inject
    CommunicationChannelRepository communicationChannelRepository;

    @Inject
    ClockService clockService;
}
