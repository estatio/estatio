package org.estatio.dom.party.relationship;

import java.util.List;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.communicationchannel.CommunicationChannelRepository;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.dom.party.PersonRepository;

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
    public PartyRelationship newRelationship(
            final Party fromParty,
            final Party toParty,
            final String relationshipType,
            final @Parameter(optionality = Optionality.OPTIONAL) String description) {
        PartyRelationship relationship = getContainer().injectServicesInto(PartyRelationshipType.createWithToTitle(fromParty, toParty, relationshipType));
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
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = RegexValidation.Person.REFERENCE, regexPatternReplacement = RegexValidation.Person.REFERENCE_DESCRIPTION) String reference,
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = RegexValidation.Person.INITIALS, regexPatternReplacement = RegexValidation.Person.INITIALS_DESCRIPTION) String initials,
            final @Parameter(optionality = Optionality.OPTIONAL) String firstName,
            final String lastName,
            final PersonGenderType gender,
            final String relationshipType,
            final @Parameter(optionality = Optionality.OPTIONAL) String description,
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = RegexValidation.CommunicationChannel.PHONENUMBER, regexPatternReplacement = RegexValidation.CommunicationChannel.PHONENUMBER_DESCRIPTION) String phoneNumber,
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = RegexValidation.CommunicationChannel.EMAIL, regexPatternReplacement = RegexValidation.CommunicationChannel.EMAIL_DESCRIPTION) String emailAddress
    ) {

        RandomCodeGenerator10Chars generator = new RandomCodeGenerator10Chars();
        String newReference = reference == null ? generator.generateRandomCode().toUpperCase() : reference;
        Person person = personRepository.newPerson(newReference, initials, firstName, lastName, gender, party.getApplicationTenancy());
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

    @Subscribe
    @Programmatic
    public void on(final Party.RemoveEvent ev) {
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
    private PersonRepository personRepository;

    @Inject
    private CommunicationChannelRepository communicationChannelRepository;
}
