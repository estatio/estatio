package org.estatio.fixture.lease;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.index.Indices;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseActorType;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseItems;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexableRent;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermFrequency;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.LeaseUnits;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.PaymentMethod;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

public class LeasesFixture extends AbstractFixture {

    private Party manager;

    @Override
    public void install() {
        manager = parties.findPartyByReference("JDOE");
        createLease("OXF-TOPMODEL-001", "Topmodel Lease", "OXF-001", "ACME", "TOPMODEL", new LocalDate(2010, 7, 15), new LocalDate(2012, 7, 15).plusYears(10).minusDays(1));
        createLease("OXF-MEDIAX-002", "Meadiax Lease", "OXF-002", "ACME", "MEDIAX", new LocalDate(2008, 1, 1), new LocalDate(2012, 1, 1).plusYears(10).minusDays(1));
    }

    private Lease createLease(
            String reference, 
            String name, 
            String unitReference, 
            String landlordReference, 
            String tentantReference, 
            LocalDate startDate, 
            LocalDate endDate) {
        Party landlord = parties.findPartyByReference(landlordReference);
        Party tenant = parties.findPartyByReference(tentantReference);
        Unit unit = units.findByReference(unitReference);
        Lease lease = leases.newLease(reference, name);
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        lease.addActor(landlord, LeaseActorType.LANDLORD, null, null);
        lease.addActor(tenant, LeaseActorType.TENANT, null, null);
        lease.addActor(manager, LeaseActorType.MANAGER, null, null);
        lease.addToUnits(leaseUnits.newLeaseUnit(lease, unit));

        if (leases.findByReference(reference) == null) {
            new RuntimeException();
        }

        Charge chargeRent = charges.findChargeByReference("RENT");
        Charge chargeService = charges.findChargeByReference("SERVICE_CHARGE");

        LeaseItem leaseItem1 = createLeaseItem(lease, LeaseItemType.RENT, chargeRent, startDate, endDate);
        createLeaseTermForIndexableRent(leaseItem1, startDate, null, BigDecimal.valueOf(20000), startDate.dayOfMonth().withMinimumValue(), startDate.plusYears(1).withMonthOfYear(1).withDayOfMonth(1), startDate.plusYears(1).withMonthOfYear(4).withDayOfMonth(1));

        LeaseItem leaseItem2 = createLeaseItem(lease, LeaseItemType.SERVICE_CHARGE, chargeService, startDate, endDate);
        createLeaseTermForServiceCharge(leaseItem2, startDate, null, BigDecimal.valueOf(6000));
        return lease;
    }

    private LeaseItem createLeaseItem(
            Lease lease, 
            LeaseItemType leaseItemType, 
            Charge charge, 
            LocalDate startDate,
            LocalDate endDate) {
        LeaseItem li = leaseItems.newLeaseItem(lease, leaseItemType);
        li.setType(leaseItemType);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        li.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        li.setCharge(charge);
        li.setStartDate(startDate);
        li.setEndDate(endDate);
        li.setSequence(BigInteger.valueOf(1));
        return li;
    }

    private LeaseTerm createLeaseTermForIndexableRent(
            LeaseItem leaseItem, 
            LocalDate startDate, 
            LocalDate endDate, 
            BigDecimal value, 
            LocalDate baseIndexDate, 
            LocalDate nextIndexDate, 
            LocalDate indexationApplicationDate) {
        LeaseTermForIndexableRent leaseTerm = (LeaseTermForIndexableRent) leaseTerms.newLeaseTerm(leaseItem);
        leaseTerm.setStartDate(startDate);
        leaseTerm.setEndDate(endDate);
        leaseTerm.setBaseValue(value);
        leaseTerm.setBaseIndexStartDate(baseIndexDate);
        leaseTerm.setNextIndexStartDate(nextIndexDate);
        leaseTerm.setEffectiveDate(indexationApplicationDate);
        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        leaseTerm.setIndex(indices.findByReference("ISTAT-FOI"));
        return leaseTerm;
    }

    private LeaseTerm createLeaseTermForServiceCharge(
            LeaseItem leaseItem, 
            LocalDate startDate, 
            LocalDate endDate, 
            BigDecimal value) {
        LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseTerms.newLeaseTerm(leaseItem);
        leaseTerm.setLeaseItem(leaseItem);
        leaseTerm.setStartDate(startDate);
        leaseTerm.setEndDate(endDate);
        leaseTerm.setBudgetedValue(value);
        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        return leaseTerm;
    }

    private Indices indices;

    public void setIndexRepository(final Indices indices) {
        this.indices = indices;
    }

    private Units units;

    public void setUnitRepository(final Units units) {
        this.units = units;
    }

    private Leases leases;

    public void setLeaseRepository(final Leases leases) {
        this.leases = leases;
    }

    private LeaseUnits leaseUnits;

    public void setLeaseRepository(final LeaseUnits leaseUnits) {
        this.leaseUnits = leaseUnits;
    }

    private LeaseItems leaseItems;

    public void setLeaseRepository(final LeaseItems leaseItems) {
        this.leaseItems = leaseItems;
    }

    private LeaseTerms leaseTerms;

    public void setLeaseRepository(final LeaseTerms leaseTerms) {
        this.leaseTerms = leaseTerms;
    }

    private Parties parties;

    public void setPartyRepository(final Parties parties) {
        this.parties = parties;
    }

    private Charges charges;

    public void setChargeRepository(final Charges charges) {
        this.charges = charges;
    }

}
