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

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyRepository;

@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.app.menus.party.PartyMenu"
)
@DomainServiceLayout(
        named = "Parties",
        menuBar = DomainServiceLayout.MenuBar.PRIMARY,
        menuOrder = "20.1"
)
public class PartyMenu {

    @Action(semantics = SemanticsOf.SAFE)
    @MemberOrder(sequence = "1")
    public List<Party> findParties(
            final @ParameterLayout(named = "Reference or Name", describedAs = "May include wildcards '*' and '?'") String referenceOrName) {
        return partyRepository.findParties(referenceOrName);
    }

    @Action(semantics = SemanticsOf.SAFE, restrictTo = RestrictTo.PROTOTYPING)
    @MemberOrder(sequence = "99")
    public List<Party> allParties() {
        return partyRepository.allParties();
    }

    // //////////////////////////////////////

    @Inject
    PartyRepository partyRepository;

}
