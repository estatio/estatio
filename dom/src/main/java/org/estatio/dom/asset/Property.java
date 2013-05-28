package org.estatio.dom.asset;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.PublishedAction;
import org.apache.isis.applib.annotation.PublishedObject;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;

import org.estatio.dom.geography.Country;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy=InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Query(name = "properties", language = "JDOQL", value = "SELECT FROM org.estatio.dom.asset.Property WHERE reference.matches(:r)")
@AutoComplete(repository = Properties.class)
@Bookmarkable
public class Property extends FixedAsset {

    
    // {{ Type (attribute)
    private PropertyType propertyType;

    @MemberOrder(sequence = "1.3")
    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(final PropertyType type) {
        this.propertyType = type;
    }

    // }}

    // {{ OpeningDate (attribute)
    @javax.jdo.annotations.Persistent
    private LocalDate openingDate;

    @MemberOrder(sequence = "1.4")
    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(final LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    // }}

    // {{ AcquireDate (attribute)
    @javax.jdo.annotations.Persistent
    private LocalDate acquireDate;

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
    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal area;

    @MemberOrder(sequence = "1.7")
    public BigDecimal getArea() {
        return area;
    }

    public void setArea(final BigDecimal area) {
        this.area = area;
    }

    // }}

    // {{ City (property)
    private String city;

    @MemberOrder(sequence = "1.8")
    public String getCity() {
        return city;
    }

    public void setCity(final String propertyName) {
        this.city = propertyName;
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

    // {{ Units (set, bidir)
    @javax.jdo.annotations.Persistent(mappedBy = "property")
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
        return unit;
    }

    // }}

    // {{ injected services

    private Units unitsRepo;
    public void injectUnitsRepo(final Units unitsRepo) {
        this.unitsRepo = unitsRepo;
    }

    // }}

}
