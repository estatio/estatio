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
package org.estatio.dom;

import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;
import org.estatio.services.clock.ClockService;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EstatioDomainObjectTest {
    public static class InjectClockService extends EstatioDomainObjectTest {

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

        @Mock
        private ClockService mockClockService;

        static class SomeDomainObject extends EstatioDomainObject<SomeDomainObject> {
            public SomeDomainObject() {
                super(null);
            }
        }

        @Test
        public void testImpl() {
            final SomeDomainObject someDomainObject = new SomeDomainObject();
            someDomainObject.injectClockService(mockClockService);

            assertThat(someDomainObject.getClockService(), is(mockClockService));
        }
    }

    public static class ToString extends EstatioDomainObjectTest {

        public static class WithCodeGetterImpl implements WithCodeGetter {

            private String code;
            @Override
            public String getCode() {
                return code;
            }
            public void setCode(String code) {
                this.code = code;
            }
        }

        public static class WithDescriptionGetterImpl implements WithDescriptionGetter {

            private String description;
            @Override
            public String getDescription() {
                return description;
            }
            public void setDescription(String description) {
                this.description = description;
            }
        }

        public static class WithNameGetterImpl implements WithNameGetter {

            private String name;
            @Override
            public String getName() {
                return name;
            }
            public void setName(String name) {
                this.name = name;
            }
        }

        public static class WithReferenceUniqueImpl implements WithReferenceGetter {

            private String reference;
            @Override
            public String getReference() {
                return reference;
            }
            public void setReference(String reference) {
                this.reference = reference;
            }
        }

        public static class WithTitleGetterImpl implements WithTitleGetter {

            private String title;
            @Override
            public String getTitle() {
                return title;
            }
            public void setTitle(String title) {
                this.title = title;
            }
        }

        public static class SomeDomainObject extends EstatioDomainObject<SomeDomainObject>  {
            public SomeDomainObject() {
                super("withCode,withReference,withName,withDescription,withTitle");
            }

            // //////////////////////////////////////

            private WithCodeGetter withCode;

            public WithCodeGetter getWithCode() {
                return withCode;
            }

            public void setWithCode(final WithCodeGetter withCode) {
                this.withCode = withCode;
            }

            // //////////////////////////////////////

            private WithDescriptionGetter withDescription;

            public WithDescriptionGetter getWithDescription() {
                return withDescription;
            }

            public void setWithDescription(final WithDescriptionGetter withDescription) {
                this.withDescription = withDescription;
            }

            // //////////////////////////////////////

            private WithNameGetter withName;

            public WithNameGetter getWithName() {
                return withName;
            }

            public void setWithName(final WithNameGetter withName) {
                this.withName = withName;
            }

            // //////////////////////////////////////

            private WithReferenceGetter withReference;

            public WithReferenceGetter getWithReference() {
                return withReference;
            }

            public void setWithReference(final WithReferenceGetter withReference) {
                this.withReference = withReference;
            }

            // //////////////////////////////////////

            private WithTitleGetter withTitle;

            public WithTitleGetter getWithTitle() {
                return withTitle;
            }

            public void setWithTitle(final WithTitleGetter withTitle) {
                this.withTitle = withTitle;
            }
        }


        @Test
        public void test() {
            final WithCodeGetterImpl wcgi = new WithCodeGetterImpl();
            wcgi.setCode("A");
            final WithDescriptionGetterImpl wdgi = new WithDescriptionGetterImpl();
            wdgi.setDescription("B");
            final WithNameGetterImpl wngi = new WithNameGetterImpl();
            wngi.setName("C");
            final WithReferenceUniqueImpl wrgi = new WithReferenceUniqueImpl();
            wrgi.setReference("D");
            final WithTitleGetterImpl wtgi = new WithTitleGetterImpl();
            wtgi.setTitle("E");

            final SomeDomainObject someDomainObject = new SomeDomainObject();
            someDomainObject.setWithCode(wcgi);
            someDomainObject.setWithDescription(wdgi);
            someDomainObject.setWithName(wngi);
            someDomainObject.setWithReference(wrgi);
            someDomainObject.setWithTitle(wtgi);

            assertThat(someDomainObject.toString(), is("SomeDomainObject{withCode=A, withReference=D, withName=C, withDescription=B, withTitle=E}"));
        }

    }

}