package org.estatio.dom.lease;


import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.party.Organisation;

public class LeaseTest {

    private Lease lease ;
    private Organisation org;
    
    @Mock
    AgreementRoles leaseActors;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);
    private AgreementRoleType tenantArt;

    @Before
    public void setup() {
        tenantArt = new AgreementRoleType();
        lease = new Lease();
        org = new Organisation();
        AgreementRole la = new AgreementRole();
        la.setParty(org);
        la.setStartDate(new LocalDate(2000,1,1));
        la.setType(tenantArt);
        la.modifyAgreement(lease);
    }

    @Ignore
    @Test
    public void findRole() {
        Assert.assertNotNull(lease.findRole(org, tenantArt, new LocalDate(2000,1,1)));
    }
    
    @Ignore
    @Test
    public void addRoleIsIdempotent() {
        lease.addRole(org, tenantArt, new LocalDate(2000,1,1), null);
        Assert.assertEquals(1, lease.getRoles().size());
    }
    
}
