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
package org.estatio.dom.charge;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.tax.Tax;

public class ChargesTest_newCharge {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private DomainObjectContainer mockContainer;

    private Charges charges;

    private Charge existingCharge;

    private ChargeGroup chargeGroup;
    private Tax tax;

    @Before
    public void setup() {

        chargeGroup = new ChargeGroup();
        tax = new Tax();

        charges = new Charges() {
            @Override
            public Charge findCharge(String reference) {
                return existingCharge;
            }
        };
        charges.setContainer(mockContainer);
    }

    @Test
    public void newCharge_whenDoesNotExist() {
        final Charge charge = new Charge();

        existingCharge = null;

        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(Charge.class);
                will(returnValue(charge));

                oneOf(mockContainer).persist(charge);
            }
        });

        final Charge newCharge = charges.createCharge("CG-REF", "CG-Name", "CG-Description", tax, chargeGroup);
        assertThat(newCharge.getReference(), is("CG-REF"));
        assertThat(newCharge.getName(), is("CG-Name"));
        assertThat(newCharge.getDescription(), is("CG-Description"));
        assertThat(newCharge.getTax(), is(tax));
        assertThat(newCharge.getGroup(), is(chargeGroup));
    }

    @Test
    public void newCharge_whenDoesExist() {
        existingCharge = new Charge();

        final Charge newCharge = charges.createCharge("CG-REF", "Some other description", "Some other code", null, null);
        assertThat(newCharge, is(existingCharge));
    }

}
