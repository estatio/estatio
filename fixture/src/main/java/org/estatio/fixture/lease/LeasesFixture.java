package org.estatio.fixture.lease;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.estatio.dom.Status;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleTypes;
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
import org.estatio.dom.lease.LeaseTermForTurnoverRent;
import org.estatio.dom.lease.LeaseTermFrequency;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.LeaseUnit;
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

        manager = parties.findPartyByReferenceOrName("JDOE");
        Lease lease1 = createLease("OXF-TOPMODEL-001", "Topmodel Lease", "OXF-001", "ACME", "TOPMODEL", new LocalDate(2010, 7, 15), new LocalDate(2022, 7, 14));
        createLeaseTermForIndexableRent(lease1, BigInteger.valueOf(1), lease1.getStartDate(), null, BigDecimal.valueOf(20000), new LocalDate(2010, 7, 1), new LocalDate(2011, 1, 1), new LocalDate(2011, 4, 1));
        createLeaseTermForServiceCharge(lease1, lease1.getStartDate(), null, BigDecimal.valueOf(6000));
        createLeaseTermForTurnoverRent(lease1, lease1.getStartDate().withDayOfYear(1).plusYears(1), null, "7");
        
        Lease lease2 = createLease("OXF-MEDIAX-002", "Meadiax Lease", "OXF-002", "ACME", "MEDIAX", new LocalDate(2008, 1, 1), new LocalDate(2017, 12, 31));
        createLeaseTermForIndexableRent(lease2, BigInteger.valueOf(1), lease2.getStartDate(), null, BigDecimal.valueOf(20000), new LocalDate(2008, 1, 1), new LocalDate(2009, 1, 1), new LocalDate(2009, 4, 1));
        createLeaseTermForServiceCharge(lease2, lease2.getStartDate(), null, BigDecimal.valueOf(6000));
        createLeaseTermForTurnoverRent(lease2, lease2.getStartDate(), null, "7");

        Lease lease3 = createLease("OXF-POISON-003", "Poison Lease", "OXF-003", "ACME", "POISON", new LocalDate(2011, 1, 1), new LocalDate(2020, 12, 31));
        createLeaseTermForIndexableRent(lease3, BigInteger.valueOf(1), lease3.getStartDate(), null, BigDecimal.valueOf(87300), null, null, null);
        createLeaseTermForIndexableRent(lease3, BigInteger.valueOf(2), lease3.getStartDate().plusYears(1), null, BigDecimal.valueOf(87300), new LocalDate(2011, 1, 1), new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1));
        createLeaseTermForServiceCharge(lease3, lease3.getStartDate(), null, BigDecimal.valueOf(12400));
        createLeaseTermForTurnoverRent(lease3, lease3.getStartDate(), null, "7");

    }

    private Lease createLease(String reference, String name, String unitReference, String landlordReference, String tenantReference, LocalDate startDate, LocalDate endDate) {
        Party landlord = parties.findPartyByReferenceOrName(landlordReference);
        Party tenant = parties.findPartyByReferenceOrName(tenantReference);
        UnitForLease unit = (UnitForLease) units.findUnitByReference(unitReference);
        Lease lease = leases.newLease(reference, name, startDate, null, endDate, landlord, tenant);
        AgreementRole role = lease.addRole(manager, agreementRoleTypes.findByTitle(LeaseConstants.ART_MANAGER), null, null);
        LeaseUnit lu = leaseUnits.newLeaseUnit(lease, unit);

        lu.setBrand(tenantReference);
        lu.setActivity("OTHER");
        lu.setSector("OTHER");
        
        if (leases.findLeaseByReference(reference) == null) {
            throw new RuntimeException("could not find lease reference='" + reference + "'");
        }
        return lease;
    }

    private LeaseItem findOrCreateLeaseItem(Lease lease, LeaseItemType leaseItemType, Charge charge, InvoicingFrequency invoicingFrequency) {
        LeaseItem li = lease.findItem(leaseItemType, lease.getStartDate(), BigInteger.ONE);
        if (li == null) {
            li = lease.newItem(leaseItemType);
            // li = leaseItems.newLeaseItem(lease, leaseItemType);
            li.setType(leaseItemType);
            li.setInvoicingFrequency(invoicingFrequency);
            li.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
            li.setCharge(charge);
            li.setStartDate(lease.getStartDate());
            li.setEndDate(lease.getEndDate());
            li.setSequence(BigInteger.valueOf(1));
        }
        return li;
    }

    private LeaseTerm createLeaseTermForIndexableRent(Lease lease1, BigInteger sequence, LocalDate startDate, LocalDate endDate, BigDecimal value, LocalDate baseIndexDate, LocalDate nextIndexDate, LocalDate indexationApplicationDate) {
        LeaseItem leaseItem = findOrCreateLeaseItem(lease1, LeaseItemType.RENT, charges.findChargeByReference("RENT"), InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        LeaseTermForIndexableRent leaseTerm;
        if (sequence.equals(BigInteger.ONE)) {
            leaseTerm = (LeaseTermForIndexableRent) leaseItem.createInitialTerm();
        } else {
            LeaseTerm currentTerm = leaseItem.findTermWithSequence(sequence.subtract(BigInteger.ONE));
            leaseTerm = (LeaseTermForIndexableRent) leaseItem.createNextTerm(currentTerm);
        }
        leaseTerm.modifyStartDate(startDate);
        leaseTerm.setEndDate(endDate);
        leaseTerm.setBaseValue(value);
        leaseTerm.setBaseIndexStartDate(baseIndexDate);
        leaseTerm.setNextIndexStartDate(nextIndexDate);
        leaseTerm.setEffectiveDate(indexationApplicationDate);
        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        leaseTerm.setIndex(indices.findIndexByReference("ISTAT-FOI"));
        leaseTerm.setSequence(sequence);
        return leaseTerm;
    }

    private LeaseTerm createLeaseTermForServiceCharge(Lease lease, LocalDate startDate, LocalDate endDate, BigDecimal budgetedValue) {
        LeaseItem leaseItem = findOrCreateLeaseItem(lease, LeaseItemType.SERVICE_CHARGE, charges.findChargeByReference("SERVICE_CHARGE"), InvoicingFrequency.QUARTERLY_IN_ADVANCE);
        LeaseTermForServiceCharge leaseTerm = (LeaseTermForServiceCharge) leaseTerms.newLeaseTerm(leaseItem);
        leaseTerm.modifyStartDate(startDate);
        leaseTerm.setEndDate(endDate);
        leaseTerm.setBudgetedValue(budgetedValue);
        leaseTerm.setAuditedValue(budgetedValue.multiply(BigDecimal.valueOf(1.1)));
        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        return leaseTerm;
    }

    private LeaseTerm createLeaseTermForTurnoverRent(Lease lease, LocalDate startDate, LocalDate endDate, String turnoverRentRule) {
        LeaseItem leaseItem = findOrCreateLeaseItem(lease, LeaseItemType.TURNOVER_RENT, charges.findChargeByReference("TURNOVER_RENT"), InvoicingFrequency.YEARLY_IN_ARREARS);
        LeaseTermForTurnoverRent leaseTerm = (LeaseTermForTurnoverRent) leaseTerms.newLeaseTerm(leaseItem);
        leaseTerm.modifyStartDate(startDate);
        leaseTerm.setEndDate(endDate);
        leaseTerm.setFrequency(LeaseTermFrequency.YEARLY);
        leaseTerm.setTurnoverRentRule(turnoverRentRule);
        return leaseTerm;
    }

    private Indices indices;

    public void injectIndices(final Indices indices) {
        this.indices = indices;
    }

    private Units<Unit> units;

    public void injectUnits(final Units<Unit> units) {
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

    private AgreementRoleTypes agreementRoleTypes;

    public void injectAgreementRoleTypes(final AgreementRoleTypes agreementRoleTypes) {
        this.agreementRoleTypes = agreementRoleTypes;
    }

}
