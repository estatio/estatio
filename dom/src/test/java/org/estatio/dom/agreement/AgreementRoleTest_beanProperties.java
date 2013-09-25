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

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.Lockable;
import org.estatio.dom.PojoTester.FilterSet;
import org.estatio.dom.PojoTester.FixtureDatumFactory;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;

public class AgreementRoleTest_beanProperties extends AbstractBeanPropertiesTest {

    @Test
    public void test() {
        final AgreementRole agreement = new AgreementRole();
        newPojoTester()
            .withFixture(pojos(AgreementRoleType.class))
            .withFixture(pojos(Agreement.class, AgreementForTesting.class))
            .withFixture(pojos(Party.class, PartyForTesting.class))
            .withFixture(statii())
            .exercise(agreement, FilterSet.excluding("lockable"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static FixtureDatumFactory<Lockable> statii() {
        return new FixtureDatumFactory(Lockable.class, (Object[])org.estatio.dom.Status.values());
    }

    
}
