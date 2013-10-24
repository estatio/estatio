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

import com.google.common.base.Objects;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementTest_newRole_action  {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    @Mock
    private DomainObjectContainer mockContainer;
    
    private AgreementRoleType art;
    private Party party;
    
    private Agreement agreement;

    private LocalDate startDate;
    private LocalDate endDate;

    
    @Before
    public void setUp() throws Exception {
        art = new AgreementRoleType();
        party = new PartyForTesting();

        startDate = new LocalDate(2013,4,1);
        endDate = new LocalDate(2023,3,30);

        agreement = new AgreementForTesting();
        agreement.setContainer(mockContainer);
    }
    
    @Test
    public void newRole() {

        final AgreementRole agreementRole = new AgreementRole();
        final Sequence sequence = context.sequence("newRole");
        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(AgreementRole.class);
                inSequence(sequence);
                will(returnValue(agreementRole));
                
                oneOf(mockContainer).persistIfNotAlready(with(initialized(agreementRole)));
                
            }

            private Matcher<AgreementRole> initialized(final AgreementRole agreementRole) {
                return new TypeSafeMatcher<AgreementRole>() {

                    @Override
                    public void describeTo(Description description) {
                        description.appendText("correctly initialized");
                    }

                    @Override
                    protected boolean matchesSafely(AgreementRole item) {
                        return item.getParty() == party &&
                               item.getType() == art &&
                               item.getAgreement() == agreement &&
                               Objects.equal(item.getStartDate(), startDate) &&
                               Objects.equal(item.getEndDate(), endDate);
                    }
                };
            }
        });
        
        Agreement x = agreement.newRole(art, party, startDate, endDate);
        assertThat(x, is(agreement));
    }

    
}
