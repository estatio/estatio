package org.estatio.dom.lease;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.filter.Filter;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

@Named("Leases")
public class Leases extends AbstractFactoryAndRepository {

    // {{ Id, iconName
    @Override
    public String getId() {
        return "leases";
    }

    public String iconName() {
        return "Lease";
    }

    // }}

    // {{ newLease
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Lease newLease(final @Named("Reference") String reference, final @Named("Name") String name) {
        Lease lease = newTransientInstance(Lease.class);
        lease.setReference(reference);
        lease.setName(name);
        persist(lease);
        return lease;
    }

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Lease newLease2(
            final @Named("Reference") String reference, 
            final @Named("Name") String name,
            final @Named("Start Date") LocalDate startDate,
            final @Named("End Date") LocalDate endDate,
            final @Optional @Named("Landlord") Party landlord,
            final @Optional @Named("Tentant") Party tenant
            ) {
        Lease lease = newTransientInstance(Lease.class);
        lease.setReference(reference);
        lease.setName(name);
        lease.setStartDate(startDate);
        lease.setEndDate(endDate);
        persist(lease);
        lease.addActor(tenant, LeaseActorType.TENANT, startDate, null);
        lease.addActor(landlord, LeaseActorType.LANDLORD, startDate, null);
        return lease;
    }

    // }}

    // {{ findByReference
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Lease> findLeasesByReference(final @Named("Reference") String reference) {
        throw new NotImplementedException();
    }

    // }}

    // {{ findByReference
    @Hidden
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public Lease findByReference(final @Named("Reference") String reference) {
        return firstMatch(Lease.class, new Filter<Lease>() {
            @Override
            public boolean accept(final Lease lease) {
                return reference.equals(lease.getReference());
            }
        });
    }

    // }}

    public void calculate(
            final @Named("Lease reference") String leaseReference,
            final @Named("Due date") LocalDate dueDate
            ){
        List<Lease> leases = findLeasesByReference(leaseReference);
        for (Lease lease : leases) {
            lease.calculate(dueDate);
        }
    }    
    
    // {{ allLeases
    @Prototype
    @ActionSemantics(Of.SAFE)
    public List<Lease> allLeases() {
        return allInstances(Lease.class);
    }

    // }}

//    @ActionSemantics(Of.NON_IDEMPOTENT)
//    public void verifySelected(Lease lease) {
//        lease.verify();
//    }
}
