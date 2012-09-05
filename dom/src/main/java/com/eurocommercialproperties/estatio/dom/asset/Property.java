package com.eurocommercialproperties.estatio.dom.asset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.commons.lang.NotImplementedException;
import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.runtimes.dflt.objectstores.jdo.applib.annotations.Auditable;
import org.joda.time.LocalDate;

import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannel;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannelType;
import com.eurocommercialproperties.estatio.dom.communicationchannel.PostalAddress;
import com.eurocommercialproperties.estatio.dom.party.Party;

@PersistenceCapable
@Auditable
public class Property extends AbstractDomainObject {
    
    
    // {{ Reference (attribute, title)
    private String reference;

    @Title(sequence = "1", prepend = "[", append = "] ")
    @Disabled
    @MemberOrder(sequence = "1.1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String code) {
        this.reference = code;
    }

    // }}

    // {{ Name (attribute, title)
    private String name;

    @Title(sequence = "2")
    @Disabled
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
    // REVIEW: should a BigDecimal be used instead?
    private Double area;

    @MemberOrder(sequence = "1.7")
    public Double getArea() {
        return area;
    }

    public void setArea(final Double area) {
        this.area = area;
    }

    // }}

    // {{ AreaOfUnits (attribute)
    @MemberOrder(sequence = "1.8")
    public BigDecimal getAreaOfUnits() {
        BigDecimal area = BigDecimal.ZERO ;
        for (Unit unit : getUnits()) {
            area.add(unit.getArea() !=null ? unit.getArea() : BigDecimal.ZERO);
        }
        return area;
    }

    
    // }}

    // {{ City (derived attribute)
    @MemberOrder(sequence = "1.9")
    public String getCity() {
        // TODO: Ugly piece of code
        for (CommunicationChannel communicationChannel : getCommunicationChannels()) {
            if (communicationChannel instanceof PostalAddress) {
                return ((PostalAddress) communicationChannel).getCity();
            }
        }
        return "";
    }

    // }}

    // {{ CommunicationChannels (list, unidir)
    //@Persistent(mappedBy = "property", defaultFetchGroup="false")
    @Join
    private List<CommunicationChannel> communicationChannels = new ArrayList<CommunicationChannel>();

    @MemberOrder(sequence = "2.1")
    public List<CommunicationChannel> getCommunicationChannels() {
        return communicationChannels;
    }

    public void setCommunicationChannels(final List<CommunicationChannel> communicationChannels) {
        this.communicationChannels = communicationChannels;
    }

    public CommunicationChannel addCommunicationChannel(final CommunicationChannelType communicationChannelType) {
        CommunicationChannel communicationChannel = communicationChannelType.create(getContainer());
        communicationChannels.add(communicationChannel);
        return communicationChannel;
    }

    @Hidden
    public void addCommunicationChannel(CommunicationChannel communicationChannel) {
        communicationChannels.add(communicationChannel);
    }

    // }}
    
    // {{ PostalAddress
    @Hidden
    @Programmatic //
    public PostalAddress getPostalAddress() {
        // TODO: Return the first or primary postal address. Q: should this implemented on the repository?
        // 
        return null;
    }

    // {{ Units (list, bidir)
    @Persistent(mappedBy = "property", defaultFetchGroup="false")
    //TODO: What's the reason for having lists and not sets? Probably none. A property cannot have a duplicate unit.
    private List<Unit> units = new ArrayList<Unit>();

    @MemberOrder(sequence = "2.2")
    public List<Unit> getUnits() {
        return units;
    }

    public void setUnits(final List<Unit> units) {
        this.units = units;
    }

    // }}

    // {{ newUnit (action)
    @MemberOrder(name = "Units", sequence = "1")
    public Unit newUnit(@Named("Code") final String code, @Named("Name") final String name) {
        Unit unit = unitsRepo.newUnit(code, name);
        unit.setProperty(this);
        getUnits().add(unit);
        return unit;
    }
    
    // }}

    // {{ PropertyActors (list, unidir)
    @Persistent(mappedBy="property")
    private List<PropertyActor> actors = new ArrayList<PropertyActor>();

    @MemberOrder(sequence = "1")
    public List<PropertyActor> getActors() {
        return actors;
    }

    public void setActors(final List<PropertyActor> actors) {
        this.actors = actors;
    }

    // }}

    // {{ newActor (action)
    @MemberOrder(sequence = "1")
    public PropertyActor addActor(@Named ("party") Party party, @Named ("type") PropertyActorType type, @Named ("startDate") @Optional LocalDate startDate, @Named ("endDate") @Optional LocalDate endDate) {
        PropertyActor propertyActor = propertyActorsRepo.newPropertyActor(this, party, type, startDate, endDate);
        actors.add(propertyActor);
        return propertyActor;
    }

    // }}

    // {{ injected: Units
    private Units unitsRepo;

    public void setUnits(final Units unitsRepo) {
        this.unitsRepo = unitsRepo;
    }

    // }}

    // {{ injected: PropertyActors
    private PropertyActors propertyActorsRepo;

    public void setPropertyActors(final PropertyActors propertyActors) {
        this.propertyActorsRepo = propertyActors;
    }

    // }}

}
