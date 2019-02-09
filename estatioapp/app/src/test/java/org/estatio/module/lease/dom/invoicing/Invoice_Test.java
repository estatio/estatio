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
package org.estatio.module.lease.dom.invoicing;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.module.base.platform.docfragment.FragmentRenderService;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.attr.InvoiceAttribute;
import org.estatio.module.invoice.dom.attr.InvoiceAttributeName;
import org.estatio.module.invoice.dom.attr.InvoiceAttributeRepository;
import org.estatio.module.lease.dom.invoicing.attr.InvoiceForLease_description;
import org.estatio.module.lease.dom.invoicing.attr.InvoiceForLease_preliminaryLetterDescription;
import org.estatio.module.lease.dom.invoicing.ssrs.InvoiceAttributesVM;

import static org.assertj.core.api.Assertions.assertThat;

public class Invoice_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    InvoiceForLease invoice;

    @Mock
    FragmentRenderService mockFragmentRenderService;

    @Mock
    InvoiceAttributeRepository mockInvoiceAttributeRepository;

    public static class OverridePreliminaryLetterDescription_Test extends Invoice_Test {

        InvoiceForLease._overridePreliminaryLetterDescription mixin;

        @Before
        public void setUp() throws Exception {
            invoice = getInvoiceForLease(InvoiceStatus.APPROVED, "Some PL desc", false, mockInvoiceAttributeRepository);
            mixin = new InvoiceForLease._overridePreliminaryLetterDescription(invoice);

        }

        @Test
        @Ignore
        public void can_change() throws Exception {

            //Given


            // expecting
            context.checking(new Expectations() {{
                oneOf(mockInvoiceAttributeRepository).findByInvoiceAndName(with(any(InvoiceForLease.class)), with(any(InvoiceAttributeName.class)));
                will(returnValue(invoiceAttributeWith("value")));
            }

            });

            // then
            assertThat(this.mixin.disableAct()).isNull();
            assertThat(this.mixin.default0Act()).isEqualTo("Some PL desc");

            // when
            this.mixin.act("Overridden PL desc");

            // then
            assertThat(new InvoiceForLease_preliminaryLetterDescription(invoice).prop()).isEqualTo("Overridden PL desc");
        }

        private InvoiceAttribute invoiceAttributeWith(final String value) {
            return new InvoiceAttribute(){
                @Override public String getValue() {
                    return value;
                }
            };
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

        InvoiceForLease._resetPreliminaryLetterDescription mixin;

        @Before
        public void setUp() throws Exception {

            // given
            invoice = getInvoiceForLease(InvoiceStatus.APPROVED, "Approved PL desc", true,
                    mockInvoiceAttributeRepository);

            mixin = new InvoiceForLease._resetPreliminaryLetterDescription(invoice) {
                {
                    this.fragmentRenderService = mockFragmentRenderService;
                }
            };
        }

        @Test
        @Ignore
        public void can_reset() throws Exception {

            // then
            assertThat(new InvoiceForLease_preliminaryLetterDescription(invoice).prop()).isEqualTo("Approved PL desc");
            assertThat(mixin.hideAct()).isFalse();
            assertThat(mixin.disableAct()).isNull();

            // expecting
            context.checking(new Expectations() {{
                oneOf(mockFragmentRenderService).render(with(viewModelFor(invoice)), with("preliminaryLetterDescription"));
                will(returnValue("PL desc reset"));
            }});

            // when
            mixin.act();

            // then
//            assertThat(invoice.getPreliminaryLetterDescription()).isEqualTo("PL desc reset");
            assertThat(mixin.hideAct()).isFalse();
        }

        private Matcher<InvoiceAttributesVM> viewModelFor(final InvoiceForLease invoice) {
            return new TypeSafeMatcher<InvoiceAttributesVM>() {
                @Override protected boolean matchesSafely(final InvoiceAttributesVM invoiceAttributesVM) {
                    return invoiceAttributesVM.getInvoice() == invoice;
                }

                @Override public void describeTo(final Description description) {
                    description.appendValue("is view model wrapping " + invoice);
                }
            };
        }

        @Test
        @Ignore
        public void hidden_if_not_overridden() throws Exception {
            // given
            invoice = getInvoiceForLease(InvoiceStatus.APPROVED, "Some PL desc", false, mockInvoiceAttributeRepository);

            // then
            assertThat(mixin.hideAct()).isTrue();
        }

        @Test
        @Ignore
        public void disabled_if_immutable() throws Exception {
            // given
            invoice.setStatus(InvoiceStatus.INVOICED);

            // then
            assertThat(mixin.disableAct()).isEqualTo("Invoice can't be changed");
        }
    }

    public static class OverrideDescription_Test extends Invoice_Test {

        InvoiceForLease._overrideInvoiceDescription mixin;

        @Before
        public void setUp() throws Exception {

            // given
            invoice = getInvoiceForLease(InvoiceStatus.APPROVED, "Some PL desc", false, mockInvoiceAttributeRepository);

            mixin = new InvoiceForLease._overrideInvoiceDescription(invoice);
        }

        @Test
        @Ignore
        public void can_change() throws Exception {

            // then
            assertThat(mixin.disableAct()).isNull();
            assertThat(mixin.default0Act()).isEqualTo("Some PL desc");

            // when
            mixin.act("Overridden PL desc");

            // then
            assertThat(new InvoiceForLease_description(invoice).prop()).isEqualTo("Overridden PL desc");
        }

        @Test
        @Ignore
        public void disabled_if_immutable() throws Exception {
            // given
            invoice.setStatus(InvoiceStatus.INVOICED);

            // then
            assertThat(mixin.disableAct()).isEqualTo("Invoice can't be changed");
        }
    }

    public static class UnoverrideDescription_Test extends Invoice_Test {

        InvoiceForLease._resetInvoiceDescription mixin;

        @Before
        public void setUp() throws Exception {
            // given
            invoice = getInvoiceForLease(InvoiceStatus.APPROVED, "Overridden PL desc", true,
                    mockInvoiceAttributeRepository);
            invoice.setStatus(InvoiceStatus.APPROVED);

            mixin = new InvoiceForLease._resetInvoiceDescription(invoice) {
                {
                    this.fragmentRenderService = mockFragmentRenderService;
                }
            };
        }

        @Test
        @Ignore
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
            assertThat(new InvoiceForLease_description(invoice).prop()).isEqualTo("Some PL desc");
            assertThat(mixin.hideAct()).isFalse();
        }

        @Test
        @Ignore
        public void hidden_if_not_overridden() throws Exception {
            // given
            invoice = getInvoiceForLease(InvoiceStatus.APPROVED, "Some PL desc", false, mockInvoiceAttributeRepository);

            // then
            assertThat(mixin.hideAct()).isTrue();
        }

        @Test
        @Ignore
        public void disabled_if_immutable() throws Exception {
            // given
            invoice.setStatus(InvoiceStatus.INVOICED);

            // then
            assertThat(mixin.disableAct()).isEqualTo("Invoice can't be changed");
        }
    }

    private static InvoiceForLease getInvoiceForLease(
            final InvoiceStatus status,
            final String attributeValue,
            final boolean attributeOverridden,
            final InvoiceAttributeRepository mockInvoiceAttributeRepository) {

        final InvoiceForLease invoiceForLease = new InvoiceForLease() {
            {
                this.invoiceAttributeRepository = mockInvoiceAttributeRepository;
            }
            @Override public String attributeValueFor(final InvoiceAttributeName invoiceAttributeName) {
                return attributeValue;
            }

            @Override protected boolean attributeOverriddenFor(final InvoiceAttributeName invoiceAttributeName) {
                return attributeOverridden;
            }

        };
        invoiceForLease.setStatus(status);
        return invoiceForLease;
    }


}