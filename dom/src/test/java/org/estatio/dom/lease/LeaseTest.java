package org.estatio.dom.lease;


import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.party.Organisation;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

public class LeaseTest {

    private Lease lease ;
    private Organisation org;
    
    @Mock
    LeaseActors leaseActors;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {
        lease = new Lease();
        org = new Organisation();
        LeaseActor la = new LeaseActor();
        la.setParty(org);
        la.setStartDate(new LocalDate(2000,1,1));
        la.setType(LeaseActorType.TENANT);
        la.modifyLease(lease);
    }

    @Ignore
    @Test
    public void findActorIsNotNull() {
        Assert.assertNotNull(lease.findRole(org, AgreementRoleType.TENANT, new LocalDate(2000,1,1)));
    }
    
    @Ignore
    @Test
    public void addActorIsIdempotent() {
        lease.addRole(org, AgreementRoleType.TENANT, new LocalDate(2000,1,1), null);
        Assert.assertEquals(1, lease.getRoles().size());
    }
    
}
