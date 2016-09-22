/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.estatio.app.menus.party;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.RegexValidation;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.PersonGenderType;
import org.estatio.dom.party.relationship.PartyRelationship;
import org.estatio.dom.party.relationship.PartyRelationshipRepository;
import org.estatio.dom.party.relationship.PartyRelationshipType;

@DomainService(
        nature = NatureOfService.VIEW
)
@DomainServiceLayout(
        named = "Parties",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "20.4")
public class PartyRelationshipMenu {



    // seemingly also contributed to Party...
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public PartyRelationship newRelationship(
            final Party fromParty,
            final Party toParty,
            final String relationshipType,
            final @Parameter(optionality = Optionality.OPTIONAL) String description) {
        return partyRelationshipRepository.newRelationship(fromParty, toParty, relationshipType, description);
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



    // seemingly also contributed to Party...
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
        return partyRelationshipRepository.newRelatedPerson(party, reference, initials, firstName, lastName, gender, relationshipType, description, phoneNumber, emailAddress);
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
        return partyRelationshipRepository.allRelationships();
    }

    // //////////////////////////////////////

    @Inject
    PartyRelationshipRepository partyRelationshipRepository;

}
