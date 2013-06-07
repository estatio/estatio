package org.estatio.dom.asset;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Where;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@AutoComplete(repository = Units.class)
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class Unit extends FixedAsset {

    private UnitType unitType;

    @MemberOrder(sequence = "3")
    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(final UnitType type) {
        this.unitType = type;
    }

    public List<UnitType> choicesUnitType() {
        return Arrays.asList(UnitType.values());
    }

    
    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal area;

    @MemberOrder(sequence = "4")
    public BigDecimal getArea() {
        return area;
    }

    public void setArea(final BigDecimal area) {
        this.area = area;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal storageArea;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "5")
    public BigDecimal getStorageArea() {
        return storageArea;
    }

    public void setStorageArea(final BigDecimal storageArea) {
        this.storageArea = storageArea;
    }

    
    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal salesArea;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "6")
    public BigDecimal getSalesArea() {
        return salesArea;
    }

    public void setSalesArea(final BigDecimal salesArea) {
        this.salesArea = salesArea;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal mezzanineArea;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "7")
    public BigDecimal getMezzanineArea() {
        return mezzanineArea;
    }

    public void setMezzanineArea(final BigDecimal mezzanineArea) {
        this.mezzanineArea = mezzanineArea;
    }

    
    // //////////////////////////////////////

    @javax.jdo.annotations.Column(scale = 2)
    private BigDecimal terraceArea;

    @Hidden(where = Where.PARENTED_TABLES)
    @MemberOrder(sequence = "8")
    public BigDecimal getTerraceArea() {
        return terraceArea;
    }

    public void setTerraceArea(final BigDecimal terraceArea) {
        this.terraceArea = terraceArea;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "PROPERTY_ID")
    private Property property;

    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    @MemberOrder(sequence = "9")
    public Property getProperty() {
        return property;
    }

    public void setProperty(final Property property) {
        this.property = property;
    }


}