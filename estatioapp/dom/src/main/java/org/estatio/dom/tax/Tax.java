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
package org.estatio.dom.tax;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceComparable;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Tax_reference_UNQ", members = {"reference"})
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.tax.Tax "
                        + "WHERE reference == :reference")
})
@DomainObject(editing = Editing.DISABLED, bounded = true)
public class Tax
        extends EstatioDomainObject<Tax>
        implements WithReferenceComparable<Tax>, WithNameGetter, WithReferenceUnique,
                   WithApplicationTenancyCountry, WithApplicationTenancyPathPersisted {

    public Tax() {
        super("reference");
    }

    // //////////////////////////////////////

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

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.REFERENCE)
    @Title(sequence = "1")
    @Property(regexPattern = RegexValidation.REFERENCE)
    @Getter @Setter
    private String reference;

    // //////////////////////////////////////

    @Title(sequence = "2", prepend = "-")
    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.NAME)
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.NAME)
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String externalReference;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.DESCRIPTION)
    @Property(optionality = Optionality.OPTIONAL)
    @PropertyLayout(multiLine = 3)
    @Getter @Setter
    private String description;

    public Tax changeDescription(final String description) {
        setDescription(description);
        return this;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "tax")
    @CollectionLayout(render = RenderType.EAGERLY)
    @Getter @Setter
    private SortedSet<TaxRate> rates = new TreeSet<TaxRate>();

    // //////////////////////////////////////

    public TaxRate newRate(
            final @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @ParameterLayout(named = "Percentage") BigDecimal percentage) {
        return taxRates.newRate(this, startDate, percentage);
    }

    // //////////////////////////////////////

    @Programmatic
    public TaxRate taxRateFor(final LocalDate date) {
        TaxRate rate = taxRates.findTaxRateByTaxAndDate(this, date);
        return rate;
    }

    // //////////////////////////////////////

    @Programmatic
    public BigDecimal percentageFor(final LocalDate date) {
        TaxRate rate = taxRateFor(date);
        if (rate == null) {
            return null;
        }
        return rate.getPercentage();
    }

    // //////////////////////////////////////

    private TaxRates taxRates;

    public final void injectTaxRates(final TaxRates taxRates) {
        this.taxRates = taxRates;
    }

}
