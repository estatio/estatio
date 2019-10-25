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

package org.estatio.module.party.app;

import java.util.Set;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;

import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonGenderType;
import org.estatio.module.party.dom.relationship.PartyRelationship;

@Mixin(method = "act")
public class Party_newRelatedPerson {

    private final Party party;
    public Party_newRelatedPerson(final Party party) {
        this.party = party;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public PartyRelationship newRelatedPerson(
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    regexPattern = Person.ReferenceType.Meta.REGEX,
                    regexPatternReplacement = Person.ReferenceType.Meta.REGEX_DESCRIPTION)
            final String reference,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    regexPattern = Person.InitialsType.Meta.REGEX,
                    regexPatternReplacement = Person.InitialsType.Meta.REGEX_DESCRIPTION
            )
            final String initials,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String firstName,
            final String lastName,
            final PersonGenderType gender,
            final String relationshipType,
            @Parameter(optionality = Optionality.OPTIONAL)
            final String description,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    regexPattern = CommunicationChannel.PhoneNumberType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.PhoneNumberType.Meta.REGEX_DESC
            )
            final String phoneNumber,
            @Parameter(
                    optionality = Optionality.OPTIONAL,
                    maxLength = CommunicationChannel.EmailType.Meta.MAX_LEN,
                    regexPattern = CommunicationChannel.EmailType.Meta.REGEX,
                    regexPatternReplacement = CommunicationChannel.EmailType.Meta.REGEX_DESC
            )
            final String emailAddress
    ) {
        return partyRelationshipMenu
                .newRelatedPerson(this.party, reference, initials, firstName, lastName, gender, relationshipType, description, phoneNumber, emailAddress);
    }

    public Set<String> choices5NewRelatedPerson(
            final String reference,
            final String initials,
            final String firstName,
            final String lastName,
            final PersonGenderType gender,
            final String type,
            final String description,
            final String phoneNumber,
            final String emailAddress) {
        return partyRelationshipMenu
                .choices6NewRelatedPerson(this.party, reference, initials, firstName, lastName, gender, type,description, phoneNumber, emailAddress);
    }

    @Inject
    PartyRelationshipMenu partyRelationshipMenu;
}
