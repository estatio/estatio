package com.eurocommercialproperties.estatio.dom.lease;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Resolve;
import org.apache.isis.applib.annotation.Resolve.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.runtimes.dflt.objectstores.jdo.applib.annotations.Auditable;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.asset.Unit;
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

    // {{ Type (property)
    private LeaseType type;

    @MemberOrder(sequence = "6")
    public LeaseType getType() {
        return type;
    }

    public void setType(final LeaseType type) {
        this.type = type;
    }

    // }}

    // {{ Actors (Collection)
    @Persistent(mappedBy = "lease")
    private Set<LeaseActor> actors = new LinkedHashSet<LeaseActor>();

    @MemberOrder(sequence = "1")
    public Set<LeaseActor> getActors() {
        return actors;
    }

    public void setActors(final Set<LeaseActor> actors) {
        this.actors = actors;
    }

    public void addToActors(final LeaseActor leaseActor) {
        // check for no-op
        if (leaseActor == null || getActors().contains(leaseActor)) {
            return;
        }
        // associate new
        getActors().add(leaseActor);
        // additional business logic
        onAddToActors(leaseActor);
    }

    public void removeFromActors(final LeaseActor leaseActor) {
        // check for no-op
        if (leaseActor == null || !getActors().contains(leaseActor)) {
            return;
        }
        // dissociate existing
        getActors().remove(leaseActor);
        // additional business logic
        onRemoveFromActors(leaseActor);
    }

    protected void onAddToActors(final LeaseActor elementName) {
    }

    protected void onRemoveFromActors(final LeaseActor elementName) {
    }

    // }}

    // {{ addActor (action)
    @MemberOrder(sequence = "1")
    public LeaseActor addActor(@Named("party") Party party, @Named("type") LeaseActorType type, @Named("startDate") @Optional LocalDate startDate, @Named("endDate") @Optional LocalDate endDate) {
        LeaseActor leaseActor = leaseActors.newLeaseActor(this, party, type, startDate, endDate);
        actors.add(leaseActor);
        return leaseActor;
    }

    // }}

    // {{ Units (Collection)
    private Set<LeaseUnit> units = new LinkedHashSet<LeaseUnit>();

    @Persistent(mappedBy = "lease", defaultFetchGroup = "false")
    @MemberOrder(sequence = "1")
    @Resolve(Type.EAGERLY)
    public Set<LeaseUnit> getUnits() {
        return units;
    }

    public void setUnits(final Set<LeaseUnit> units) {
        this.units = units;
    }

    // }}

    // }}

    public void addToUnits(final LeaseUnit leaseUnit) {
        // check for no-op
        if (leaseUnit == null || getUnits().contains(leaseUnit)) {
            return;
        }
        // associate new
        getUnits().add(leaseUnit);
        // additional business logic
        onAddToUnits(leaseUnit);
    }

    public void removeFromUnits(final LeaseUnit leaseUnit) {
        // check for no-op
        if (leaseUnit == null || !getUnits().contains(leaseUnit)) {
            return;
        }
        // dissociate existing
        getUnits().remove(leaseUnit);
        // additional business logic
        onRemoveFromUnits(leaseUnit);
    }

    protected void onAddToUnits(final LeaseUnit leaseUnit) {
    }

    protected void onRemoveFromUnits(final LeaseUnit leaseUnit) {
    }

    // }}

    // {{ addUnit (action)
    @MemberOrder(sequence = "1")
    public LeaseUnit addUnit(@Named("unit") Unit unit) {
        LeaseUnit leaseUnit = leaseUnits.newLeaseUnit(this, unit);
        units.add(leaseUnit);
        return leaseUnit;
    }

    // {{ Items (Collection)
    private Set<LeaseItem> items = new LinkedHashSet<LeaseItem>();

    @Resolve(Type.EAGERLY)
    @MemberOrder(sequence = "1")
    public Set<LeaseItem> getItems() {
        return items;
    }

    public void setItems(final Set<LeaseItem> items) {
        this.items = items;
    }

    public void addToItems(final LeaseItem leaseItem) {
        // check for no-op
        if (leaseItem == null || getItems().contains(leaseItem)) {
            return;
        }
        // associate new
        getItems().add(leaseItem);
        // additional business logic
        onAddToItems(leaseItem);
    }

    public void removeFromItems(final LeaseItem leaseItem) {
        // check for no-op
        if (leaseItem == null || !getItems().contains(leaseItem)) {
            return;
        }
        // dissociate existing
        getItems().remove(leaseItem);
        // additional business logic
        onRemoveFromItems(leaseItem);
    }

    protected void onAddToItems(final LeaseItem leaseItem) {
    }

    protected void onRemoveFromItems(final LeaseItem leaseItem) {
    }

    // }}

    // {{ addUnit (action)
    @MemberOrder(sequence = "1")
    public LeaseItem addItem() {
        LeaseItem leaseItem = leaseItems.newLeaseItem(this);
        items.add(leaseItem);
        return leaseItem;
    }

    // }}

    // {{ injected: LeaseItems

    private LeaseItems leaseItems;

    public void setLeaseItems(final LeaseItems leaseItems) {
        this.leaseItems = leaseItems;

    }

    // }}

    // {{ injected: LeaseUnits

    private LeaseUnits leaseUnits;

    public void setLeaseUnits(final LeaseUnits leaseUnits) {
        this.leaseUnits = leaseUnits;

    }

    // }}

    // {{ injected: LeaseActors
    private LeaseActors leaseActors;

    public void setLeaseActors(final LeaseActors leaseActors) {
        this.leaseActors = leaseActors;
    }

    // }}
}
