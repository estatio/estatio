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

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.party.Party;

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
                name = "FinancialAccount_reference_UNQ", members = "reference")
})
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.financial.FinancialAccount "
                        + "WHERE reference == :reference"),
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
        implements WithNameGetter, WithReferenceUnique {

    public FinancialAccount() {
        super("type, reference");
    }

    // //////////////////////////////////////

    private String reference;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.FinancialAccount.REFERENCE)
    @Property(regexPattern = RegexValidation.REFERENCE)
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    private String name;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.NAME)
    @Title
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    private FinancialAccountType type;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TYPE_ENUM)
    @Property(hidden = Where.EVERYWHERE)
    public FinancialAccountType getType() {
        return type;
    }

    public void setType(final FinancialAccountType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private Party owner;

    @javax.jdo.annotations.Column(name = "ownerPartyId", allowsNull = "false")
    public Party getOwner() {
        return owner;
    }

    public void setOwner(final Party owner) {
        this.owner = owner;
    }

    // //////////////////////////////////////

    private String externalReference;

    @Property(optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.NAME)
    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(final String externalReference) {
        this.externalReference = externalReference;
    }

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

    @Inject
    private FinancialAccountTransactions financialAccountTransactions;
}
