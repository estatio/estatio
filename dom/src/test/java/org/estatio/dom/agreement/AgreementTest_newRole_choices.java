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

import com.google.common.collect.Lists;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.Status;

public class AgreementTest_newRole_choices  {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    
    private AgreementType art;
    private Agreement<Status> agreement;

    @Mock
    private AgreementRoleTypes mockAgreementRoleTypes;

    @Before
    public void setUp() throws Exception {
        
        art = new AgreementType();

        agreement = new AgreementForTesting();
        agreement.setAgreementType(art);
        agreement.injectAgreementRoleTypes(mockAgreementRoleTypes);
    }
    
    @Test
    public void choices() {
        final List<AgreementRoleType> list = Lists.newArrayList();
        context.checking(new Expectations() {
            {
                oneOf(mockAgreementRoleTypes).findApplicableTo(art);
                will(returnValue(list));
            }
        });
        
        assertThat(agreement.choices0NewRole(), is(list));;
    }

    
}
