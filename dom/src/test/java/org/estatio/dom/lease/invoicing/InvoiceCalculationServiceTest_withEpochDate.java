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
package org.estatio.dom.lease.invoicing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForTesting;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService.CalculationResult;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.dom.tax.TaxRates;
import org.estatio.services.settings.EstatioSettingsService;

public class InvoiceCalculationServiceTest_withEpochDate {
    private static final LocalDate LEASE_START_DATE = new LocalDate(2011, 11, 1);
    private static final LocalDate LEASE_END_DATE = new LocalDate(2011, 11, 1).plusYears(10).minusDays(1);

    private Lease lease;
    private LeaseItem leaseItem;
    private LeaseTermForTesting leaseTerm;

    private Tax tax;
    private TaxRate taxRate;
    private Charge charge;

    private AgreementRoleType artLandlord;
    private AgreementRoleType artTenant;

    @Mock
    private TaxRates mockTaxRates;

    @Mock
    private Invoices mockInvoices;

    @Mock
    private InvoiceItemsForLease mockInvoiceItemsForLease;

    @Mock
    private AgreementRoles mockAgreementRoles;

    @Mock
    private AgreementRoleTypes mockAgreementRoleTypes;

    @Mock
    private AgreementTypes mockAgreementTypes;

    @Mock
    private EstatioSettingsService mockSettings;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    private InvoiceCalculationService ic;

    private InvoiceItemForLease invoiceItemForLease;

    @Before
    public void setup() {
        artLandlord = new AgreementRoleType();
        artLandlord.setTitle("Landlord");

        artTenant = new AgreementRoleType();
        artTenant.setTitle("Tenant");

        context.checking(new Expectations() {
            {
                allowing(mockAgreementRoleTypes).findByTitle("Landlord");
                will(returnValue(artLandlord));
                allowing(mockAgreementRoleTypes).findByTitle("Tenant");
                will(returnValue(artTenant));
                allowing(mockSettings).fetchEpochDate();
                will(returnValue(new LocalDate(2013, 1, 1)));
            }
        });

        lease = new Lease();
        lease.injectAgreementRoles(mockAgreementRoles);
        lease.setStartDate(LEASE_START_DATE);
        lease.setEndDate(LEASE_END_DATE);

        leaseItem = new LeaseItem();
        leaseItem.setStartDate(LEASE_START_DATE);
        leaseItem.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        leaseItem.setLease(lease);

        lease.getItems().add(leaseItem);

        leaseTerm = new LeaseTermForTesting();
        leaseTerm.setLeaseItem(leaseItem);

        leaseItem.getTerms().add(leaseTerm);

        tax = new Tax();
        tax.injectTaxRates(mockTaxRates);
        tax.setReference("VAT");

        taxRate = new TaxRate();
        taxRate.setPercentage(BigDecimal.valueOf(21));

        charge = new Charge();
        charge.setReference("RENT");
        charge.setTax(tax);

        invoiceItemForLease = new InvoiceItemForLease();
        invoiceItemForLease.modifyLeaseTerm(leaseTerm);

        invoiceItemForLease.injectAgreementRoleTypes(mockAgreementRoleTypes);
        invoiceItemForLease.injectAgreementTypes(mockAgreementTypes);

        ic = new InvoiceCalculationService();
        ic.injectEstatioSettings(mockSettings);
    }

    @Test
    public void testFuture() {
        leaseItem.setCharge(charge);
        leaseTerm.setStartDate(new LocalDate(2014, 1, 1));
        leaseTerm.setEndDate(new LocalDate(2014, 12, 31));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        leaseTerm.setAdjustedValue(BigDecimal.valueOf(22000));
        tester(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2014, 1, 1),
                0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00);
    }

    @Test
    public void testWithYearlyInvoicingFrequency() {
        leaseTerm.setStartDate(new LocalDate(2012, 2, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 1, 1));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        tester(leaseTerm, new LocalDate(2012, 1, 1), new LocalDate(2012, 1, 1), InvoicingFrequency.YEARLY_IN_ARREARS,
                3296.70, 5000.00, 5000.00, 5000.00);

    }

    @Test
    public void testFullCalculationResults() {
        leaseTerm.setStartDate(new LocalDate(2012, 2, 1));
        leaseTerm.setEndDate(new LocalDate(2013, 1, 31));
        leaseTerm.setValue(BigDecimal.valueOf(20000));
        tester(leaseTerm, leaseTerm.getStartDate(), leaseTerm.getEndDate(),
                0.00, 0.00, 0.00, 0.00, 1722.22);
        // TODO: Since 2012 is a leap year, the sum of the invoices is greater
        // than the value of the term.....
    }

    // //////////////////////////////////////

    private void tester(
            final LeaseTerm leaseTerm,
            final LocalDate startDueDate,
            final LocalDate nextDueDate,
            final Double... values
            ) {
        tester(leaseTerm, startDueDate, nextDueDate, leaseTerm.getLeaseItem().getInvoicingFrequency(), values);
    }

    private void tester(
            final LeaseTerm leaseTerm,
            final LocalDate startDueDate,
            final LocalDate nextDueDate,
            final InvoicingFrequency invoicingFrequency,
            final Double... values
            ) {
        List<CalculationResult> results = ic.calculateDueDateRange(
                leaseTerm,
                new InvoiceCalculationParameters(
                        InvoiceRunType.NORMAL_RUN, 
                        startDueDate,
                        startDueDate, 
                        nextDueDate == null ? startDueDate.plusDays(1) : nextDueDate));
        for (int i = 0; i < results.size(); i++) {
            assertThat(results.toString(), results.get(i).value().subtract(results.get(i).mockValue()),
                    is(new BigDecimal(values[i]).setScale(2, RoundingMode.HALF_UP)));
        }
    }

}
