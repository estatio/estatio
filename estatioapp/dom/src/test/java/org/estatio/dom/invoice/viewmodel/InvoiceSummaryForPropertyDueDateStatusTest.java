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
package org.estatio.dom.invoice.viewmodel;

import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.estatio.dom.asset.Property;
import org.estatio.dom.asset.PropertyRepository;

public class InvoiceSummaryForPropertyDueDateStatusTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            final InvoiceSummaryForPropertyDueDateStatus pojo = new InvoiceSummaryForPropertyDueDateStatus();
            newPojoTester()
                    .withFixture(pojos(Property.class))
                    .exercise(pojo);
        }
    }

    public static class GetProperty extends InvoiceSummaryForPropertyDueDateStatusTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Mock
        PropertyRepository mockPropertyRepository;

        private Property property;

        private InvoiceSummaryForPropertyDueDateStatus summary;

        //        @Before
        //        public void setUp() throws Exception {
        //            property = new Property();
        //            property.setReference("REF-1");
        //
        //            summary = new InvoiceSummaryForPropertyDueDateStatus();
        //            summary.setPropertyReference("REF-1");
        //            summary.propertyRepository = mockPropertyRepository;
        //        }
        //
        //        @Test
        //        public void whenEagerlySet() {
        //            summary.setProperty(property);
        //            context.checking(new Expectations() {
        //                {
        //                    never(mockPropertyMenu);
        //                }
        //            });
        //            assertThat(summary.getProperty()).isEqualTo(property);
        //        }
        //
        //        @Test
        //        public void whenLazilyLoaded() {
        //            context.checking(new Expectations() {
        //                {
        //                    oneOf(mockPropertyRepository).findPropertyByReference("REF-1");
        //                    will(returnValue(property));
        //                }
        //            });
        //
        //            assertThat(summary.getProperty()).isEqualTo(property);
        //        }
    }
}