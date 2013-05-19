package org.estatio.fixture.lease;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.asset.Unit;
import org.estatio.dom.asset.Units;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.InvoicingFrequency;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseConstants;
import org.estatio.dom.lease.LeaseItem;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForIndexableRent;
import org.estatio.dom.lease.LeaseTermForServiceCharge;
import org.estatio.dom.lease.LeaseTermFrequency;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.LeaseUnit;
import org.estatio.dom.lease.LeaseUnitActivity;
import org.estatio.dom.lease.LeaseUnitBrand;
import org.estatio.dom.lease.LeaseUnitReferenceType;
import org.estatio.dom.lease.LeaseUnitReferences;
import org.estatio.dom.lease.LeaseUnitSector;
import org.estatio.dom.lease.LeaseUnits;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.UnitForLease;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import org.apache.isis.applib.fixtures.AbstractFixture;

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
        UnitForLease unit = (UnitForLease) units.findUnitByReference(unitReference);
        Lease lease = leases.newLease(reference, name, startDate, null, endDate, landlord, tenant);
        lease.addRole(manager, agreementRoleTypes.find(LeaseConstants.ART_MANAGER), null, null);
        LeaseUnit lu = leaseUnits.newLeaseUnit(lease, unit);
        lu.setBrand((LeaseUnitBrand) leaseUnitReferences.findOrCreate(LeaseUnitReferenceType.BRAND, tentantReference));
        lu.setActivity((LeaseUnitActivity) leaseUnitReferences.findOrCreate(LeaseUnitReferenceType.ACTIVITY, "OTHER"));
        lu.setSector((LeaseUnitSector) leaseUnitReferences.findOrCreate(LeaseUnitReferenceType.SECTOR, "OTHER"));
        if (leases.findByReference(reference) == null) {
            new RuntimeException();
        }
        return lease;
    }

    private LeaseItem createLeaseItem(Lease lease, LeaseItemType leaseItemType, Charge charge) {
        LeaseItem li = lease.findItem(leaseItemType, lease.getStartDate(), BigInteger.ONE);
        if (li == null) {
            li = lease.newItem(leaseItemType);
            // li = leaseItems.newLeaseItem(lease, leaseItemType);
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
        } else {
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

    private LeaseTerm createLeaseTermForServiceCharge(Lease lease, LocalDate startDate, LocalDate endDate, BigDecimal budgetedValue) {
        LeaseItem leaseItem = createLeaseItem(lease, LeaseItemType.SERVICE_CHARGE, charges.findChargeByReference("SERVICE_CHARGE"));
        LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseTerms.newLeaseTerm(leaseItem);
        leaseTerm.modifyLeaseItem(leaseItem);
        leaseTerm.setStartDate(startDate);
        leaseTerm.setEndDate(endDate);
        leaseTerm.setBudgetedValue(budgetedValue);
        leaseTerm.setAuditedValue(budgetedValue.multiply(BigDecimal.valueOf(1.1)));
        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        return leaseTerm;
    }

    
    // {{ injected
    private Indices indices;
    public void injectIndices(final Indices indices) {
        this.indices = indices;
    }

    private Units units;
    public void injectUnits(final Units units) {
        this.units = units;
    }

    private Leases leases;
    public void injectLeases(final Leases leases) {
        this.leases = leases;
    }

    private LeaseUnits leaseUnits;
    public void injectLeaseUnits(final LeaseUnits leaseUnits) {
        this.leaseUnits = leaseUnits;
    }

    private LeaseUnitReferences leaseUnitReferences;
    public void injectLeaseUnitReferences(LeaseUnitReferences leaseUnitReferences) {
        this.leaseUnitReferences = leaseUnitReferences;
    }

    private LeaseTerms leaseTerms;
    public void injectLeaseTerms(final LeaseTerms leaseTerms) {
        this.leaseTerms = leaseTerms;
    }

    private Parties parties;
    public void injectParties(final Parties parties) {
        this.parties = parties;
    }

    private Charges charges;
    public void injectCharges(final Charges charges) {
        this.charges = charges;
    }

    private AgreementTypes agreementTypes;
    public void injectAgreementTypes(final AgreementTypes agreementTypes) {
        this.agreementTypes = agreementTypes;
    }

    private AgreementRoleTypes agreementRoleTypes;
    public void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }
    // }}

}
