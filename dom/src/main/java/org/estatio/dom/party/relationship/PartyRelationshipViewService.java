/*
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.dom.party.relationship;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.NotContributed;
import org.apache.isis.applib.annotation.NotContributed.As;

import org.estatio.dom.EstatioService;
import org.estatio.dom.party.Party;

@DomainService()
@Hidden
public class PartyRelationshipViewService extends EstatioService<PartyRelationshipViewService> {

    public PartyRelationshipViewService() {
        super(PartyRelationshipViewService.class);
    }

    // //////////////////////////////////////

    @PostConstruct
    public void init() {
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @NotContributed(As.ACTION)
    public List<PartyRelationshipView> relationships(Party party) {
        List<PartyRelationshipView> partyRelationshipViews = new ArrayList<PartyRelationshipView>();
        final List<PartyRelationship> relationships = partyRelationships.findByParty(party);
        for (PartyRelationship pr : relationships) {
            PartyRelationshipView template = getContainer().injectServicesInto(new PartyRelationshipView(pr, party));
            final String viewModelMemento = template.viewModelMemento();
            partyRelationshipViews.add(getContainer().newViewModelInstance(PartyRelationshipView.class, viewModelMemento));
        }
        return partyRelationshipViews;
    }

    @Inject
    private PartyRelationships partyRelationships;

}
