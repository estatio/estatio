/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.dom.asset.registration.contributed;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetForTesting;
import org.estatio.dom.asset.registration.FixedAssetRegistration;
import org.estatio.dom.asset.registration.FixedAssetRegistrationType;
import org.estatio.dom.asset.registration.FixedAssetRegistrationRepository;
import org.estatio.dom.valuetypes.LocalDateInterval;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FixedAssetRegistrationContributionsTest {

    public static class NewRegistration extends FixedAssetRegistrationContributionsTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

        @Mock
        private DomainObjectContainer mockContainer;
        @Mock
        private FixedAssetRegistrationRepository mockFixedAssetRegistrationRepository;

        private FixedAsset subject;
        private FixedAssetRegistrationType registrationType;

        private FixedAssetRegistrationContributions target;

        public static class FoobarAssetRegistration extends FixedAssetRegistration {

            @Override
            public LocalDate default0ChangeDates() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public LocalDate default1ChangeDates() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public LocalDateInterval getEffectiveInterval() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean isCurrent() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }

        }

        @Before
        public void setUp() throws Exception {
            subject = new FixedAssetForTesting();
            registrationType = new FixedAssetRegistrationType();
            registrationType.setFullyQualifiedClassName(FoobarAssetRegistration.class.getName());

            target = new FixedAssetRegistrationContributions();
            target.injectFixedAssetRegistrations(mockFixedAssetRegistrationRepository);
            target.setContainer(mockContainer);
        }

        @Test
        public void test() {
            final FoobarAssetRegistration created = new FoobarAssetRegistration();
            context.checking(new Expectations() {
                {
                    oneOf(mockContainer).newTransientInstance(FoobarAssetRegistration.class);
                    will(returnValue(created));

                    oneOf(mockContainer).persistIfNotAlready(created);
                }
            });
            final FixedAssetRegistration far = target.newRegistration(subject, registrationType);
            assertThat(far.getType(), is(registrationType));
        }

    }
}