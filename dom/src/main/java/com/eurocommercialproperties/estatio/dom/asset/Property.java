package com.eurocommercialproperties.estatio.dom.asset;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;


import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannel;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannelType;
import com.eurocommercialproperties.estatio.dom.communicationchannel.PostalAddress;
import com.eurocommercialproperties.estatio.dom.party.Party;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.ObjectType;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.runtimes.dflt.objectstores.jdo.applib.annotations.Auditable;

@javax.jdo.annotations.PersistenceCapable(schema = "asset", identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@ObjectType("PROP")
@Auditable
public class Property extends AbstractDomainObject {

    // {{ Reference (attribute, title)
    private String reference;

    @Title(sequence = "1", append = ", ")
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
    private Date openingDate;

    @javax.jdo.annotations.Persistent
    // required for applib.Date
    @MemberOrder(sequence = "1.4")
    public Date getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(final Date openingDate) {
        this.openingDate = openingDate;
    }

    // }}

    // {{ AcquireDate (attribute)
    private Date acquireDate;

    @javax.jdo.annotations.Persistent
    // required for applib.Date
    @MemberOrder(sequence = "1.5")
    @Optional
    public Date getAcquireDate() {
        return acquireDate;
    }

    public void setAcquireDate(final Date acquireDate) {
        this.acquireDate = acquireDate;
    }

    // }}

    // {{ Disposal Date (attribute)
    private Date disposalDate;

    @javax.jdo.annotations.Persistent
    // required for applib.Date
    @MemberOrder(sequence = "1.6")
    @Optional
    public Date getDisposalDate() {
        return disposalDate;
    }

    public void setDisposalDate(final Date disposalDate) {
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
            area.add(unit.getArea());
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
    @javax.jdo.annotations.Join(column = "PROPERTY_ID")
    // , generateForeignKey = "false")
    // to avoid FK back to Property
    @javax.jdo.annotations.Element(column = "COMMUNICATIONCHANNEL_ID")
    // , generateForeignKey = "false")
    @javax.jdo.annotations.Order(column = "IDX")
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

    // {{ Units (list, bidir)
    @javax.jdo.annotations.Persistent(mappedBy = "property")
    @javax.jdo.annotations.Order(column = "PROPERTY_UNITS_IDX")
    private List<Unit> units = new ArrayList<Unit>();

    @Disabled
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
        getUnits().add(unit);
        unit.setProperty(this);
        return unit;
    }

    // }}

    // {{ PropertyActors (list, unidir)
    @javax.jdo.annotations.Join(column = "PROPERTY_ID")
    // , generateForeignKey = "false")
    // to avoid FK back to Property
    @javax.jdo.annotations.Element(column = "PROPERTYACTOR_ID")
    // , generateForeignKey = "false")
    @javax.jdo.annotations.Order(column = "IDX")
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
    public PropertyActor addActor(@Named ("party") Party party, @Named ("type") PropertyActorType type, @Named ("from") @Optional Date from, @Named ("thru") @Optional Date thru) {
        PropertyActor propertyActor = propertyActorsRepo.newPropertyActor(this, party, type, from, thru);
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
