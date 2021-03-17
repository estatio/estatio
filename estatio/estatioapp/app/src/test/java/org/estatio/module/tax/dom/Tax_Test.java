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
package org.estatio.module.tax.dom;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.apache.isis.applib.services.factory.FactoryService;
import org.assertj.core.api.Assertions;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.incode.module.unittestsupport.dom.matchers.IsisMatchers;

import static org.junit.Assert.assertEquals;

public class Tax_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    Tax tax;

    public static class PercentageFor extends Tax_Test {

        @Mock
        private FactoryService mockFactoryService;

        @Mock
        private TaxRateRepository mockTaxRateRepository;

        @Before
        public void setup() {

            context.checking(new Expectations() {
                {
                    allowing(mockFactoryService).instantiate(with.is(IsisMatchers.classEqualTo(TaxRate.class)));
                    will(returnNewTransientInstance(TaxRate.class));
                }

                private <T> Action returnNewTransientInstance(final Class<T> cls) {
                    return new Action() {

                        @Override
                        public void describeTo(Description description) {
                            description.appendText("new transient instance of " + cls.getName());
                        }

                        @Override
                        public Object invoke(Invocation inv) throws Throwable {
                            Class<?> cls = (Class<?>) inv.getParameter(0);
                            Object obj = cls.newInstance();
                            final Method[] methods = cls.getMethods();
                            for (final Method method : methods) {
                                if (!method.getName().startsWith("setContainer")) {
                                    continue;
                                }
                                if (method.getParameterTypes().length != 1) {
                                    continue;
                                }
                                final Class<?> methodParameterType = method.getParameterTypes()[0];
                                if (methodParameterType.isAssignableFrom(FactoryService.class)) {
                                    method.invoke(obj, mockFactoryService);
                                    break;
                                }
                            }
                            return obj;
                        }
                    };
                }
            });

            final BigDecimal p1;
            final BigDecimal p2;

            p1 = BigDecimal.valueOf(19);
            p2 = BigDecimal.valueOf(21);

            final LocalDate d1;
            final LocalDate d2;

            d1 = new LocalDate(1980, 1, 1);
            d2 = new LocalDate(2000, 1, 1);

            final TaxRate r1;
            final TaxRate r2;

            r1 = tax.newRate(d1, p1);
            r2 = r1.newRate(d2, p1);

            context.checking(new Expectations() {
                {
                    oneOf(mockTaxRateRepository).findTaxRateByTaxAndDate(with(equal(tax)), with(equal(d1)));
                    will(returnValue(r1));
                    oneOf(mockTaxRateRepository).findTaxRateByTaxAndDate(with(equal(tax)), with(equal(d2)));
                    will(returnValue(r2));
                }
            });

            tax.newRate(d1, p1).newRate(d2, p2);

        }

        @Ignore // TODO: reinstate or delete
        @Test
        public void testTaxPercentageForDate() {
            final LocalDate d1;
            d1 = new LocalDate(1980, 1, 1);

            assertEquals(BigDecimal.valueOf(19), tax.percentageFor(d1));
        }

    }

    public static class GrossFromNet {

        @Test
        public void gross_from_net_correctly_calculated() throws Exception {
            //given

            TaxRate rate = new TaxRate();
            rate.setPercentage(new BigDecimal("21"));
            Tax tax = new Tax() {
                @Override public TaxRate taxRateFor(final LocalDate date) {
                    return rate;
                }

                ;
            };
            //when, then
            Assertions.assertThat(tax.grossFromNet(new BigDecimal("100.00"), new LocalDate(2013, 1, 1))).isEqualTo(new BigDecimal("121.00"));

        }

    }
}