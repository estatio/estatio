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

import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@DomainObject(editing = Editing.DISABLED)
public class Organisation
        extends Party
        implements WithApplicationTenancyCountry, WithApplicationTenancyPathPersisted {

    private String applicationTenancyPath;

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Hidden
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
        return securityApplicationTenancyRepository.findByPathCached(getApplicationTenancyPath());
    }

    // //////////////////////////////////////

    private String vatCode;

    @javax.jdo.annotations.Column(length = JdoColumnLength.Organisation.VAT_CODE)
    @Property(optionality = Optionality.OPTIONAL)
    @MemberOrder(sequence = "1")
    public String getVatCode() {
        return vatCode;
    }

    public void setVatCode(final String vatCode) {
        this.vatCode = vatCode;
    }

    // //////////////////////////////////////

    private String companyRegistration;

    @Property(optionality = Optionality.OPTIONAL)
    public String getCompanyRegistration() {
        return companyRegistration;
    }

    public void setCompanyRegistration(final String companyRegistration) {
        this.companyRegistration = companyRegistration;
    }

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    public Organisation change(
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Vat Code") @Parameter(optionality = Optionality.OPTIONAL, regexPattern = RegexValidation.REFERENCE) String vatCode,
            final @ParameterLayout(named = "Fiscal Code") @Parameter(optionality = Optionality.OPTIONAL, regexPattern = RegexValidation.REFERENCE) String fiscalCode,
            final @ParameterLayout(named = "Company Registration") @Parameter(optionality = Optionality.OPTIONAL) String companyRegistration) {
        setName(name);
        setVatCode(vatCode);
        setFiscalCode(fiscalCode);
        setCompanyRegistration(companyRegistration);

        return this;
    }

    public String default0Change() {
        return getName();
    }

    public String default1Change() {
        return getVatCode();
    }

    public String default2Change() {
        return getFiscalCode();
    }

    public String default3Change() {
        return getCompanyRegistration();
    }

}
