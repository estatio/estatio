package com.eurocommercialproperties.estatio.dom.asset;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannel;
import com.eurocommercialproperties.estatio.dom.communicationchannel.CommunicationChannelType;
import com.eurocommercialproperties.estatio.dom.lease.LeaseUnit;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Resolve;
import org.apache.isis.applib.annotation.Resolve.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.runtimes.dflt.objectstores.jdo.applib.annotations.Auditable;

@PersistenceCapable
@Auditable
public class Unit extends AbstractDomainObject {

    // {{ Reference (attribute, title)
    private String reference;

    @Title(sequence = "1", append = ", ")
    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String code) {
        this.reference = code;
    }

    // }}

    // {{ Name (attribute, title)
    private String name;

    @Disabled
    @Title(sequence = "2")
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ Type (attribute)
    private UnitType type;

    @MemberOrder(sequence = "3")
    public UnitType getType() {
        return type;
    }

    public void setType(final UnitType type) {
        this.type = type;
    }

    public List<UnitType> choicesType() {
        return Arrays.asList(UnitType.values());
    }

    // }}

    // {{ Area (attribute)
    private BigDecimal area;

    @MemberOrder(sequence = "4")
    public BigDecimal getArea() {
        return area;
    }

    public void setArea(final BigDecimal area) {
        this.area = area;
    }

    // }}

    // {{ StorageArea (property)
    private BigDecimal storageArea;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "5")
    public BigDecimal getStorageArea() {
        return storageArea;
    }

    public void setStorageArea(final BigDecimal storageArea) {
        this.storageArea = storageArea;
    }

    // }}

    // {{ SalesArea (property)
    private BigDecimal salesArea;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "6")
    public BigDecimal getSalesArea() {
        return salesArea;
    }

    public void setSalesArea(final BigDecimal salesArea) {
        this.salesArea = salesArea;
    }

    // }}

    // {{ MezzanineArea (property)
    private BigDecimal mezzanineArea;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "7")
    public BigDecimal getMezzanineArea() {
        return mezzanineArea;
    }

    public void setMezzanineArea(final BigDecimal mezzanineArea) {
        this.mezzanineArea = mezzanineArea;
    }

    // }}

    // {{ TerraceArea (property)
    private BigDecimal terraceArea;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "8")
    public BigDecimal getTerraceArea() {
        return terraceArea;
    }

    public void setTerraceArea(final BigDecimal terraceArea) {
        this.terraceArea = terraceArea;
    }

    // }}

    // {{ Property (attribute)
    private Property property;

    @javax.jdo.annotations.Column(name = "PROPERTY_ID")
    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    @MemberOrder(sequence = "9")
    public Property getProperty() {
        return property;
    }

    public void setProperty(final Property property) {
        this.property = property;
    }

    // }}

    // {{ Leases (Collection)
    @Persistent(mappedBy = "unit", defaultFetchGroup = "false")
    private Set<LeaseUnit> leases = new LinkedHashSet<LeaseUnit>();

    @Resolve(Type.EAGERLY)
    @MemberOrder(sequence = "2.2")
    public Set<LeaseUnit> getLeases() {
        return leases;
    }

    public void setLeases(final Set<LeaseUnit> leases) {
        this.leases = leases;
    }

    // }}

    // {{ CommunicationChannels (list, unidir)
    @Join(column="UNIT_ID", generateForeignKey = "false")
    @Element(column = "COMMUNICATIONCHANNEL_ID", generateForeignKey = "false")
    private Set<CommunicationChannel> communicationChannels = new LinkedHashSet<CommunicationChannel>();

    @Resolve(Type.EAGERLY)
    @MemberOrder(sequence = "2.1")
    public Set<CommunicationChannel> getCommunicationChannels() {
        return communicationChannels;
    }

    public void setCommunicationChannels(final Set<CommunicationChannel> communicationChannels) {
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

}
