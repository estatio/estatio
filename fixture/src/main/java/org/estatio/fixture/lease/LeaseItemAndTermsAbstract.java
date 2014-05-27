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
package org.estatio.fixture.lease;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.inject.Inject;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.*;
import org.joda.time.LocalDate;
import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class LeaseItemAndTermsAbstract extends FixtureScript {

    protected LeaseItem findOrCreateLeaseItem(
            final Lease lease,
            final String chargeReference,
            final LeaseItemType leaseItemType,
            final InvoicingFrequency invoicingFrequency,
            ExecutionContext executionContext) {
        final Charge charge = charges.findCharge(chargeReference);
        return findOrCreateLeaseItem(lease, charge, leaseItemType, invoicingFrequency, executionContext);
    }

    protected LeaseItem findOrCreateLeaseItem(
            final Lease lease,
            final Charge charge,
            final LeaseItemType leaseItemType,
            final InvoicingFrequency invoicingFrequency,
            ExecutionContext executionContext) {

        LeaseItem li = lease.findItem(leaseItemType, lease.getStartDate(), BigInteger.ONE);
        if (li == null) {
            li = lease.newItem(leaseItemType, charge, invoicingFrequency, PaymentMethod.DIRECT_DEBIT, lease.getStartDate());
            li.setType(leaseItemType);
            li.setStatus(LeaseItemStatus.ACTIVE);
            li.setEndDate(lease.getEndDate());
            li.setSequence(BigInteger.valueOf(1));
            executionContext.add(this, li);
        }
        return li;
    }

    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForRent(
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal baseValue,
            final LocalDate baseIndexStartDate,
            final LocalDate nextIndexStartDate,
            final LocalDate effectiveDate,
            final String indexReference,
            final ExecutionContext executionContext) {

        LeaseItem leaseItem = findOrCreateLeaseItem(lease, "RENT", LeaseItemType.RENT, InvoicingFrequency.QUARTERLY_IN_ADVANCE, executionContext);

        LeaseTermForIndexableRent leaseTerm = (LeaseTermForIndexableRent) leaseItem.newTerm(startDate, endDate);
        leaseTerm.setBaseValue(baseValue);
        leaseTerm.setBaseIndexStartDate(baseIndexStartDate);
        leaseTerm.setNextIndexStartDate(nextIndexStartDate);
        leaseTerm.setEffectiveDate(effectiveDate);
        leaseTerm.setIndex(indices.findIndex(indexReference));

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);

        return executionContext.add(this, leaseTerm);
    }

    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForServiceCharge(
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal budgetedValue,
            final ExecutionContext executionContext) {

        LeaseItem leaseItem = findOrCreateLeaseItem(lease, "SERVICE_CHARGE", LeaseItemType.SERVICE_CHARGE, InvoicingFrequency.QUARTERLY_IN_ADVANCE, executionContext);
        LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseItem.newTerm(startDate, endDate);

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);

        leaseTerm.setBudgetedValue(budgetedValue);

        return executionContext.add(this, leaseTerm);
    }

    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForTurnoverRent(
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final String turnoverRentRule,
            final ExecutionContext executionContext) {

        LeaseItem leaseItem = findOrCreateLeaseItem(lease, "TURNOVER_RENT", LeaseItemType.TURNOVER_RENT, InvoicingFrequency.YEARLY_IN_ARREARS, executionContext);
        LeaseTermForTurnoverRent leaseTerm = (LeaseTermForTurnoverRent) leaseItem.newTerm(startDate, endDate);

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        leaseTerm.setTurnoverRentRule(turnoverRentRule);

        return executionContext.add(this, leaseTerm);
    }

    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForDiscount(
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal value, // typical value bd(-20000), a negative
            final ExecutionContext executionContext) {

        LeaseItem leaseItem = findOrCreateLeaseItem(lease, "DISCOUNT", LeaseItemType.DISCOUNT, InvoicingFrequency.FIXED_IN_ADVANCE, executionContext);
        LeaseTermForFixed leaseTerm = (LeaseTermForFixed) leaseItem.newTerm(startDate, endDate);

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);

        leaseTerm.setValue(value);

        return executionContext.add(this, leaseTerm);
    }

    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForEntryFee(
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal value, // typical value bd(20000)
            final ExecutionContext executionContext) {

        LeaseItem leaseItem = findOrCreateLeaseItem(lease, "ENTRY_FEE", LeaseItemType.ENTRY_FEE, InvoicingFrequency.FIXED_IN_ADVANCE, executionContext);
        LeaseTermForFixed leaseTerm = (LeaseTermForFixed) leaseItem.newTerm(startDate, endDate);

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);

        leaseTerm.setValue(value);

        return executionContext.add(this, leaseTerm);
    }

    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForTax(
            final Lease lease,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal taxPercentage, // typical value bd(1)
            final BigDecimal recoverablePercentage, // typical value bd(50)
            final boolean taxable,
            final ExecutionContext executionContext) {

        LeaseItem leaseItem = findOrCreateLeaseItem(lease, "TAX", LeaseItemType.TAX, InvoicingFrequency.FIXED_IN_ADVANCE, executionContext);
        LeaseTermForTax leaseTerm = (LeaseTermForTax) leaseItem.newTerm(startDate, endDate);

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);

        leaseTerm.setTaxPercentage(taxPercentage); //mandatory
        leaseTerm.setRecoverablePercentage(recoverablePercentage); //mandatory
        leaseTerm.setTaxable(taxable); // mandatory

        leaseTerm.setTaxValue(null); // optional
        leaseTerm.setTaxableValue(null); // optional
        leaseTerm.setOverrideTaxValue(false); // optional

        leaseTerm.setPaymentDate(null); // optional

        leaseTerm.setOfficeCode(null); // optional
        leaseTerm.setOfficeName(null); // optional

        leaseTerm.setRegistrationDate(null); // optional
        leaseTerm.setRegistrationNumber(null); // optional

        leaseTerm.setDescription(null); // optional

        return executionContext.add(this, leaseTerm);
    }


    // //////////////////////////////////////

    @Inject
    protected Indices indices;

    @Inject
    protected Leases leases;

    @Inject
    protected LeaseTerms leaseTerms;

    @Inject
    protected Charges charges;
}
