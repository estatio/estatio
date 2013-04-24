package org.estatio.dom.lease;

import junit.framework.Assert;

import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseActor;
import org.estatio.dom.lease.LeaseActorType;
import org.estatio.dom.lease.LeaseActors;
import org.estatio.dom.party.Organisation;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.danhaywood.testsupport.jmock.JUnitRuleMockery2;
import com.danhaywood.testsupport.jmock.JUnitRuleMockery2.Mode;

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
        Assert.assertNotNull(lease.findActor(org, LeaseActorType.TENANT, new LocalDate(2000,1,1)));
    }
    
    @Ignore
    @Test
    public void addActorIsIdempotent() {
        lease.addActor(org, LeaseActorType.TENANT, new LocalDate(2000,1,1), null);
        Assert.assertEquals(1, lease.getActorsWorkaround().size());
    }
    
}
