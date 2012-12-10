package com.eurocommercialproperties.estatio.fixture.lease;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.Units;
import com.eurocommercialproperties.estatio.dom.index.Indices;
import com.eurocommercialproperties.estatio.dom.invoice.Charge;
import com.eurocommercialproperties.estatio.dom.invoice.Charges;
import com.eurocommercialproperties.estatio.dom.lease.IndexableLeaseTerm;
import com.eurocommercialproperties.estatio.dom.lease.IndexationFrequency;
import com.eurocommercialproperties.estatio.dom.lease.InvoicingFrequency;
import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActorType;
import com.eurocommercialproperties.estatio.dom.lease.LeaseItem;
import com.eurocommercialproperties.estatio.dom.lease.LeaseItemType;
import com.eurocommercialproperties.estatio.dom.lease.LeaseItems;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTerm;
import com.eurocommercialproperties.estatio.dom.lease.LeaseTerms;
import com.eurocommercialproperties.estatio.dom.lease.LeaseUnits;
import com.eurocommercialproperties.estatio.dom.lease.Leases;
import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;

import org.apache.isis.applib.fixtures.AbstractFixture;

public class LeasesFixture extends AbstractFixture {

    private Party manager;
    
    @Override
    public void install() {
        String[] prefixes = { "OXF", "KAL" };
        LocalDate[] dates = { new LocalDate(2010, 7, 15), new LocalDate(2008, 1, 1) };
        Charge charge = charges.newCharge("RENT");
        manager = parties.findPartyByReference("JDOE");
        for (String prefix : prefixes) {
            createLease(prefix + "-TOPMODEL-001", "Topmodel Lease", prefix + "-001", "ACME", "TOPMODEL", dates[0], dates[0].plusYears(10).minusDays(1), charge);
            createLease(prefix + "-MEDIAX-002", "Meadiax Lease", prefix + "-002", "ACME", "MEDIAX", dates[1], dates[1].plusYears(10).minusDays(1), charge);
        }
    }

    private Lease createLease(String reference, String name, String unitReference, String landlordReference, String tentantReference, LocalDate startDate, LocalDate endDate, Charge charge) {
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
        LeaseItem leaseItem = createLeaseItem(lease, LeaseItemType.RENT, charge, startDate);
        leaseItem.addToTerms(createIndexableLeaseTerm(leaseItem, startDate, null, BigDecimal.valueOf(20000), startDate.dayOfMonth().withMinimumValue(), startDate.plusYears(1).withMonthOfYear(1).withDayOfMonth(1), startDate.plusYears(1).withMonthOfYear(4).withDayOfMonth(1)));
        lease.addToItems(leaseItem);
        return lease;
    }

    private LeaseItem createLeaseItem(Lease lease, LeaseItemType leaseItemType, Charge charge, LocalDate startDate) {
        LeaseItem li = leaseItems.newLeaseItem(lease);
        li.setType(leaseItemType);
        li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY);
        li.setIndexationFrequency(IndexationFrequency.YEARLY);
        li.setIndex(indices.findByReference("ISTAT-FOI"));
        li.setCharge(charge);
        li.setStartDate(startDate);
        li.setSequence(BigInteger.valueOf(1));
        return li;
    }

    private LeaseTerm createLeaseTerm(LeaseItem leaseItem, LocalDate startDate, LocalDate endDate, BigDecimal value) {
        LeaseTerm leaseTerm = leaseTerms.newLeaseTerm(leaseItem);
        leaseTerm.setStartDate(startDate);
        leaseTerm.setEndDate(endDate);
        leaseTerm.setValue(value);
        return leaseTerm;
    }

    private LeaseTerm createIndexableLeaseTerm(LeaseItem leaseItem, LocalDate startDate, LocalDate endDate, BigDecimal value, LocalDate baseIndexDate, LocalDate nextIndexDate, LocalDate indexationApplicationDate) {
        IndexableLeaseTerm leaseTerm = leaseTerms.newIndexableLeaseTerm(leaseItem);
        leaseTerm.setStartDate(startDate);
        leaseTerm.setEndDate(endDate);
        leaseTerm.setValue(value);
        leaseTerm.setBaseIndexStartDate(baseIndexDate);
        leaseTerm.setBaseIndexEndDate(baseIndexDate.dayOfMonth().withMaximumValue());
        leaseTerm.setNextIndexStartDate(nextIndexDate);
        leaseTerm.setNextIndexEndDate(nextIndexDate.dayOfMonth().withMaximumValue());
        leaseTerm.setEffectiveDate(indexationApplicationDate);
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
