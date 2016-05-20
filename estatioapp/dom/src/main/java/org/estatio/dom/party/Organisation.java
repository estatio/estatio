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
package org.estatio.dom.party;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject(editing = Editing.DISABLED)
public class Organisation
        extends Party
        implements WithApplicationTenancyCountry, WithApplicationTenancyPathPersisted {

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

    @javax.jdo.annotations.Column(length = JdoColumnLength.Organisation.FISCAL_CODE)
    @Property(optionality = Optionality.OPTIONAL)
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private String fiscalCode;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(length = JdoColumnLength.Organisation.VAT_CODE)
    @Property(optionality = Optionality.OPTIONAL)
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private String vatCode;

    // //////////////////////////////////////

    @Persistent(mappedBy = "organisation")
    @CollectionLayout(defaultView = "table")
    @Getter @Setter
    private SortedSet<OrganisationPreviousName> previousNames = new TreeSet<>();

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    public Organisation change(
            final String name,
            final LocalDate previousNameEndDate,
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = RegexValidation.REFERENCE, regexPatternReplacement = RegexValidation.REFERENCE_DESCRIPTION) String vatCode,
            final @Parameter(optionality = Optionality.OPTIONAL, regexPattern = RegexValidation.REFERENCE, regexPatternReplacement = RegexValidation.REFERENCE_DESCRIPTION) String fiscalCode) {

        if (!name.equals(getName())) {
            OrganisationPreviousName organisationPreviousName = organisationPreviousNameRepository.newOrganisationPreviousName(getName(), previousNameEndDate);
            getPreviousNames().add(organisationPreviousName);
        }

        setName(name);
        setVatCode(vatCode);
        setFiscalCode(fiscalCode);

        return this;
    }

    public String default0Change() {
        return getName();
    }

    public LocalDate default1Change() {
        return getClockService().now();
    }

    public String default2Change() {
        return getVatCode();
    }

    public String default3Change() {
        return getFiscalCode();
    }

    public String validateChange(
            final String name,
            final LocalDate previousNameEndDate,
            final String vatCode,
            final String fiscalCode) {
        return previousNameEndDate.isAfter(getClockService().now()) ? "You can not select a future end date" : null;
    }

    @Inject
    OrganisationPreviousNameRepository organisationPreviousNameRepository;
}
