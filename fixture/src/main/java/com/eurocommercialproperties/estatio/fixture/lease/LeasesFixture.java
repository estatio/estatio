package com.eurocommercialproperties.estatio.fixture.lease;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.asset.Unit;
import com.eurocommercialproperties.estatio.dom.asset.Units;
import com.eurocommercialproperties.estatio.dom.lease.Lease;
import com.eurocommercialproperties.estatio.dom.lease.LeaseActorType;
import com.eurocommercialproperties.estatio.dom.lease.Leases;
import com.eurocommercialproperties.estatio.dom.party.Parties;
import com.eurocommercialproperties.estatio.dom.party.Party;

public class LeasesFixture extends AbstractFixture {

    @Override
    public void install() {
        createLease("OXF-HELLO-001", "Just a smaple lease", "OXF-001", "ACME", "TOPMODEL", new LocalDate(2003, 6, 1), new LocalDate(2013, 5, 31));
    }

    private Lease createLease(String reference, String name, String unitReference, String landlordReference, String tentantReference, LocalDate startDate, LocalDate endDate) {
        Party landlord = parties.findPartyByReference(landlordReference);
        Party tenant = parties.findPartyByReference(tentantReference);
        Unit unit = units.findByReference(unitReference);
        Lease lease = leases.newLease(reference, name);
        lease.addActor(landlord, LeaseActorType.LANDLORD, null, null);
        lease.addActor(tenant, LeaseActorType.TENTANT, null, null);
        lease.addToUnits(leases.newLeaseUnit(lease, unit));
        return lease;
    }

    private Units units;
    
    public void setUnitRepository(final Units units) {
        this.units = units;
    }
    
    private Leases leases;

    public void setLeaseRepository(final Leases leases) {
        this.leases = leases;
    }

    private Parties parties;

    public void setPartyRepository(final Parties parties) {
        this.parties = parties;
    }

}
