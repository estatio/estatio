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

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.types.MoneyType;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccountRepository;
import org.estatio.dom.financial.FinancialAccountType;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.roles.EstatioRole;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.guarantee.Guarantee")
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
                name = "matchByReferenceOrNameOrComments", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.guarantee.Guarantee "
                        + "WHERE reference.matches(:referenceOrNameOrComments)"
                        + "|| name.matches(:referenceOrNameOrComments)"
                        + "|| comments.matches(:referenceOrNameOrComments)")
})
@DomainObject(
        editing = Editing.DISABLED,
        autoCompleteRepository = GuaranteeRepository.class,
        autoCompleteAction = "autoComplete"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
public class Guarantee
        extends Agreement
        implements WithApplicationTenancyProperty {

    public Guarantee() {
        super(GuaranteeAgreementRoleTypeEnum.GUARANTEE, GuaranteeAgreementRoleTypeEnum.GUARANTOR);
    }

    // //////////////////////////////////////
    @Property(hidden = Where.PARENTED_TABLES)
    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getLease().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "leaseId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Lease lease;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "financialAccountId", allowsNull = "true")
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private FinancialAccount financialAccount;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Getter @Setter
    private GuaranteeType guaranteeType;

    public void changeGuaranteeType(GuaranteeType guaranteeType) {
        FinancialAccountType financialAccountType = guaranteeType.getFinancialAccountType();
        if (financialAccountType != null) {
            FinancialAccount financialAccount = financialAccountRepository.newFinancialAccount(
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

    public String disableChangeGuaranteeType() {
        return getGuaranteeType().isMutable() || EstatioRole.ADMINISTRATOR.isApplicableFor(getUser()) ? null : "Bank guarantees and deposits cannot be changed";
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true")
    @PropertyLayout(multiLine = 3)
    @Getter @Setter
    private String description;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate terminationDate;

    // //////////////////////////////////////

    @Column(allowsNull = "true")
    @PropertyLayout(multiLine = 5)
    @Getter @Setter
    private String comments;

    // //////////////////////////////////////

    public Guarantee terminate(
            final LocalDate terminationDate,
            final String description) {
        setTerminationDate(terminationDate);
        if (financialAccount != null && financialAccount.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            financialAccount.newTransaction(terminationDate, description, financialAccount.getBalance().negate());
        }
        return this;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
    @Getter @Setter
    private BigDecimal contractualAmount;

    public Guarantee changeContractualAmount(
            final BigDecimal newContractualAmount) {
        setContractualAmount(newContractualAmount);
        return this;
    }

    public BigDecimal default0ChangeContractualAmount() {
        return getContractualAmount();
    }

    public Guarantee change(
            final String name,
            final @ParameterLayout(multiLine = 3) @Parameter(optionality = Optionality.OPTIONAL) String description,
            final @ParameterLayout(multiLine = 3) String comments) {
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

    @ActionLayout(hidden = Where.EVERYWHERE)
    public Agreement changePrevious(
            @Parameter(optionality = Optionality.OPTIONAL)
            final Agreement previousLease) {
        return this;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void remove(final String reason) {
        remove(this);
        final FinancialAccount financialAccount = getFinancialAccount();
        this.setFinancialAccount(null);
        if (financialAccount != null) {
            financialAccount.remove(reason);
        }
    }

    public boolean hideRemove() {
        final boolean userIsAdmin = EstatioRole.ADMINISTRATOR.isApplicableFor(getUser());
        return !userIsAdmin;
    }

    @Inject
    FinancialAccountRepository financialAccountRepository;
}
