/*
 *
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
package org.estatio.dom.agreement;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

public class AgreementRoleTypeTest {

    AgreementRoleType art;

    public static class ApplicableTo extends AgreementRoleTypeTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

        private AgreementType at;

        @Mock
        private AgreementRoleTypeRepository mockAgreementRoleTypeRepository;

        @Before
        public void setUp() throws Exception {
            at = new AgreementType();
            at.agreementRoleTypeRepository = mockAgreementRoleTypeRepository;

            art = new AgreementRoleType();
            art.setAppliesTo(at);
        }

        @Test
        public void delegatesToService() {
            context.checking(new Expectations() {
                                 {
                                     oneOf(mockAgreementRoleTypeRepository).findApplicableTo(at);
                                 }
                             }
            );

            AgreementRoleType.applicableTo(at);
        }
    }

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final AgreementRoleType agreementRoleType = new AgreementRoleType();
            newPojoTester()
                    .withFixture(pojos(AgreementType.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(agreementRoleType);
        }

    }

}