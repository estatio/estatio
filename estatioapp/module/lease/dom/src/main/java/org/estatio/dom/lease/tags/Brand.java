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

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.NameType;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.country.dom.impl.Country;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Unique(name = "Brand_name_atPath_UNQ", members = {"name", "applicationTenancyPath"})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.tags.Brand "
                        + "WHERE name == :name"),
        @javax.jdo.annotations.Query(
                name = "findByNameAndAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.tags.Brand "
                        + "WHERE name == :name && applicationTenancyPath == :atPath "),
        @javax.jdo.annotations.Query(
                name = "findByNameLowerCaseAndAppTenancy", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.tags.Brand "
                        + "WHERE name.toLowerCase() == :name && applicationTenancyPath == :atPath "),
        @javax.jdo.annotations.Query(
                name = "matchByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.lease.tags.Brand "
                        + "WHERE name.matches(:name)"),
        @javax.jdo.annotations.Query(
                name = "findUniqueNames", language = "JDOQL",
                value = "SELECT name "
                        + "FROM org.estatio.dom.lease.tags.Brand ORDER BY name"),
        @javax.jdo.annotations.Query(
                name = "findUniqueGroups", language = "JDOQL",
                value = "SELECT DISTINCT this.group "
                        + "FROM org.estatio.dom.lease.tags.Brand WHERE this.group != null "
                        + "ORDER BY this.group ")
})
@DomainObject(
        editing = Editing.DISABLED,
        autoCompleteRepository = BrandRepository.class,
        autoCompleteAction = "autoComplete",
        objectType = "org.estatio.dom.lease.tags.Brand"
)
public class Brand
        extends UdoDomainObject2<Brand>
        implements WithApplicationTenancyCountry, WithApplicationTenancyPathPersisted {

    public Brand() {
        super("name, applicationTenancyPath");
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getName())
                .toString();
    }

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String applicationTenancyPath;

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }

    // //////////////////////////////////////

    @Column(allowsNull = "false", length = NameType.Meta.MAX_LEN)
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @Column(allowsNull = "true")
    @Getter @Setter
    private BrandCoverage coverage;

    // //////////////////////////////////////

    @Column(name = "countryOfOriginId", allowsNull = "true")
    @Getter @Setter
    private Country countryOfOrigin;

    // //////////////////////////////////////

    @Column(allowsNull = "true")
    @Getter @Setter
    private String group;

    // //////////////////////////////////////

    public Brand change(
            final String name,
            final @Parameter(optionality = Optionality.OPTIONAL) String group,
            final @Parameter(optionality = Optionality.OPTIONAL) BrandCoverage coverage,
            final @Parameter(optionality = Optionality.OPTIONAL) Country countryOfOrigin) {
        setName(name);
        setGroup(group);
        setCoverage(coverage);
        setCountryOfOrigin(countryOfOrigin);
        return this;
    }

    public String default0Change() {
        return this.getName();
    }

    public String default1Change() {
        return this.getGroup();
    }

    public BrandCoverage default2Change() {
        return this.getCoverage();
    }

    public Country default3Change() {
        return this.getCountryOfOrigin();
    }

    // //////////////////////////////////////

    public static class RemoveEvent extends ActionDomainEvent<Brand> {
        private static final long serialVersionUID = 1L;

        public Brand getReplacement() {
            return (Brand) (this.getArguments().isEmpty() ? null : getArguments().get(0));
        }
    }

    @Action(domainEvent = Brand.RemoveEvent.class)
    public void remove() {
        removeAndReplace(null);
    }

    @Action(domainEvent = Brand.RemoveEvent.class, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public Object removeAndReplace(
            final @Parameter(optionality = Optionality.OPTIONAL) Brand replaceWith) {
        getContainer().remove(this);
        getContainer().flush();

        return replaceWith;
    }

    public String validateRemoveAndReplace(final Brand brand) {
        if (brand == this) {
            return "Cannot replace a brand with itself";
        } else {
            return null;
        }
    }

}
