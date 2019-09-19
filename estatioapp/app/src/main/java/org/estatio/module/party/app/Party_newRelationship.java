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
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.relationship.PartyRelationship;

@Mixin(method = "act")
public class Party_newRelationship {

    private final Party fromParty;
    public Party_newRelationship(final Party fromParty) {
        this.fromParty = fromParty;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public PartyRelationship act(
            final Party toParty,
            final String relationshipType,
            final @Parameter(optionality = Optionality.OPTIONAL) String description) {
        return partyRelationshipMenu.newRelationship(fromParty, toParty, relationshipType, description);
    }

    public Set<String> choices1NewRelationship(
            final Party to,
            final String type) {
        return partyRelationshipMenu.choices2NewRelationship(fromParty, to, type);
    }

    public String validateNewRelationship(
            final Party to,
            final String relationshipType,
            final String description) {
        return partyRelationshipMenu.validateNewRelationship(fromParty, to, relationshipType, description);
    }

    @Inject
    PartyRelationshipMenu partyRelationshipMenu;

}
