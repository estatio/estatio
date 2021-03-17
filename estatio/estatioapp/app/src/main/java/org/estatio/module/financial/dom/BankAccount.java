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
package org.estatio.module.financial.dom;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.IsisApplibModule.ActionDomainEvent;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ObjectPersistedEvent;
import org.apache.isis.applib.services.eventbus.ObjectRemovingEvent;
import org.apache.isis.applib.services.eventbus.ObjectUpdatedEvent;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;
import org.estatio.module.bankmandate.dom.BankMandateRepository;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.incode.module.base.dom.Titled;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.country.dom.impl.Country;

import org.estatio.module.base.dom.EstatioRole;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationState;
import org.estatio.module.financial.dom.bankaccount.verification.BankAccountVerificationStateTransition;
import org.estatio.module.task.dom.state.State;
import org.estatio.module.task.dom.state.StateTransition;
import org.estatio.module.task.dom.state.StateTransitionType;
import org.estatio.module.task.dom.state.Stateful;
import org.estatio.module.financial.dom.utils.IBANHelper;
import org.estatio.module.financial.dom.utils.IBANValidator;
import org.estatio.module.party.dom.Party;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator("org.estatio.dom.financial.bankaccount.BankAccount")
// no @DatastoreIdentity nor @Version, since inherited from supertype
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.financial.dom.BankAccount "
                        + "WHERE reference == :reference"),
        @javax.jdo.annotations.Query(
                name = "findByIban", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.financial.dom.BankAccount "
                        + "WHERE iban == :iban"),
        @javax.jdo.annotations.Query(
                name = "matchOnReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.financial.dom.BankAccount "
                        + "WHERE reference.matches(:regex)")
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(name = "BankAccount_iban",
                members = { "iban" }),
})
@DomainObject(
        editing = Editing.DISABLED,
        persistedLifecycleEvent = BankAccount.PersistedLifecycleEvent.class,
        updatedLifecycleEvent = BankAccount.UpdatedLifecycleEvent.class,
        removingLifecycleEvent = BankAccount.RemovingLifecycleEvent.class,
        autoCompleteRepository = BankAccountRepository.class
        //,objectType = "org.estatio.dom.financial.bankaccount.BankAccount"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class BankAccount
        extends FinancialAccount implements Stateful {

    public static class PersistedLifecycleEvent extends ObjectPersistedEvent<BankAccount> {}
    public static class UpdatedLifecycleEvent extends ObjectUpdatedEvent<BankAccount> {}
    public static class RemovingLifecycleEvent extends ObjectRemovingEvent<BankAccount> {}

    @Column(name = "bankPartyId", allowsNull = "true")
    @Getter @Setter
    private Party bank;

    @Column(name = "countryId", allowsNull = "true")
    @Getter @Setter
    private Country country;

    @Column(allowsNull = "true", length = IbanType.Meta.MAX_LEN)
    @Getter @Setter
    private String iban;

    public boolean isValidIban() {
        return IBANValidator.valid(getIban());
    }

    @Action(hidden = Where.EVERYWHERE)
    public BankAccount verifyIban() {
        IBANHelper.verifyAndUpdate(this);
        return this;
    }

    @Column(allowsNull = "true", length = NationalCheckCodeType.Meta.MAX_LEN)
    @Getter @Setter
    private String nationalCheckCode;

    @Column(allowsNull = "true", length = NationalBankCodeType.Meta.MAX_LEN)
    @Getter @Setter
    private String nationalBankCode;

    @Column(allowsNull = "true", length = BranchCodeType.Meta.MAX_LEN)
    @Getter @Setter
    private String branchCode;

    @Column(allowsNull = "true", length = AccountNumberType.Meta.MAX_LEN)
    @Getter @Setter
    private String accountNumber;

    @Column(allowsNull = "true", length = AccountNumberType.Meta.MAX_LEN)
    @Getter @Setter
    private String bic;

    public static class BicUpdatedEvent extends ActionDomainEvent<BankAccount>{};

    @Action(domainEvent = BicUpdatedEvent.class)
    public BankAccount updateBic(@Nullable String bic) {
        setBic(trimBic(bic));
        return this;
    }

    public String default0UpdateBic() {
        return getBic();
    }

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private Boolean preferred;

    @MemberOrder(name = "preferred",sequence = "1")
    public BankAccount togglePreferred() {
        if(getPreferred() == null) {
            setPreferred(true);
        } else {
            setPreferred(!getPreferred());
        }
        return this;
    }

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private Boolean deprecated;

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public BankAccount deprecate(final String reason){
        setDeprecated(true);
        return this;
    }

    public String disableDeprecate(){
        return getDeprecated()!=null && getDeprecated() ? "Already deprecated" : null;
    }

    public static class ChangeEvent extends ActionDomainEvent<BankAccount> {}

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ChangeEvent.class
    )
    public BankAccount changeIban(
            @Parameter(maxLength = IbanType.Meta.MAX_LEN) final String iban) {
        setIban(iban);
        setName(iban);
        setReference(iban);

        return this;
    }

    public String default0ChangeIban() {
        return getIban();
    }

    public String validate0ChangeIban(final String iban) {
        if (!IBANValidator.valid(iban)) {
            return "Not a valid IBAN number";
        }
        return null;
    }

    public String disableChangeIban(){
        if (getVerificationState() == BankAccountVerificationState.NOT_VERIFIED
                && invoiceRepository.findFirstByBuyer(getOwner()) == null
                && invoiceRepository.findFirstBySeller(getOwner()) == null
                && bankMandateRepository.findFirstBankMandateFor(this) == null) {
            return null;
        }

        // This is a temporary measure to prevent user errors:
        return EstatioRole.ADMINISTRATOR.isApplicableFor(getUser()) ? null : "Only administrators can change an already verified and/or used IBAN";
    }


    public BankAccount refresh() {
        IBANHelper.verifyAndUpdate(this);
        return this;
    }

    @Action(domainEvent = BankAccount.RemoveEvent.class, semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void remove(final String reason) {
        getContainer().remove(this);
        getContainer().flush();
        return;
    }

    @Override
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>>
    S getStateOf(
            final Class<ST> stateTransitionClass) {
            if (stateTransitionClass == BankAccountVerificationStateTransition.class) {
            return (S) getVerificationState();
        }
        return null;
    }

    @Override
    public <
            DO,
            ST extends StateTransition<DO, ST, STT, S>,
            STT extends StateTransitionType<DO, ST, STT, S>,
            S extends State<S>
            > void setStateOf(
            final Class<ST> stateTransitionClass, final S newState) {
        if (stateTransitionClass == BankAccountVerificationStateTransition.class) {
            setVerificationState((BankAccountVerificationState) newState);
        }
    }

    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "false")
    private BankAccountVerificationState verificationState;



    public static class RemoveEvent extends ActionDomainEvent<BankAccount> {
        private static final long serialVersionUID = 1L;
    }


    public static String trimBic(final String str) {
        // also trims non-breaking spaces
        return str == null ? null : str.replace((char) 160, ' ').trim();
    }


    public static class AccountNumberType {

        private AccountNumberType() {}

        public static class Meta {

            /**
             * TODO: review
             */
            public final static int MAX_LEN = 20;

            private Meta() {}

        }

    }

    public static class IbanType {

        private IbanType() {}

        public static class Meta {

            /**
             * eg http://en.wikipedia.org/wiki/International_Bank_Account_Number
             *
             */
            public final static int MAX_LEN = 34;

            public static final String REGEX = "[A-Z,0-9]+";
            public static final String REGEX_DESCRIPTION = "Only letters and numbers are allowed";

            private Meta() {}

        }

    }

    public static class BranchCodeType {

        private BranchCodeType() {}

        public static class Meta {

            /**
             * TODO: review
             */
            public final static int MAX_LEN = 20;

            private Meta() {}

        }

    }

    public static class NationalBankCodeType {

        private NationalBankCodeType() {}

        public static class Meta {

            /**
             * TODO: review
             */
            public final static int MAX_LEN = 20;

            private Meta() {}

        }

    }

    public static class NationalCheckCodeType {

        private NationalCheckCodeType() {}

        public static class Meta {

            /**
             * TODO: review
             */
            public final static int MAX_LEN = 20;

            private Meta() {}

        }

    }


    public String title() {
        final BankAccountVerificationState verificationState = getVerificationState();
        final String friendlyName = friendlyName();
        return verificationState != null
                ? String.format("%s (%s)", friendlyName, verificationState)
                : friendlyName;
    }

    @Programmatic
    public String friendlyName() {
        final BankAccount bankAccount = this;
        final TitleBuilder builder = TitleBuilder.start();
        final String name = bankAccount.getName();
        if(Objects.equals(name, bankAccount.getIban())) {
            // courtesy of https://stackoverflow.com/a/3761521/56880
            final String[] parts = name.split("(?<=\\G.{4})");
            for (final String part : parts) {
                builder.withName(part);
            }
        } else {
            builder.withName(name);
        }
        return builder
                .withName("-")
                .withName(bankAccount.getOwner().getReference())
                .toString();
    }

    @Mixin(method="act")
    public static class BankAccount_lookupBic {

        public static enum WebSite implements Titled {
            IBANCALCULATOR("www.ibancalculator.com") {
                @Override
                URL buildUrl(String iban) throws MalformedURLException {
                    return new URL("https://www.ibancalculator.com/validate/" + iban);
                }
            },
            IBAN("www.iban.com") {
                @Override
                URL buildUrl(String iban) throws MalformedURLException {
                    return new URL("https://www.iban.com");
                }
            };

            private final String name;
            WebSite(final String name) {
                this.name = name;
            }

            abstract URL buildUrl(String iban) throws MalformedURLException;

            @Override
            public String title() {
                return name;
            }
        }
        private final BankAccount bankAccount;

        public BankAccount_lookupBic(BankAccount bankAccount) {
            this.bankAccount = bankAccount;
        }

        @Action(
                semantics = SemanticsOf.SAFE
        )
        public URL act(
                final WebSite webSite,
                final String iban) throws MalformedURLException {
            return webSite.buildUrl(iban);
        }

        public WebSite default0Act() { return WebSite.IBANCALCULATOR; }
        public String default1Act() { return bankAccount.getIban(); }

    }

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    BankMandateRepository bankMandateRepository;

}
