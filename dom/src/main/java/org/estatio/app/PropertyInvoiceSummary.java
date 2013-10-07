/*
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
package org.estatio.app;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.ViewModel;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;

/**
 * View model that surfaces information about each property along with summary details of its
 * invoices in their various states.
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.NONDURABLE,
        table = "PropertyInvoiceSummary",
        extensions = {
                @Extension(vendorName = "datanucleus", key = "view-definition",
                        value = "CREATE VIEW \"PropertyInvoiceSummary\" "
                                + "( "
                                + "{this.reference}, "
                                + "{this.name} "
                                + ") AS "
                                + "SELECT \"reference\", \"name\" "
                                + "FROM \"FixedAsset\" "
                                + "WHERE \"discriminator\" = 'org.estatio.dom.asset.Property'")
        })
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Bookmarkable
@Immutable
public class PropertyInvoiceSummary
        extends EstatioDomainObject<PropertyInvoiceSummary>
        implements ViewModel {

    // //////////////////////////////////////

    public PropertyInvoiceSummary() {
        super("name");
    }

    // //////////////////////////////////////

    /**
     * {@link ViewModel} implementation.
     */
    @Override
    public String viewModelMemento() {
        return getReference();
    }

    /**
     * {@link ViewModel} implementation.
     */
    @Override
    public void viewModelInit(final String memento) {
        setReference(memento);
    }
    
    // //////////////////////////////////////

    private String reference;
    
    /**
     * Used as the {@link #viewModelMemento() view model memento}, holds the reference of the
     * corresponding {@link #getProperty()}.
     * 
     * <p>
     * This attribute is always guaranteed to be populated.
     */
    @javax.jdo.annotations.Column(allowsNull="false")
    @DescribedAs("Unique reference code for this property")
    @Title(sequence = "1", prepend = "[", append = "] ")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    /**
     * Annotated as {@link javax.jdo.annotations.NotPersistent not persistent} because not 
     * mapped in the <tt>view-definition</tt>.
     */
    @javax.jdo.annotations.NotPersistent
    private Property property;

    /**
     * Lazily loaded from the {@link #getReference() reference}, provides access to the underlying
     * {@link Property}.
     */
    @Optional
    public Property getProperty() {
        if(property == null) {
            setProperty(properties.findPropertyByReference(getReference()));
        }
        return property;
    }

    public void setProperty(final Property property) {
        this.property = property;
    }


    // //////////////////////////////////////

    private String name;
    
    /**
     * The name of the underlying {@link #getProperty()}.
     * 
     * <p>
     * Either populated directly by the JDO/DataNucleus objectstore, or else is lazily derived from 
     * the {@link #getProperty()} if this view model is rehydrated subsequently by Isis.
     */
    @javax.jdo.annotations.Column(allowsNull = "false")
    @DescribedAs("Unique name for this property")
    @Title(sequence = "2")
    public String getName() {
        return name != null? name: (name = getProperty().getName());
    }
    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    private Properties properties;

    public void injectProperties(final Properties properties) {
        this.properties = properties;
    }

}
