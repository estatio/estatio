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
package org.estatio.dom.guarantee;

import java.math.BigDecimal;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.joda.time.LocalDate;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.lease.Lease;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.guarantee.Guarantee "
                        + "WHERE reference == :reference"),
        @javax.jdo.annotations.Query(
                name = "findByLease", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.guarantee.Guarantee "
                        + "WHERE lease == :lease"),
        @javax.jdo.annotations.Query(
                name = "findByFinancialAccount", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.guarantee.Guarantee "
                        + "WHERE financialAccount == :financialAccount"),
        @javax.jdo.annotations.Query(
                name = "matchByReferenceOrName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.guarantee.Guarantee "
                        + "WHERE reference.matches(:referenceOrName)"
                        + "|| name.matches(:referenceOrName)")
})
@DomainObject(editing = Editing.DISABLED, autoCompleteRepository = Guarantees.class, autoCompleteAction = "autoComplete")
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Guarantee
        extends Agreement
        implements WithApplicationTenancyProperty {

    @Hidden(where = Where.PARENTED_TABLES)
    public Guarantee() {
        super(GuaranteeConstants.ART_GUARANTEE, GuaranteeConstants.ART_GUARANTOR);
    }

    // //////////////////////////////////////
    @Hidden(where = Where.PARENTED_TABLES)
    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getLease().getApplicationTenancy();
    }

    // //////////////////////////////////////


    private Lease lease;

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT)
    public Lease getLease() {
        return lease;
    }

    public void setLease(Lease lease) {
        this.lease = lease;
    }

    // //////////////////////////////////////

    private FinancialAccount financialAccount;

    @javax.jdo.annotations.Column(name = "financialAccountId", allowsNull = "true")
    @Property(hidden = Where.EVERYWHERE)
    public FinancialAccount getFinancialAccount() {
        return financialAccount;
    }

    public void setFinancialAccount(FinancialAccount financialAccount) {
        this.financialAccount = financialAccount;
    }

    // //////////////////////////////////////

    private GuaranteeType guaranteeType;

    @javax.jdo.annotations.Column(allowsNull = "false")
    public GuaranteeType getGuaranteeType() {
        return guaranteeType;
    }

    public void setGuaranteeType(GuaranteeType guaranteeType) {
        this.guaranteeType = guaranteeType;
    }

    public void changeGuaranteeType(GuaranteeType guaranteeType) {
        FinancialAccountType financialAccountType = guaranteeType.getFinancialAccountType();
        if (financialAccountType != null) {
            FinancialAccount financialAccount = financialAccounts.newFinancialAccount(
                    financialAccountType,
                    this.getReference(),
                    this.getName(),
                    this.getSecondaryParty());
            this.setFinancialAccount(financialAccount);
        }

        this.setGuaranteeType(guaranteeType);
    }

    public GuaranteeType default0ChangeGuaranteeType() {
        return this.getGuaranteeType();
    }

    public String disableChangeGuaranteeType(GuaranteeType guaranteeType) {
        return (getGuaranteeType() == GuaranteeType.COMPANY_GUARANTEE ||
                getGuaranteeType() == GuaranteeType.NONE || getGuaranteeType() == GuaranteeType.UNKNOWN) ? null : "Bank guarantees and deposits cannot be changed";
    }

    // //////////////////////////////////////

    private String description;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @PropertyLayout(multiLine = 3)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    private LocalDate terminationDate;

    @Property(optionality = Optionality.OPTIONAL)
    public LocalDate getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(LocalDate terminationDate) {
        this.terminationDate = terminationDate;
    }

    // //////////////////////////////////////

    private String comments;

    @Column(allowsNull = "true")
    @PropertyLayout(multiLine = 5)
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // //////////////////////////////////////

    public Guarantee terminate(
            final @ParameterLayout(named = "Termination date") LocalDate terminationDate,
            final @ParameterLayout(named = "Description") String description) {
        setTerminationDate(terminationDate);
        BigDecimal balance = financialAccount.getBalance();
        if (balance.compareTo(BigDecimal.ZERO) != 0) {
            financialAccount.newTransaction(terminationDate, description, balance.negate());
        }
        return this;
    }

    // //////////////////////////////////////

    private BigDecimal contractualAmount;

    @javax.jdo.annotations.Column(allowsNull = "true")
    public BigDecimal getContractualAmount() {
        return contractualAmount;
    }

    public void setContractualAmount(BigDecimal contractualAmount) {
        this.contractualAmount = contractualAmount;
    }

    public Guarantee changeContractualAmount(
            final @ParameterLayout(named = "New contractual amount") BigDecimal newContractualAmount) {
        setContractualAmount(newContractualAmount);
        return this;
    }

    public BigDecimal default0ChangeContractualAmount() {
        return getContractualAmount();
    }

    public Guarantee change(
            final @ParameterLayout(named = "Name") String name,
            final @ParameterLayout(named = "Description", multiLine = 3) @Parameter(optionality = Optionality.OPTIONAL) String description,
            final @ParameterLayout(named = "Comments", multiLine = 3) String comments) {
        setName(name);
        setDescription(description);
        setComments(comments);

        return this;
    }

    public String default0Change() {
        return getName();
    }

    public String default1Change() {
        return getDescription();
    }

    public String default2Change() {
        return getComments();
    }

    @Inject
    FinancialAccounts financialAccounts;
}
