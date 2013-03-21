package org.estatio.dom.asset;

import java.math.BigDecimal;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.geography.Country;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Mask;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.PublishedAction;
import org.apache.isis.applib.annotation.PublishedObject;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable
@Version(strategy=VersionStrategy.VERSION_NUMBER, column="VERSION")
@AutoComplete(repository = Properties.class)
@PublishedObject
public class Property extends EstatioTransactionalObject implements Comparable<Property> {

    
    // {{ Reference (attribute, title)
    private String reference;

    @DescribedAs("Unique reference code for this property")
    @Unique(name="REFERENCE_IDX")
    @Title(sequence = "1", prepend = "[", append = "] ")
    @MemberOrder(sequence = "1.1")
    @Mask("AAAAAAAA")
    public String getReference() {
        return reference;
    }

    public void setReference(final String code) {
        this.reference = code;
    }
    // }}

    // {{ Name (attribute, title)
    private String name;

    @DescribedAs("Unique reference code for this property")
    @Title(sequence = "2")
    @MemberOrder(sequence = "1.2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ Type (attribute)
    private PropertyType type;

    @MemberOrder(sequence = "1.3")
    public PropertyType getType() {
        return type;
    }

    public void setType(final PropertyType type) {
        this.type = type;
    }

    // }}

    // {{ OpeningDate (attribute)
    private LocalDate openingDate;

    @javax.jdo.annotations.Persistent
    // required for applib.Date
    @MemberOrder(sequence = "1.4")
    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(final LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    // }}

    // {{ AcquireDate (attribute)
    private LocalDate acquireDate;

    @javax.jdo.annotations.Persistent
    // required for applib.Date
    @MemberOrder(sequence = "1.5")
    @Optional
    public LocalDate getAcquireDate() {
        return acquireDate;
    }

    public void setAcquireDate(final LocalDate acquireDate) {
        this.acquireDate = acquireDate;
    }

    // }}

    // {{ Disposal LocalDate (attribute)
    private LocalDate disposalDate;

    @javax.jdo.annotations.Persistent
    // required for applib.Date
    @MemberOrder(sequence = "1.6")
    @Optional
    public LocalDate getDisposalDate() {
        return disposalDate;
    }

    public void setDisposalDate(final LocalDate disposalDate) {
        this.disposalDate = disposalDate;
    }

    // }}

    // {{ Area (attribute)
    private BigDecimal area;

    @MemberOrder(sequence = "1.7")
    @Column(scale=4)
    public BigDecimal getArea() {
        return area;
    }

    public void setArea(final BigDecimal area) {
        this.area = area;
    }

    // }}

    // {{ AreaOfUnits (attribute)
    // @MemberOrder(sequence = "1.8")
    // public BigDecimal getAreaOfUnits() {
    // BigDecimal area = BigDecimal.ZERO;
    // for (Unit unit : getUnits()) {
    // area.add(unit.getArea() != null ? unit.getArea() : BigDecimal.ZERO);
    // }
    // return area;
    // }

    // }}

    
    // {{ City (property)
    private String propertyName;

    @MemberOrder(sequence = "1.8")
    public String getCity() {
        return propertyName;
    }

    public void setCity(final String propertyName) {
        this.propertyName = propertyName;
    }
    // }}

    // {{ Country (property)
    private Country country;

    @MemberOrder(sequence = "1.9")
    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    // }}

    // {{ Actors (list, unidir)
    @Persistent(mappedBy = "property")
    private SortedSet<PropertyActor> actors = new TreeSet<PropertyActor>();

    @Render(Type.EAGERLY)
    @MemberOrder(name = "Actors", sequence = "2.1")
    public SortedSet<PropertyActor> getActors() {
        return actors;
    }

    public void setActors(final SortedSet<PropertyActor> actors) {
        this.actors = actors;
    }

    public void addToActors(final PropertyActor actor) {
        // check for no-op
        if (actor == null || getActors().contains(actor)) {
            return;
        }
        // associate new
        getActors().add(actor);
    }

    public void removeFromActors(final PropertyActor actor) {
        // check for no-op
        if (actor == null || !getActors().contains(actor)) {
            return;
        }
        // dissociate existing
        getActors().remove(actor);
    }

    @MemberOrder(name="Actors", sequence = "1")
    public PropertyActor addActor(
           @Named("party") Party party, 
           @Named("type") PropertyActorType type, 
           @Named("startDate") @Optional LocalDate startDate, 
           @Named("endDate") @Optional LocalDate endDate) {
        PropertyActor propertyActor = propertyActorsRepo.findPropertyActor(this, party, type, startDate, endDate);
        if (propertyActor ==  null) { 
            propertyActor = propertyActorsRepo.newPropertyActor(this, party, type, startDate, endDate);
            actors.add(propertyActor);
        }
        return propertyActor;
    }

    public List<Party> choices0AddActor() {
        return parties.allParties();
    }

    // }}

    // {{ CommunicationChannels (list, unidir)
    @Join(column="PROPERTY_ID", generateForeignKey = "false")
    @Element(column = "COMMUNICATIONCHANNEL_ID", generateForeignKey = "false")
    private SortedSet<CommunicationChannel> communicationChannels = new TreeSet<CommunicationChannel>();

    @Render(Type.EAGERLY)
    @MemberOrder( name = "CommunicationChannels", sequence = "1")
    public SortedSet<CommunicationChannel> getCommunicationChannels() {
        return communicationChannels;
    }

    public void setCommunicationChannels(final SortedSet<CommunicationChannel> communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

    @MemberOrder(name="CommunicationChannels", sequence="1")
    public CommunicationChannel addCommunicationChannel(final CommunicationChannelType communicationChannelType) {
        CommunicationChannel communicationChannel = communicationChannelType.create(getContainer());
        communicationChannels.add(communicationChannel);
        return communicationChannel;
    }
    
    @Hidden
    public CommunicationChannel findCommunicationChannelForType(CommunicationChannelType type){
        for (CommunicationChannel c : communicationChannels){
            if (c.getType().equals(type)){
                return c;
            }
        }
        return null;
    }

    // }}

    // {{ Units (set, bidir)
    @Persistent(mappedBy = "property")
    private SortedSet<Unit> units = new TreeSet<Unit>();
    @Render(Type.EAGERLY)
    @MemberOrder(sequence = "2", name = "Units")
    public SortedSet<Unit> getUnits() {
        return units;
    }

    public void setUnits(final SortedSet<Unit> units) {
        this.units = units;
    }

    // }}

    // {{ NewUnit (action)
    @PublishedAction
    @MemberOrder(name = "Units", sequence = "1")
    public Unit newUnit(@Named("Code") final String code, @Named("Name") final String name) {
        Unit unit = unitsRepo.newUnit(code, name);
        unit.setProperty(this);
        getUnits().add(unit);
        return unit;
    }

    // }}

    
    // {{ injected: Units
    
    private Units unitsRepo;

    public void setUnitsRepo(final Units unitsRepo) {
        this.unitsRepo = unitsRepo;
    }

    private Parties parties;

    public void setParties(Parties parties) {
        this.parties = parties;
    }
    
    // }}

    // {{ injected: PropertyActors
    private PropertyActors propertyActorsRepo;

    public void setPropertyActorsRepo(final PropertyActors propertyActors) {
        this.propertyActorsRepo = propertyActors;
    }

    // }}

    @Override
    public int compareTo(Property other) {
        return this.getName().compareTo(other.getName());
    }

    
}
