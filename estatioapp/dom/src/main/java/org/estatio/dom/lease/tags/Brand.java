/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.dom.lease.tags;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.eventbus.ActionInteractionEvent;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithNameComparable;
import org.estatio.dom.WithNameUnique;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.dom.geography.Country;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Unique(name = "Brand_name_UNQ", members = {"name"})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.tags.Brand "
                        + "WHERE name == :name"),
        @javax.jdo.annotations.Query(
                name = "matchByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.tags.Brand "
                        + "WHERE name.matches(:name)"),
        @javax.jdo.annotations.Query(
                name = "findUniqueNames", language = "JDOQL",
                value = "SELECT name "
                        + "FROM org.estatio.dom.lease.tags.Brand")
})
@DomainObject(editing = Editing.DISABLED, autoCompleteRepository = Brands.class, autoCompleteAction = "autoComplete")
public class Brand
        extends EstatioDomainObject<Brand>
        implements WithNameUnique, WithNameComparable<Brand>, WithApplicationTenancyCountry, WithApplicationTenancyPathPersisted {

    public Brand() {
        super("name");
    }

    // //////////////////////////////////////

    private String applicationTenancyPath;

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )

    @Property(hidden = Where.EVERYWHERE)
    public String getApplicationTenancyPath() {
        return applicationTenancyPath;
    }

    public void setApplicationTenancyPath(final String applicationTenancyPath) {
        this.applicationTenancyPath = applicationTenancyPath;
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return applicationTenancies.findTenancyByPath(getApplicationTenancyPath());
    }

    // //////////////////////////////////////

    private String name;

    @Column(allowsNull = "false", length = JdoColumnLength.NAME)
    @Title
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    private BrandCoverage coverage;

    @Column(allowsNull = "true")
    public BrandCoverage getCoverage() {
        return coverage;
    }

    public void setCoverage(BrandCoverage coverage) {
        this.coverage = coverage;
    }

    // //////////////////////////////////////

    private Country countryOfOrigin;

    @Column(allowsNull = "true")
    public Country getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(Country countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    // //////////////////////////////////////

    private Brand parentBrand;

    @Column(allowsNull = "true")
    public Brand getParentBrand() {
        return parentBrand;
    }

    public void setParentBrand(Brand parentBrand) {
        this.parentBrand = parentBrand;
    }

    // //////////////////////////////////////

    public Brand change(
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Parent brand") @Parameter(optionality = Optionality.OPTIONAL) Brand parentBrand,
            final @ParameterLayout(named = "Coverage") @Parameter(optionality = Optionality.OPTIONAL) BrandCoverage brandCoverage,
            final @ParameterLayout(named = "Country of origin") @Parameter(optionality = Optionality.OPTIONAL) Country countryOfOrigin) {
        setName(name);
        setParentBrand(parentBrand);
        setCoverage(brandCoverage);
        setCountryOfOrigin(countryOfOrigin);
        return this;
    }

    public String default0Change() {
        return this.getName();
    }

    public Brand default1Change() {
        return this.getParentBrand();
    }

    public BrandCoverage default2Change() {
        return this.getCoverage();
    }

    public Country default3Change() {
        return this.getCountryOfOrigin();
    }

    // //////////////////////////////////////

    public static class RemoveEvent extends ActionInteractionEvent<Brand> {
        private static final long serialVersionUID = 1L;

        public RemoveEvent(
                final Brand source,
                final Identifier identifier,
                final Object... arguments) {
            super(source, identifier, arguments);
        }

        public Brand getReplacement() {
            return (Brand) (this.getArguments().isEmpty() ? null : getArguments().get(0));
        }
    }

    @Action(domainEvent = Brand.RemoveEvent.class)
    public void remove() {
        removeAndReplace(null, true);
    }

    @Action(domainEvent = Brand.RemoveEvent.class)
    public Object removeAndReplace(
            final @ParameterLayout(named = "Replace with") @Parameter(optionality = Optionality.OPTIONAL) Brand replacement,
            final @ParameterLayout(named = "Are you sure?") boolean confirm) {
        getContainer().remove(this);
        getContainer().flush();

        return replacement;
    }

    public String validateRemoveAndReplace(final Brand brand, final boolean confirm) {
        if (brand == this) {
            return "Cannot replace a brand with itself";
        } else if (confirm == false) {
            return "Please confirm this replacement";
        } else {
            return null;
        }
    }

}
