package com.eurocommercialproperties.estatio.fixture.lease;

import java.math.BigDecimal;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.Units;
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

public class LeasesFixture extends AbstractFixture {

    @Override
    public void install() {
        String[] prefixes = { "OXF", "KAL" };
        for (String prefix : prefixes) {
            createLease(prefix + "-TOPMODEL-001", "Topmodel Lease", prefix + "-001", "ACME", "TOPMODEL", new LocalDate(2002, 6, 1), new LocalDate(2013, 5, 31));
            createLease(prefix + "-MEDIAX-002", "Meadiax Lease", prefix + "-002", "ACME", "MEDIAX", new LocalDate(2001, 3, 1), new LocalDate(2012, 2, 29));
        }
    }

    private Lease createLease(String reference, String name, String unitReference, String landlordReference, String tentantReference, LocalDate startDate, LocalDate endDate) {
        Party landlord = parties.findPartyByReference(landlordReference);
        Party tenant = parties.findPartyByReference(tentantReference);
        Unit unit = units.findByReference(unitReference);
        Lease lease = leases.newLease(reference, name);
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        lease.addActor(landlord, LeaseActorType.LANDLORD, null, null);
        lease.addActor(tenant, LeaseActorType.TENANT, null, null);
        lease.addToUnits(leaseUnits.newLeaseUnit(lease, unit));
        LeaseItem leaseItem = createLeaseItem(lease, LeaseItemType.RENT);
        for (int i = 0; i < 10; i++) {
            leaseItem.addToTerms(createLeaseTerm(leaseItem, startDate.plusYears(i), startDate.plusYears(i+1).minusDays(1), BigDecimal.valueOf(30000+(i*1000))));
        }
        lease.addToItems(leaseItem);

        return lease;
    }

    private LeaseItem createLeaseItem(Lease lease, LeaseItemType leaseItemType) {
        return leaseItems.newLeaseItem(lease);
    }
    
    private LeaseTerm createLeaseTerm(LeaseItem leaseItem, LocalDate startDate, LocalDate endDate, BigDecimal value) {
        LeaseTerm leaseTerm = leaseTerms.newLeaseTerm(leaseItem);
        leaseTerm.setStartDate(startDate);
        leaseTerm.setEndDate(endDate);
        leaseTerm.setValue(value);
        return leaseTerm;
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

}
