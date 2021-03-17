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
package org.estatio.module.tax.dom;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.user.UserService;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.DescriptionType;
import org.incode.module.base.dom.types.NameType;
import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.with.WithNameGetter;
import org.incode.module.base.dom.with.WithReferenceComparable;
import org.incode.module.base.dom.with.WithReferenceUnique;

import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyPathPersisted;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"  // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
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
                        + "FROM org.estatio.module.tax.dom.Tax "
                        + "WHERE reference == :reference")
})
@DomainObject(
        editing = Editing.DISABLED,
        bounded = true,
        objectType = "org.estatio.dom.tax.Tax"
)
@EqualsAndHashCode(of = {"reference"}, callSuper = false)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class Tax
        extends UdoDomainObject2<Tax>
        implements WithReferenceComparable<Tax>, WithNameGetter, WithReferenceUnique,
                   WithApplicationTenancyCountry, WithApplicationTenancyPathPersisted {

    public Tax() {
        super("reference");
    }

    @Builder
    public Tax(
            final String applicationTenancyPath,
            final String reference,
            final String name) {
        this();
        this.applicationTenancyPath = applicationTenancyPath;
        this.reference = reference;
        this.name = name;
    }

    public String title() {
        return TitleBuilder.start()
                .withReference(getReference())
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


    @javax.jdo.annotations.Column(allowsNull = "false", length = ReferenceType.Meta.MAX_LEN)
    @Property(regexPattern = ReferenceType.Meta.REGEX)
    @Getter @Setter
    private String reference;


    @javax.jdo.annotations.Column(allowsNull = "true", length = NameType.Meta.MAX_LEN)
    @Getter @Setter
    private String name;


    @javax.jdo.annotations.Column(allowsNull = "true", length = NameType.Meta.MAX_LEN)
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private String externalReference;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public Tax changeExternalReference(final String externalReference){
        setExternalReference(externalReference);
        return this;
    }

    public String disableChangeExternalReference(){
        return !EstatioRole.SUPERUSER.isApplicableFor(userService.getUser()) ? "You need Superuser rights to change the external reference" : null;
    }

    @javax.jdo.annotations.Column(allowsNull = "true", length = DescriptionType.Meta.MAX_LEN)
    @Property(optionality = Optionality.OPTIONAL)
    @PropertyLayout(multiLine = 3)
    @Getter @Setter
    private String description;

    public Tax changeDescription(final String description) {
        setDescription(description);
        return this;
    }


    @javax.jdo.annotations.Persistent(mappedBy = "tax")
    @CollectionLayout(defaultView = "table")
    @Getter @Setter
    private SortedSet<TaxRate> rates = new TreeSet<>();


    public TaxRate newRate(
            final LocalDate startDate,
            final BigDecimal percentage) {
        return taxRateRepository.newRate(this, startDate, percentage);
    }


    @Programmatic
    public TaxRate taxRateFor(final LocalDate date) {
        TaxRate rate = taxRateRepository.findTaxRateByTaxAndDate(this, date);
        return rate;
    }


    @Programmatic
    public BigDecimal percentageFor(final LocalDate date) {
        TaxRate rate = taxRateFor(date);
        if (rate == null) {
            return null;
        }
        return rate.getPercentage();
    }


    @Programmatic
    public BigDecimal grossFromNet(final BigDecimal net, LocalDate date) {
        return net.add(percentageFor(date.minusDays(1)).multiply(net).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

    @Programmatic
    public BigDecimal netFromGross(final BigDecimal gross, LocalDate date) {
        return gross.divide(percentageFor(date.minusDays(1)).add(new BigDecimal("100"))).multiply(new BigDecimal("100"));
    }


    @Inject
    public TaxRateRepository taxRateRepository;

    @Inject UserService userService;

}
