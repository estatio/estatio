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

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.estatio.dom.apptenancy.EstatioApplicationTenancyRepository;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;
import org.estatio.dom.index.IndexRepository;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Fraction;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemStatus;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseMenu;
import org.estatio.dom.lease.LeaseRepository;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForDeposit;
import org.estatio.dom.lease.LeaseTermForFixed;
import org.estatio.dom.lease.LeaseTermForIndexable;
import org.estatio.dom.lease.LeaseTermForPercentage;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermForTax;
import org.estatio.dom.lease.LeaseTermForTurnoverRent;
import org.estatio.dom.lease.LeaseTermFrequency;
import org.estatio.dom.lease.LeaseTermRepository;
import org.estatio.dom.valuetypes.ApplicationTenancyLevel;
import org.estatio.fixture.EstatioFixtureScript;
import org.estatio.fixture.charge.ChargeRefData;

public abstract class LeaseItemAndTermsAbstract extends EstatioFixtureScript {

    protected LeaseItem findOrCreateLeaseItem(
            final String leaseRef,
            final String leaseItemAtPath,
            final String chargeReference,
            final LeaseItemType leaseItemType,
            final InvoicingFrequency invoicingFrequency,
            final ExecutionContext executionContext) {

        final Lease lease = findLease(leaseRef);
        final ApplicationTenancy leaseApplicationTenancy = lease.getApplicationTenancy();
        final ApplicationTenancy countryApplicationTenancy = leaseApplicationTenancy.getParent();

        if(!ApplicationTenancyLevel.of(countryApplicationTenancy).isCountry()) {
            // not expected to happen...
            throw new IllegalStateException("Lease '" + leaseRef + "' has an app tenancy '" + leaseApplicationTenancy.getName() + "' whose parent is not at the country level");
        }

        final Charge charge = chargeRepository.findByReference(chargeReference);
        return findOrCreateLeaseItem(leaseRef, leaseItemAtPath, charge, leaseItemType, invoicingFrequency, executionContext);
    }

    private Lease findLease(final String leaseRef) {
        return leaseRepository.findLeaseByReference(leaseRef);
    }

    protected LeaseItem findOrCreateLeaseItem(
            final String leaseRef,
            final String leaseItemAtPath,
            final Charge charge,
            final LeaseItemType leaseItemType,
            final InvoicingFrequency invoicingFrequency,
            final ExecutionContext executionContext) {

        final Lease lease = findLease(leaseRef);
        final ApplicationTenancy leaseItemApplicationTenancy = estatioApplicationTenancyRepository.findOrCreateTenancyFor(lease.getProperty(), lease.getPrimaryParty());

        LeaseItem li = lease.findItem(leaseItemType, lease.getStartDate(), BigInteger.ONE);
        if (li == null) {
            li = lease.newItem(leaseItemType, charge, invoicingFrequency, PaymentMethod.DIRECT_DEBIT, lease.getStartDate());
            li.setType(leaseItemType);
            li.setStatus(LeaseItemStatus.ACTIVE);
            li.setEndDate(lease.getEndDate());
            li.setSequence(BigInteger.valueOf(1));
            executionContext.addResult(this, li);
        }
        return li;
    }

    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForIndexableRent(
            final String leaseRef,
            final String leaseItemAtPath,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal baseValue,
            final LocalDate baseIndexStartDate,
            final LocalDate nextIndexStartDate,
            final LocalDate effectiveDate,
            final String indexReference,
            final ExecutionContext executionContext) {

        return createLeaseTermForIndexable(
                leaseRef, leaseItemAtPath,
                startDate,
                endDate, 
                baseValue, 
                baseIndexStartDate, 
                nextIndexStartDate, 
                effectiveDate, 
                indexReference, 
                LeaseItemType.RENT, 
                ChargeRefData.IT_RENT,
                executionContext);
    }

    protected LeaseTerm createLeaseTermForPercentage(
            final String leaseRef,
            final String leaseItemAtPath,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal percentage,
            final ExecutionContext executionContext) {

        final LeaseItem leaseItem = findOrCreateLeaseItem(
                leaseRef, leaseItemAtPath,
                ChargeRefData.IT_PERCENTAGE,
                LeaseItemType.RENTAL_FEE,
                InvoicingFrequency.YEARLY_IN_ARREARS,
                executionContext);
        final LeaseTermForPercentage leaseTerm = (LeaseTermForPercentage) leaseItem.newTerm(startDate, endDate);

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        leaseTerm.setPercentage(percentage);

        return executionContext.addResult(this, leaseTerm);
    }

    protected LeaseTerm createLeaseTermForDeposit(
            final String leaseRef,
            final String leaseItemAtPath,
            final LocalDate startDate,
            final LocalDate endDate,
            final Fraction fraction,
            final ExecutionContext executionContext) {

        //Find the rent Item,
        final LeaseItem rentItem = findOrCreateLeaseItem(
                leaseRef,
                leaseItemAtPath,
                ChargeRefData.IT_RENT,
                LeaseItemType.RENT,
                InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                executionContext);

        final LeaseItem leaseItem = findOrCreateLeaseItem(
                leaseRef, leaseItemAtPath,
                ChargeRefData.IT_DEPOSIT,
                LeaseItemType.DEPOSIT,
                InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                executionContext);

        //Link to souce
        if (leaseItem.getSourceItems().size() == 0) {
            leaseItem.newSourceItem(rentItem);
        }



        final LeaseTermForDeposit leaseTerm = (LeaseTermForDeposit) leaseItem.newTerm(startDate, endDate);

        leaseTerm.setFraction(fraction);

        return executionContext.addResult(this, leaseTerm);
    }


    protected LeaseTerm createLeaseTermForIndexableServiceCharge(
            final String leaseRef,
            final String leaseItemAtPath,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal baseValue,
            final LocalDate baseIndexStartDate,
            final LocalDate nextIndexStartDate,
            final LocalDate effectiveDate,
            final String indexReference,
            final ExecutionContext executionContext) {

        return createLeaseTermForIndexable(
                leaseRef, leaseItemAtPath,
                startDate,
                endDate, 
                baseValue, 
                baseIndexStartDate, 
                nextIndexStartDate, 
                effectiveDate, 
                indexReference, 
                LeaseItemType.SERVICE_CHARGE_INDEXABLE, 
                ChargeRefData.IT_SERVICE_CHARGE_INDEXABLE,
                executionContext);
    }

    private LeaseTerm createLeaseTermForIndexable(
            final String leaseRef,
            final String leaseItemAtPath,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal baseValue,
            final LocalDate baseIndexStartDate,
            final LocalDate nextIndexStartDate,
            final LocalDate effectiveDate,
            final String indexReference,
            final LeaseItemType rent,
            final String chargeReference,
            final ExecutionContext executionContext) {

        final LeaseItem leaseItem = findOrCreateLeaseItem(
                leaseRef, leaseItemAtPath,
                chargeReference,
                rent, 
                InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                executionContext);

        final LeaseTermForIndexable leaseTerm = (LeaseTermForIndexable) leaseItem.newTerm(startDate, endDate);
        leaseTerm.setBaseValue(baseValue);
        leaseTerm.setBaseIndexStartDate(baseIndexStartDate);
        leaseTerm.setNextIndexStartDate(nextIndexStartDate);
        leaseTerm.setEffectiveDate(effectiveDate);
        leaseTerm.setIndex(indexRepository.findByReference(indexReference));

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);

        return executionContext.addResult(this, leaseTerm);
    }

    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForServiceCharge(
            final String leaseRef,
            final String leaseItemAtPath,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal budgetedValue,
            final ExecutionContext executionContext) {

        final LeaseItem leaseItemServiceCharge = findOrCreateLeaseItem(
                leaseRef, leaseItemAtPath,
                ChargeRefData.GB_SERVICE_CHARGE,
                LeaseItemType.SERVICE_CHARGE,
                InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                executionContext);

        final LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseItemServiceCharge.newTerm(startDate, endDate);
        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        leaseTerm.setBudgetedValue(budgetedValue);

        return executionContext.addResult(this, leaseTerm);
    }

    // //////////////////////////////////////

    protected LeaseItem createLeaseItemForServiceChargeBudgeted(
            final String leaseRef,
            final String leaseItemAtPath,
            final ExecutionContext executionContext) {

        final LeaseItem leaseItemServiceChargeBudgeted = findOrCreateLeaseItem(
                leaseRef, leaseItemAtPath,
                ChargeRefData.GB_SERVICE_CHARGE,
                LeaseItemType.SERVICE_CHARGE_BUDGETED,
                InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                executionContext);

        return executionContext.addResult(this, leaseItemServiceChargeBudgeted);
    }


    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForTurnoverRent(
            final String leaseRef,
            final String leaseItemAtPath,
            final LocalDate startDate,
            final LocalDate endDate,
            final String turnoverRentRule,
            final ExecutionContext executionContext) {

        //Find the rent Item,
        final LeaseItem rentItem = findOrCreateLeaseItem(
                leaseRef,
                leaseItemAtPath,
                ChargeRefData.IT_RENT,
                LeaseItemType.RENT,
                InvoicingFrequency.QUARTERLY_IN_ADVANCE,
                executionContext);

        final LeaseItem leaseItem = findOrCreateLeaseItem(
                leaseRef,
                leaseItemAtPath,
                ChargeRefData.IT_TURNOVER_RENT,
                LeaseItemType.TURNOVER_RENT,
                InvoicingFrequency.YEARLY_IN_ARREARS,
                executionContext);
        if (leaseItem.getSourceItems().size() == 0) {
            leaseItem.newSourceItem(rentItem);
        }

        //Add firtst term
        final LeaseTermForTurnoverRent leaseTerm = (LeaseTermForTurnoverRent) leaseItem.newTerm(startDate, endDate);

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        leaseTerm.setTurnoverRentRule(turnoverRentRule);

        return executionContext.addResult(this, leaseTerm);
    }

    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForDiscount(
            final String leaseRef,
            final String leaseItemAtPath,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal value, // typical value bd(-20000), a negative
            final ExecutionContext executionContext) {

        final LeaseItem leaseItem = findOrCreateLeaseItem(
                leaseRef, leaseItemAtPath,
                ChargeRefData.IT_DISCOUNT,
                LeaseItemType.DISCOUNT, InvoicingFrequency.FIXED_IN_ADVANCE,
                executionContext);

        final LeaseTermForFixed leaseTerm = (LeaseTermForFixed) leaseItem.newTerm(startDate, endDate);
        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        leaseTerm.setValue(value);

        return executionContext.addResult(this, leaseTerm);
    }

    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForEntryFee(
            final String leaseRef,
            final String leaseItemAtPath,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal value, // typical value bd(20000)
            final ExecutionContext executionContext) {

        final LeaseItem leaseItem = findOrCreateLeaseItem(
                leaseRef, leaseItemAtPath,
                ChargeRefData.IT_ENTRY_FEE,
                LeaseItemType.ENTRY_FEE,
                InvoicingFrequency.FIXED_IN_ADVANCE,
                executionContext);
        final LeaseTermForFixed leaseTerm = (LeaseTermForFixed) leaseItem.newTerm(startDate, endDate);

        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);

        leaseTerm.setValue(value);

        return executionContext.addResult(this, leaseTerm);
    }

    // //////////////////////////////////////

    protected LeaseTerm createLeaseTermForTax(
            final String leaseRef,
            final String leaseItemAtPath,
            final LocalDate startDate,
            final LocalDate endDate,
            final BigDecimal taxPercentage, // typical value bd(1)
            final BigDecimal recoverablePercentage, // typical value bd(50)
            final boolean taxable,
            final ExecutionContext executionContext) {

        final LeaseItem leaseItem = findOrCreateLeaseItem(
                leaseRef, leaseItemAtPath,
                ChargeRefData.IT_TAX,
                LeaseItemType.TAX,
                InvoicingFrequency.FIXED_IN_ADVANCE,
                executionContext);
        final LeaseTermForTax leaseTerm = (LeaseTermForTax) leaseItem.newTerm(startDate, endDate);

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

        return executionContext.addResult(this, leaseTerm);
    }

    // //////////////////////////////////////

    @Inject
    protected IndexRepository indexRepository;

    @Inject
    protected LeaseMenu leaseMenu;

    @Inject
    protected LeaseRepository leaseRepository;

    @Inject
    protected LeaseTermRepository leaseTermRepository;

    @Inject
    protected ChargeRepository chargeRepository;

    @Inject
    protected ApplicationTenancyRepository applicationTenancies;

    @Inject
    private EstatioApplicationTenancyRepository estatioApplicationTenancyRepository;


}
