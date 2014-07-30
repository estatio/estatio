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

import org.joda.time.LocalDate;

import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemStatus;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForFixed;
import org.estatio.dom.lease.LeaseTermForIndexable;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermForTax;
import org.estatio.dom.lease.LeaseTermForTurnoverRent;
import org.estatio.dom.lease.LeaseTermFrequency;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.Leases;
import org.estatio.fixture.EstatioFixtureScript;
import org.estatio.fixture.charge.refdata.ChargeAndChargeGroupRefData;

public abstract class LeaseItemAndTermsAbstract extends EstatioFixtureScript {

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

    protected LeaseTerm createLeaseTermForIndexableRent(
            Lease lease,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal baseValue,
            LocalDate baseIndexStartDate,
            LocalDate nextIndexStartDate,
            LocalDate effectiveDate,
            String indexReference,
            ExecutionContext executionContext) {
        return createLeaseTermForIndexable(
                lease, 
                startDate, 
                endDate, 
                baseValue, 
                baseIndexStartDate, 
                nextIndexStartDate, 
                effectiveDate, 
                indexReference, 
                LeaseItemType.RENT, 
                ChargeAndChargeGroupRefData.CHARGE_REFERENCE_RENT, 
                executionContext);
    }

    protected LeaseTerm createLeaseTermForIndexableServiceCharge(
            Lease lease,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal baseValue,
            LocalDate baseIndexStartDate,
            LocalDate nextIndexStartDate,
            LocalDate effectiveDate,
            String indexReference,
            ExecutionContext executionContext) {
        return createLeaseTermForIndexable(
                lease, 
                startDate, 
                endDate, 
                baseValue, 
                baseIndexStartDate, 
                nextIndexStartDate, 
                effectiveDate, 
                indexReference, 
                LeaseItemType.SERVICE_CHARGE_INDEXABLE, 
                ChargeAndChargeGroupRefData.CHARGE_REFERENCE_SERVICE_CHARGE_INDEXABLE, 
                executionContext);
    }

    private LeaseTerm createLeaseTermForIndexable(
            Lease lease, 
            LocalDate startDate, 
            LocalDate endDate, 
            BigDecimal baseValue, 
            LocalDate baseIndexStartDate, 
            LocalDate nextIndexStartDate, 
            LocalDate effectiveDate, 
            String indexReference, 
            LeaseItemType rent, 
            String chargeReference,
            ExecutionContext executionContext) {
        LeaseItem leaseItem = findOrCreateLeaseItem(
                lease,
                chargeReference,
                rent, 
                InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                executionContext);

        LeaseTermForIndexable leaseTerm = (LeaseTermForIndexable) leaseItem.newTerm(startDate, endDate);
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

        LeaseItem leaseItemServiceCharge = findOrCreateLeaseItem(
                lease,
                ChargeAndChargeGroupRefData.CHARGE_REFERENCE_SERVICE_CHARGE,
                LeaseItemType.SERVICE_CHARGE,
                InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                executionContext);

        LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseItemServiceCharge.newTerm(startDate, endDate);
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

        LeaseItem leaseItem = findOrCreateLeaseItem(
                lease,
                ChargeAndChargeGroupRefData.CHARGE_REFERENCE_TURNOVER_RENT,
                LeaseItemType.TURNOVER_RENT, InvoicingFrequency.YEARLY_IN_ARREARS,
                executionContext);
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

        LeaseItem leaseItem = findOrCreateLeaseItem(
                lease,
                ChargeAndChargeGroupRefData.CHARGE_REFERENCE_DISCOUNT,
                LeaseItemType.DISCOUNT, InvoicingFrequency.FIXED_IN_ADVANCE,
                executionContext);

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

        LeaseItem leaseItem = findOrCreateLeaseItem(
                lease,
                ChargeAndChargeGroupRefData.CHARGE_REFERENCE_ENTRY_FEE,
                LeaseItemType.ENTRY_FEE,
                InvoicingFrequency.FIXED_IN_ADVANCE,
                executionContext);
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

        LeaseItem leaseItem = findOrCreateLeaseItem(
                lease,
                ChargeAndChargeGroupRefData.CHARGE_REFERENCE_TAX,
                LeaseItemType.TAX,
                InvoicingFrequency.FIXED_IN_ADVANCE,
                executionContext);
        LeaseTermForTax leaseTerm = (LeaseTermForTax) leaseItem.newTerm(startDate, endDate);

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);

        leaseTerm.setTaxPercentage(taxPercentage); // mandatory
        leaseTerm.setRecoverablePercentage(recoverablePercentage); // mandatory
        leaseTerm.setInvoicingDisabled(taxable); // mandatory

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
