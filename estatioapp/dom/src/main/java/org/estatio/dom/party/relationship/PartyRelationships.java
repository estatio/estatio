package org.estatio.dom.party.relationship;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.core.runtime.authentication.standard.RandomCodeGenerator10Chars;

import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainRepositoryAndFactory;
import org.estatio.dom.communicationchannel.CommunicationChannelRepository;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.dom.party.Persons;

@DomainServiceLayout(
        named = "Parties",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "20.4")
@DomainService(repositoryFor = PartyRelationship.class, nature = NatureOfService.VIEW)
public class PartyRelationships extends UdoDomainRepositoryAndFactory<PartyRelationship> {

    public PartyRelationships() {
        super(PartyRelationships.class, PartyRelationship.class);
    }

    @Programmatic
    public List<PartyRelationship> findByParty(Party party) {
        return allMatches("findByParty", "party", party);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
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

    public Set<String> choices2NewRelationship(
            final Party from,
            final Party to,
            final String type) {
        return PartyRelationshipType.toTitlesFor(
                from == null ? null : from.getClass(),
                to == null ? null : to.getClass());
    }

    public String validateNewRelationship(
            final Party from,
            final Party to,
            final String relationshipType,
            final String description) {
        if (from.equals(to)) {
            return "Cannot create relationship to itself";
        }
        return null;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
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
        Person person = persons.newPerson(newReference, initials, firstName, lastName, gender, party.getApplicationTenancy());
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            communicationChannelRepository.newPhoneOrFax(person, CommunicationChannelType.PHONE_NUMBER, phoneNumber);
        }
        if (emailAddress != null && !emailAddress.isEmpty()) {
            communicationChannelRepository.newEmail(person, CommunicationChannelType.EMAIL_ADDRESS, emailAddress);
        }
        return newRelationship(party, person, relationshipType, description);
    }

    public Set<String> choices6NewRelatedPerson(
            final Party from,
            final String reference,
            final String initials,
            final String firstName,
            final String lastName,
            final PersonGenderType gender,
            final String type,
            final String description,
            final String phoneNumber,
            final String emailAddress) {
        return PartyRelationshipType.toTitlesFor(from.getClass(), Person.class);
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "99")
    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
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

    // //////////////////////////////////////

    @Inject
    private Persons persons;

    @Inject
    private CommunicationChannelRepository communicationChannelRepository;
}
