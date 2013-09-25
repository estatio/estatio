/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.asset;

import java.math.BigDecimal;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.AutoComplete;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Where;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
//TODO: make name abstract in FixedAsset, then define subclass-specific constraint:
//@javax.jdo.annotations.Unique(name="UNIT_NAME_UNQ_IDX", members={"property","name"})
@AutoComplete(repository = Units.class)
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class Unit extends FixedAsset {


// TODO: make name abstract in FixedAsset 
// (in order to be able to define subclass-specific constraint, see above)

//    private String name;
//
//    @javax.jdo.annotations.Column(allowsNull="false")
//    @DescribedAs("Unique name for this property")
//    @Title(sequence = "2")
//    public String getName() {
//        return name;
//    }
//
//    public void setName(final String name) {
//        this.name = name;
//    }

    // //////////////////////////////////////

    private UnitType unitType;

    @javax.jdo.annotations.Column(allowsNull="false")
    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(final UnitType type) {
        this.unitType = type;
    }

    
    // //////////////////////////////////////

    private BigDecimal area;

    @javax.jdo.annotations.Column(scale = 2, allowsNull="true")
    public BigDecimal getArea() {
        return area;
    }

    public void setArea(final BigDecimal area) {
        this.area = area;
    }

    // //////////////////////////////////////

    private BigDecimal storageArea;

    @javax.jdo.annotations.Column(scale = 2, allowsNull="true")
    @Hidden(where = Where.PARENTED_TABLES)
    public BigDecimal getStorageArea() {
        return storageArea;
    }

    public void setStorageArea(final BigDecimal storageArea) {
        this.storageArea = storageArea;
    }

    
    // //////////////////////////////////////

    private BigDecimal salesArea;

    @javax.jdo.annotations.Column(scale = 2, allowsNull="true")
    @Hidden(where = Where.PARENTED_TABLES)
    public BigDecimal getSalesArea() {
        return salesArea;
    }

    public void setSalesArea(final BigDecimal salesArea) {
        this.salesArea = salesArea;
    }

    // //////////////////////////////////////

    private BigDecimal mezzanineArea;

    @javax.jdo.annotations.Column(scale = 2, allowsNull="true")
    @Hidden(where = Where.PARENTED_TABLES)
    public BigDecimal getMezzanineArea() {
        return mezzanineArea;
    }

    public void setMezzanineArea(final BigDecimal mezzanineArea) {
        this.mezzanineArea = mezzanineArea;
    }

    
    // //////////////////////////////////////

    private BigDecimal terraceArea;

    @javax.jdo.annotations.Column(scale = 2, allowsNull="true")
    @Hidden(where = Where.PARENTED_TABLES)
    public BigDecimal getTerraceArea() {
        return terraceArea;
    }

    public void setTerraceArea(final BigDecimal terraceArea) {
        this.terraceArea = terraceArea;
    }

    // //////////////////////////////////////

    private Property property;

    @javax.jdo.annotations.Column(name = "PROPERTY_ID", allowsNull="false")
    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    public Property getProperty() {
        return property;
    }

    public void setProperty(final Property property) {
        this.property = property;
    }


}