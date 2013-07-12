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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementTest_inject {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private Agreements mockAgreements;
    @Mock
    private AgreementRoleTypes mockAgreementRoleTypes;
    @Mock
    private AgreementTypes mockAgreementTypes;
    @Mock
    private AgreementRoles mockAgreementRoles;
    
    private Agreement<?> agreement;

    @Before
    public void setup() {
        agreement = new AgreementForTesting();
    }

    @Test
    public void injected() {
        agreement.injectAgreementTypes(mockAgreementTypes);
        agreement.injectAgreementRoleTypes(mockAgreementRoleTypes);
        agreement.injectAgreements(mockAgreements);
        agreement.injectAgreementRoles(mockAgreementRoles);
        
        assertThat(agreement.agreements, is(mockAgreements));
        assertThat(agreement.agreementRoles, is(mockAgreementRoles));
        assertThat(agreement.agreementRoleTypes, is(mockAgreementRoleTypes));
        assertThat(agreement.agreementTypes, is(mockAgreementTypes));
    }
    

}
