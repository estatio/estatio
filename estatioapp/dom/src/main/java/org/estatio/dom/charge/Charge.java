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
package org.estatio.dom.charge;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import org.apache.isis.applib.annotation.Bounded;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Title;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.IsisMultilineLines;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.WithNameUnique;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.tax.Tax;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "Charge_reference_UNQ", members = { "reference" }),
        @javax.jdo.annotations.Unique(
                name = "Charge_name_UNQ", members = { "name" })
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.charge.Charge "
                        + "WHERE reference == :reference")
})
@Bounded
public class Charge
        extends EstatioDomainObject<Charge>
        implements WithReferenceUnique, WithNameUnique, WithApplicationTenancyProperty, WithApplicationTenancyPathPersisted {

    public Charge() {
        super("reference");
    }

    // //////////////////////////////////////

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

    private String reference;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.REFERENCE)
    @Property(regexPattern = RegexValidation.REFERENCE, editing = Editing.DISABLED)
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @Title(sequence = "1")
    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.NAME)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    private String description;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.DESCRIPTION)
    @PropertyLayout(multiLine = IsisMultilineLines.NUMBER_OF_LINES)
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    private String externalReference;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.NAME)
    @Property(optionality = Optionality.OPTIONAL)
    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(final String externalReference) {
        this.externalReference = externalReference;
    }

    // //////////////////////////////////////

    private Tax tax;

    @javax.jdo.annotations.Column(name = "taxId", allowsNull = "false")
    public Tax getTax() {
        return tax;
    }

    public void setTax(final Tax tax) {
        this.tax = tax;
    }

    // //////////////////////////////////////

    private ChargeGroup group;

    @javax.jdo.annotations.Column(name = "groupId", allowsNull = "false")
    public ChargeGroup getGroup() {
        return group;
    }

    public void setGroup(final ChargeGroup group) {
        this.group = group;
    }

    // //////////////////////////////////////

    private String sortOrder;

    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.REFERENCE)
    @Property(optionality = Optionality.OPTIONAL)
    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(final String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Charge change(
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Tax") @Parameter(optionality = Optionality.OPTIONAL) Tax tax,
            final @ParameterLayout(named = "Description") String description,
            final @ParameterLayout(named = "Group") ChargeGroup group,
            final @ParameterLayout(named = "External Reference") @Parameter(optionality = Optionality.OPTIONAL) String externalReference,
            final @ParameterLayout(named = "Sort Order") @Parameter(optionality = Optionality.OPTIONAL) String sortOrder) {

        setName(name);
        setTax(tax);
        setDescription(description);
        setGroup(group);
        setExternalReference(externalReference);
        setSortOrder(sortOrder);
        return this;
    }

    public String default0Change() {
        return getName();
    }

    public Tax default1Change() {
        return getTax();
    }

    public String default2Change() {
        return getDescription();
    }

    public ChargeGroup default3Change() {
        return getGroup();
    }

    public String default4Change() {
        return getExternalReference();
    }

    public String default5Change() {
        return getSortOrder();
    }

}