package com.eurocommercialproperties.estatio.dom.lease;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.runtimes.dflt.objectstores.jdo.applib.annotations.Auditable;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.party.Party;

@PersistenceCapable
@Auditable
public class Lease extends AbstractDomainObject {

    // {{ Reference (property)
    private String reference;

    @MemberOrder(sequence = "1")
    @Title()
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Name (property)
    private String name;

    @MemberOrder(sequence = "2")
    @Optional
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ StartDate (property)
    private LocalDate startDate;

    @Persistent
    @MemberOrder(sequence = "3")
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    // }}

    // {{ EndDate (property)
    private LocalDate endDate;

    @Persistent
    @MemberOrder(sequence = "4")
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // }}

    // {{ TerminationDate (property)
    private LocalDate terminationDate;

    @Persistent
    @MemberOrder(sequence = "5")
    @Optional
    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(final LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    // }}

    // {{ Actors (Collection)
    private Set<LeaseActor> actors = new LinkedHashSet<LeaseActor>();

    @MemberOrder(sequence = "1")
    public Set<LeaseActor> getActors() {
        return actors;
    }

    public void setActors(final Set<LeaseActor> actors) {
        this.actors = actors;
    }

    // }}

    // {{ newActor (action)
    @MemberOrder(sequence = "1")
    public LeaseActor addActor(@Named("party") Party party, @Named("type") LeaseActorType type, @Named("from") @Optional LocalDate from, @Named("thru") @Optional LocalDate thru) {
        LeaseActor LeaseActor = leaseActorsRepo.newLeaseActor(this, party, type, from, thru);
        actors.add(LeaseActor);
        return LeaseActor;
    }

    // {{ injected: LeaseActors
    private LeaseActors leaseActorsRepo;

    public void setLeaseActors(final LeaseActors leaseActors) {
        this.leaseActorsRepo = leaseActors;
    }

    // }}

    // {{ Units (Collection)

    @Persistent(mappedBy = "lease", defaultFetchGroup = "false")
    private Set<LeaseUnit> units = new LinkedHashSet<LeaseUnit>();

    @MemberOrder(sequence = "1")
    public Set<LeaseUnit> getUnits() {
        return units;
    }

    public void setUnits(final Set<LeaseUnit> units) {
        this.units = units;
    }
    // }}

}
