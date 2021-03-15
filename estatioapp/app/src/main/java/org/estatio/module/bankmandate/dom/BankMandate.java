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
package org.estatio.module.bankmandate.dom;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.base.dom.types.NameType;

import org.estatio.module.agreement.dom.Agreement;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyPathPersisted;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.BankAccountRepository;
import org.estatio.module.financial.dom.FinancialAccount;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
// identityType=IdentityType.DATASTORE inherited from superclass
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
// no @DatastoreIdentity nor @Version, since inherited from supertype
@javax.jdo.annotations.Discriminator("org.estatio.dom.bankmandate.BankMandate")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findBankMandatesFor", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.bankmandate.dom.BankMandate "
                        + "WHERE bankAccount == :bankAccount")
})
@DomainObject(editing = Editing.DISABLED)
public class BankMandate
        extends Agreement
        implements WithApplicationTenancyProperty, WithApplicationTenancyPathPersisted {

    // //////////////////////////////////////

    private String applicationTenancyPath;

    /**
     * Primary role is creditor, secondary role is debtor.
     */
    public BankMandate() {
        super(BankMandateAgreementRoleTypeEnum.CREDITOR, BankMandateAgreementRoleTypeEnum.DEBTOR);
    }

    /**
     * For testing.
     */
    public BankMandate(ApplicationTenancyRepository applicationTenancyRepository) {
        this();
        this.securityApplicationTenancyRepository = applicationTenancyRepository;
    }

    @javax.jdo.annotations.Column(
            length = ApplicationTenancy.MAX_LENGTH_PATH,
            allowsNull = "false",
            name = "atPath"
    )
    @Property(hidden = Where.EVERYWHERE)
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

    @javax.jdo.annotations.Column(name = "bankAccountId", allowsNull = "false")
    @Getter @Setter
    private FinancialAccount bankAccount;

    public BankMandate changeBankAccount(
            final BankAccount bankAccount
    ) {
        setBankAccount(bankAccount);
        return this;
    }

    public List<? extends FinancialAccount> choices0ChangeBankAccount() {
        return bankAccountRepository.findBankAccountsByOwner(getSecondaryParty());
    }

    // //////////////////////////////////////

    public static class SepaMandateIdentifierType {
        private SepaMandateIdentifierType() {}
        public static class Meta {
            public static final int MAX_LEN = 35;
            private Meta() {}
        }
    }

    @Property(optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Column(allowsNull = "true", length = SepaMandateIdentifierType.Meta.MAX_LEN)
    @Getter @Setter
    private String sepaMandateIdentifier;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Getter @Setter
    private SequenceType sequenceType;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Getter @Setter
    private Scheme scheme;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate signatureDate;

    // //////////////////////////////////////

    public BankMandate changeSignatureDate(final @Parameter(optionality = Optionality.OPTIONAL) LocalDate signatureDate) {
        setSignatureDate(signatureDate);
        return this;
    }

    public LocalDate default0ChangeSignatureDate() {
        return getSignatureDate();
    }


    // //////////////////////////////////////

    public BankMandate change(
            @Parameter(optionality = Optionality.OPTIONAL, maxLength = NameType.Meta.MAX_LEN)
            final String name,
            @Parameter(optionality = Optionality.OPTIONAL, maxLength = SepaMandateIdentifierType.Meta.MAX_LEN)
            final String sepaMandateIdentifier,
            @Parameter(optionality = Optionality.OPTIONAL)
            final SequenceType sequenceType,
            @Parameter(optionality = Optionality.OPTIONAL)
            final Scheme scheme) {
        setName(name);
        setSepaMandateIdentifier(sepaMandateIdentifier);
        setSequenceType(sequenceType);
        setScheme(scheme);
        return this;
    }

    public String default0Change() {
        return getName();
    }

    public String default1Change() {
        return getSepaMandateIdentifier();
    }

    public SequenceType default2Change() {
        return getSequenceType();
    }

    public Scheme default3Change() {
        return getScheme();
    }

    // //////////////////////////////////////

    @ActionLayout(hidden = Where.EVERYWHERE)
    public Agreement changePrevious(
            @Parameter(optionality = Optionality.OPTIONAL)
            final Agreement previousLease) {
        return this;
    }

    // //////////////////////////////////////

    @Inject
    private BankAccountRepository bankAccountRepository;

}
