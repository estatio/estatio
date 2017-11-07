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
package org.estatio.dom.party;

import org.assertj.core.api.Assertions;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.dom.party.role.PartyRole;
import org.estatio.dom.party.role.PartyRoleType;
import org.estatio.dom.party.role.PartyRoleTypeRepository;

public class Party_Test {
    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new PartyForTesting());
        }
    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock PartyRoleTypeRepository mockPartyRoleTypeRepository;

    @Test
    public void validate_delete_works() throws Exception {

        // given
        PartyRoleType typeForTenant = new PartyRoleType();
        PartyRole roleForTenant = new PartyRole();
        roleForTenant.setRoleType(typeForTenant);
        Party partyToDelete = new PartyForTesting();
        partyToDelete.getRoles().add(roleForTenant); // party to delete now has tenant role
        partyToDelete.partyRoleTypeRepository = mockPartyRoleTypeRepository;


        PartyRoleType typeForSupplier = new PartyRoleType();
        PartyRole roleForSupplier = new PartyRole();
        roleForSupplier.setRoleType(typeForSupplier);
        Party replacementParty = new PartyForTesting();
        replacementParty.getRoles().add(roleForSupplier); // replacement party now has supplier role but NOT tenant role
        replacementParty.partyRoleTypeRepository = mockPartyRoleTypeRepository;

        // expect
        context.checking(new Expectations(){{
            oneOf(mockPartyRoleTypeRepository).findByKey("TENANT");
            will(returnValue(typeForTenant));
            oneOf(mockPartyRoleTypeRepository).findByKey("SUPPLIER");
            will(returnValue(typeForSupplier));
            oneOf(mockPartyRoleTypeRepository).findByKey("TENANT");
            will(returnValue(null));
        }});

        // when, then
        Assertions.assertThat(partyToDelete.validateDelete(replacementParty)).isEqualTo("A tenant should not be replaced by a supplier");


    }
}