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
package org.estatio.dom.invoice;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.base.FragmentRenderService;

import static org.assertj.core.api.Assertions.assertThat;

public class Invoice_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    Invoice invoice;

    @Mock
    FragmentRenderService mockFragmentRenderService;

    @Before
    public void setUp() throws Exception {
        invoice = new Invoice("x"){};
    }


    public static class OverridePreliminaryLetterDescription_Test extends Invoice_Test {

        Invoice._overridePreliminaryLetterDescription mixin;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            // given
            invoice.setPreliminaryLetterDescription("Some PL desc");
            invoice.setPreliminaryLetterDescriptionOverridden(false);
            invoice.setStatus(InvoiceStatus.APPROVED);

            mixin = new Invoice._overridePreliminaryLetterDescription(invoice);
        }

        @Test
        public void can_change() throws Exception {

            // then
            assertThat(mixin.hideAct()).isFalse();
            assertThat(mixin.disableAct()).isNull();
            assertThat(mixin.default0Act()).isEqualTo("Some PL desc");
            
            // when
            mixin.act("Overridden PL desc");

            // then
            assertThat(invoice.getPreliminaryLetterDescription()).isEqualTo("Overridden PL desc");
            assertThat(invoice.isPreliminaryLetterDescriptionOverridden()).isTrue();
        }

        @Test
        public void hidden_if_overridden() throws Exception {
            // given
            invoice.setPreliminaryLetterDescription("Overridden PL descr");
            invoice.setPreliminaryLetterDescriptionOverridden(true);

            // then
            assertThat(mixin.hideAct()).isTrue();
        }

        @Test
        public void disabled_if_immutable() throws Exception {
            // given
            invoice.setStatus(InvoiceStatus.INVOICED);

            // then
            assertThat(mixin.disableAct()).isEqualTo("Invoice can't be changed");
        }
    }

    public static class UnoverridePreliminaryLetterDescription_Test extends Invoice_Test {

        Invoice._unoverridePreliminaryLetterDescription mixin;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            // given
            invoice.setPreliminaryLetterDescription("Overridden PL desc");
            invoice.setPreliminaryLetterDescriptionOverridden(true);
            invoice.setStatus(InvoiceStatus.APPROVED);

            mixin = new Invoice._unoverridePreliminaryLetterDescription(invoice);
            mixin.fragmentRenderService = mockFragmentRenderService;
        }

        @Test
        public void can_unoverride() throws Exception {

            // then
            assertThat(mixin.hideAct()).isFalse();
            assertThat(mixin.disableAct()).isNull();

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockFragmentRenderService).render(invoice, "preliminaryLetterDescription");
                will(returnValue("Some PL desc"));
            }});

            // when
            mixin.act();

            // then
            assertThat(invoice.getPreliminaryLetterDescription()).isEqualTo("Some PL desc");
            assertThat(invoice.isPreliminaryLetterDescriptionOverridden()).isFalse();
        }

        @Test
        public void hidden_if_not_overridden() throws Exception {
            // given
            invoice.setPreliminaryLetterDescription("Some PL descr");
            invoice.setPreliminaryLetterDescriptionOverridden(false);

            // then
            assertThat(mixin.hideAct()).isTrue();
        }

        @Test
        public void disabled_if_immutable() throws Exception {
            // given
            invoice.setStatus(InvoiceStatus.INVOICED);

            // then
            assertThat(mixin.disableAct()).isEqualTo("Invoice can't be changed");
        }
    }

    public static class OverrideDescription_Test extends Invoice_Test {

        Invoice._overrideDescription mixin;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            // given
            invoice.setDescription("Some PL desc");
            invoice.setDescriptionOverridden(false);
            invoice.setStatus(InvoiceStatus.APPROVED);

            mixin = new Invoice._overrideDescription(invoice);
        }

        @Test
        public void can_change() throws Exception {

            // then
            assertThat(mixin.hideAct()).isFalse();
            assertThat(mixin.disableAct()).isNull();
            assertThat(mixin.default0Act()).isEqualTo("Some PL desc");
            
            // when
            mixin.act("Overridden PL desc");

            // then
            assertThat(invoice.getDescription()).isEqualTo("Overridden PL desc");
            assertThat(invoice.isDescriptionOverridden()).isTrue();
        }

        @Test
        public void hidden_if_overridden() throws Exception {
            // given
            invoice.setDescription("Overridden PL descr");
            invoice.setDescriptionOverridden(true);

            // then
            assertThat(mixin.hideAct()).isTrue();
        }

        @Test
        public void disabled_if_immutable() throws Exception {
            // given
            invoice.setStatus(InvoiceStatus.INVOICED);

            // then
            assertThat(mixin.disableAct()).isEqualTo("Invoice can't be changed");
        }
    }

    public static class UnoverrideDescription_Test extends Invoice_Test {

        Invoice._unoverrideDescription mixin;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            // given
            invoice.setDescription("Overridden PL desc");
            invoice.setDescriptionOverridden(true);
            invoice.setStatus(InvoiceStatus.APPROVED);

            mixin = new Invoice._unoverrideDescription(invoice);
            mixin.fragmentRenderService = mockFragmentRenderService;
        }

        @Test
        public void can_unoverride() throws Exception {

            // then
            assertThat(mixin.hideAct()).isFalse();
            assertThat(mixin.disableAct()).isNull();

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockFragmentRenderService).render(invoice, "description");
                will(returnValue("Some PL desc"));
            }});

            // when
            mixin.act();

            // then
            assertThat(invoice.getDescription()).isEqualTo("Some PL desc");
            assertThat(invoice.isDescriptionOverridden()).isFalse();
        }

        @Test
        public void hidden_if_not_overridden() throws Exception {
            // given
            invoice.setDescription("Some PL descr");
            invoice.setDescriptionOverridden(false);

            // then
            assertThat(mixin.hideAct()).isTrue();
        }

        @Test
        public void disabled_if_immutable() throws Exception {
            // given
            invoice.setStatus(InvoiceStatus.INVOICED);

            // then
            assertThat(mixin.disableAct()).isEqualTo("Invoice can't be changed");
        }
    }

    public static class ChangePreliminaryLetterComment_Test extends Invoice_Test {

        Invoice._changePreliminaryLetterComment mixin;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            // given
            invoice.setStatus(InvoiceStatus.APPROVED);

            mixin = new Invoice._changePreliminaryLetterComment(invoice);
        }

        @Test
        public void can_change() throws Exception {

            // then
            assertThat(mixin.disableAct()).isNull();
            assertThat(mixin.default0Act()).isNull();

            // when
            mixin.act("Some PL comment");

            // then
            assertThat(invoice.getPreliminaryLetterComment()).isEqualTo("Some PL comment");

            // when
            assertThat(mixin.default0Act()).isEqualTo("Some PL comment");
            mixin.act("Some other PL comment");

            // then
            assertThat(invoice.getPreliminaryLetterComment()).isEqualTo("Some other PL comment");
        }

        @Test
        public void disabled_if_immutable() throws Exception {
            // given
            invoice.setStatus(InvoiceStatus.INVOICED);

            // then
            assertThat(mixin.disableAct()).isEqualTo("Invoice can't be changed");
        }
    }


}