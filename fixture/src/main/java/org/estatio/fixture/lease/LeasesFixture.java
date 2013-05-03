package org.estatio.fixture.lease;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.index.Indices;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
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
        Lease lease1 = createLease("OXF-TOPMODEL-001", "Topmodel Lease", "OXF-001", "ACME", "TOPMODEL", new LocalDate(2010, 7, 15), new LocalDate(2022, 7, 14));
        createLeaseTermForIndexableRent(lease1, BigInteger.valueOf(1), lease1.getStartDate(), null, BigDecimal.valueOf(20000), new LocalDate(2010, 7, 1), new LocalDate(2011, 1, 1), new LocalDate(2011, 4, 1));
        createLeaseTermForServiceCharge(lease1, lease1.getStartDate(), null, BigDecimal.valueOf(6000));

        Lease lease2 = createLease("OXF-MEDIAX-002", "Meadiax Lease", "OXF-002", "ACME", "MEDIAX", new LocalDate(2008, 1, 1), new LocalDate(2017, 12, 31));
        createLeaseTermForIndexableRent(lease2, BigInteger.valueOf(1), lease2.getStartDate(), null, BigDecimal.valueOf(20000), new LocalDate(2008, 1, 1), new LocalDate(2009, 1, 1), new LocalDate(2009, 4, 1));
        createLeaseTermForServiceCharge(lease2, lease2.getStartDate(), null, BigDecimal.valueOf(6000));

        Lease lease3 = createLease("OXF-POISON-003", "Poison Lease", "OXF-003", "ACME", "POISON", new LocalDate(2011, 1, 1), new LocalDate(2020, 12, 31));
        createLeaseTermForIndexableRent(lease3, BigInteger.valueOf(1), lease3.getStartDate(), null, BigDecimal.valueOf(87300), null, null, null);
        createLeaseTermForIndexableRent(lease3, BigInteger.valueOf(2), lease3.getStartDate().plusYears(1), null, BigDecimal.valueOf(87300), new LocalDate(2011, 1, 1), new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1));
        createLeaseTermForServiceCharge(lease3, lease3.getStartDate(), null, BigDecimal.valueOf(12400));

    }

    private Lease createLease(String reference, String name, String unitReference, String landlordReference, String tentantReference, LocalDate startDate, LocalDate endDate) {
        Party landlord = parties.findPartyByReference(landlordReference);
        Party tenant = parties.findPartyByReference(tentantReference);
        Unit unit = units.findByReference(unitReference);
        Lease lease = leases.newLease(reference, name);
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        lease.addRole(landlord, AgreementRoleType.LANDLORD, null, null);
        lease.addRole(tenant, AgreementRoleType.TENANT, null, null);
        lease.addRole(manager, AgreementRoleType.MANAGER, null, null);
        leaseUnits.newLeaseUnit(lease, unit);

        if (leases.findByReference(reference) == null) {
            new RuntimeException();
        }
        return lease;
    }

    private LeaseItem createLeaseItem(Lease lease, LeaseItemType leaseItemType, Charge charge) {
        LeaseItem li = lease.findItem(leaseItemType, lease.getStartDate(), BigInteger.ONE);
        if (li == null) {
            li = lease.newItem(leaseItemType);
            //li = leaseItems.newLeaseItem(lease, leaseItemType);
            li.setType(leaseItemType);
            li.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
            li.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            li.setCharge(charge);
            li.setStartDate(lease.getStartDate());
            li.setEndDate(lease.getEndDate());
            li.setSequence(BigInteger.valueOf(1));
        }
        return li;
    }

    private LeaseTerm createLeaseTermForIndexableRent(Lease lease1, BigInteger sequence, LocalDate startDate, LocalDate endDate, BigDecimal value, LocalDate baseIndexDate, LocalDate nextIndexDate, LocalDate indexationApplicationDate) {
        LeaseItem leaseItem = createLeaseItem(lease1, LeaseItemType.RENT, charges.findChargeByReference("RENT"));
        LeaseTermForIndexableRent leaseTerm;
        if (sequence.equals(BigInteger.ONE)) {
            leaseTerm = (LeaseTermForIndexableRent) leaseItem.createInitialTerm();
        } else
        {
            LeaseTerm currentTerm = leaseItem.findTermWithSequence(sequence.subtract(BigInteger.ONE));
            leaseTerm = (LeaseTermForIndexableRent) leaseItem.createNextTerm(currentTerm);   
        }
        leaseTerm.setStartDate(startDate);
        leaseTerm.setEndDate(endDate);
        leaseTerm.setBaseValue(value);
        leaseTerm.setBaseIndexStartDate(baseIndexDate);
        leaseTerm.setNextIndexStartDate(nextIndexDate);
        leaseTerm.setEffectiveDate(indexationApplicationDate);
        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        leaseTerm.setIndex(indices.findByReference("ISTAT-FOI"));
        leaseTerm.setSequence(sequence);
        return leaseTerm;
    }

    private LeaseTerm createLeaseTermForServiceCharge(Lease lease, LocalDate startDate, LocalDate endDate, BigDecimal value) {
        LeaseItem leaseItem = createLeaseItem(lease, LeaseItemType.SERVICE_CHARGE, charges.findChargeByReference("SERVICE_CHARGE"));
        LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseTerms.newLeaseTerm(leaseItem);
        leaseTerm.modifyLeaseItem(leaseItem);
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
