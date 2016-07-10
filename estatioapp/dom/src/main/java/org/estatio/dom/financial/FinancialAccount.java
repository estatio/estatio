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
package org.estatio.dom.financial;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceGetter;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
//                name = "FinancialAccount_reference_UNQ", members = "reference")
                name = "FinancialAccount_owner_reference_UNQ", members = {"owner", "reference"})
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByOwnerAndReference", language = "JDOQL",
//                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.financial.FinancialAccount "
                        + "WHERE owner == :owner "
                        + "&& reference == :reference"),
        @javax.jdo.annotations.Query(
                name = "findByTypeAndOwner", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.financial.FinancialAccount "
                        + "WHERE type == :type "
                        + "&& owner == :owner"),
        @javax.jdo.annotations.Query(
                name = "findByOwner", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.financial.FinancialAccount "
                        + "WHERE owner == :owner")
})
@DomainObject(editing = Editing.DISABLED)
public class FinancialAccount
        extends EstatioDomainObject<FinancialAccount>
        implements WithNameGetter, WithReferenceGetter, WithApplicationTenancyCountry {
//        implements WithNameGetter, WithReferenceUnique,     WithApplicationTenancyCountry {

    public FinancialAccount() {
        super("type, owner, reference");
//        super("type, reference");
    }

    public String title() {
        return TitleBuilder.start()
                .withReference(getReference())
                .withName(getName())
                .toString();
    }


    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getOwner().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.FinancialAccount.REFERENCE)
    @Property(regexPattern = RegexValidation.REFERENCE)
    @Getter @Setter
    private String reference;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.NAME)
    @Getter @Setter
    private String name;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TYPE_ENUM)
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private FinancialAccountType type;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "ownerPartyId", allowsNull = "false")
    @Getter @Setter
    private Party owner;

    public FinancialAccount changeOwner(final Party newOwner){
        this.owner = newOwner;
        return this;
    }

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.NAME)
    @Getter @Setter
    private String externalReference;

    // //////////////////////////////////////

    @Programmatic
    public BigDecimal getBalance() {
        return financialAccountTransactions.balance(this);
    }

    // //////////////////////////////////////

    @Programmatic
    public void newTransaction(
            final LocalDate transactionDate,
            final String description,
            final BigDecimal amount) {
        financialAccountTransactions.newTransaction(this, transactionDate, description, amount);
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public FinancialAccount changeName(final String name){
        setName(name);
        return this;
    }

    public String default0ChangeName(){
        return getName();
    }

    @Inject
    private FinancialAccountTransactions financialAccountTransactions;
}
