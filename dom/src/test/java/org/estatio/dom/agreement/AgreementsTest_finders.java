/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.dom.agreement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.query.Query;
import org.apache.isis.core.commons.matchers.IsisMatchers;

import org.estatio.dom.FinderInteraction;
import org.estatio.dom.FinderInteraction.FinderMethod;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.asset.Property;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementsTest_finders {

    private FinderInteraction finderInteraction;

    private Agreements agreements;

    private AgreementType agreementType;
    private AgreementRoleType agreementRoleType;
    private Party party;

    @Before
    public void setup() {
        
        agreementType = new AgreementType();
        agreementRoleType = new AgreementRoleType();
        party = new PartyForTesting();
        
        agreements = new Agreements() {

            @Override
            protected <T> T firstMatch(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.FIRST_MATCH);
                return null;
            }
            @Override
            protected List<Agreement> allInstances() {
                finderInteraction = new FinderInteraction(null, FinderMethod.ALL_INSTANCES);
                return null;
            }

            @Override
            protected <T> List<T> allMatches(Query<T> query) {
                finderInteraction = new FinderInteraction(query, FinderMethod.ALL_MATCHES);
                return null;
            }
        };
    }


    @Test
    public void findAgreementByReference() {

        agreements.findAgreementByReference("*some?Reference*");
        
        // then
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.FIRST_MATCH));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Agreement.class));
        assertThat(finderInteraction.getQueryName(), is("findByReference"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("reference"), is((Object)".*some.Reference.*"));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(1));
    }
    
    @Test
    public void findByAgreementTypeAndRoleTypeAndParty() {

        agreements.findByAgreementTypeAndRoleTypeAndParty(agreementType, agreementRoleType, party);
        
        // then
        assertThat(finderInteraction.getFinderMethod(), is(FinderMethod.ALL_MATCHES));
        assertThat(finderInteraction.getResultType(), IsisMatchers.classEqualTo(Agreement.class));
        assertThat(finderInteraction.getQueryName(), is("findByAgreementTypeAndRoleTypeAndParty"));
        assertThat(finderInteraction.getArgumentsByParameterName().get("agreementType"), is((Object)agreementType));
        assertThat(finderInteraction.getArgumentsByParameterName().get("roleType"), is((Object)agreementRoleType));
        assertThat(finderInteraction.getArgumentsByParameterName().get("party"), is((Object)party));
        assertThat(finderInteraction.getArgumentsByParameterName().size(), is(3));
    }


}
