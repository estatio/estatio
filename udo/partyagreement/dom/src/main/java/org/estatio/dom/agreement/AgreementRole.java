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
package org.estatio.dom.agreement;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.title.TitleService;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.with.WithIntervalContiguous;
import org.incode.module.base.dom.types.NameType;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelRepository;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.party.Party;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"        // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByAgreement", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.agreement.AgreementRole "
                        + "WHERE agreement == :agreement "),
        @javax.jdo.annotations.Query(
                name = "findByParty", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.agreement.AgreementRole "
                        + "WHERE party == :party "),
        @javax.jdo.annotations.Query(
                name = "findByPartyAndTypeAndContainsDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.AgreementRole "
                        + "WHERE party == :party "
                        + "&& type == :type "
                        + "&& (startDate == null || :startDate >= startDate) "
                        + "&& (endDate == null || :endDate <= endDate) "),
        @javax.jdo.annotations.Query(
                name = "findByAgreementAndPartyAndTypeAndContainsDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.agreement.AgreementRole "
                        + "WHERE agreement == :agreement "
                        + "&& party == :party "
                        + "&& type == :type "
                        + "&& (startDate == null || startDate <= :startDate) "
                        + "&& (endDate == null || endDate >= :endDate) "),
        @javax.jdo.annotations.Query(
                name = "findByAgreementAndTypeAndContainsDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.AgreementRole "
                        + "WHERE agreement == :agreement "
                        + "&& type == :type "
                        + "&& (startDate == null || startDate <= :startDate) "
                        + "&& (endDate == null || endDate >= :endDate) "),
        @javax.jdo.annotations.Query(
                name = "findByPartyAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.AgreementRole "
                        + "WHERE party == :party "
                        + "&& type == :type ")
})
@Unique(
        name = "AgreementRole_agreement_party_type_startDate_UNQ",
        members = { "agreement", "party", "type", "startDate" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.agreement.AgreementRole"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public class AgreementRole
        extends UdoDomainObject2<AgreementRole>
        implements WithIntervalContiguous<AgreementRole>, WithApplicationTenancyProperty {

    private final WithIntervalContiguous.Helper<AgreementRole> helper =
            new WithIntervalContiguous.Helper<>(this);

    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getAgreement().getApplicationTenancy();
    }

    // //////////////////////////////////////

    public AgreementRole() {
        super("agreement, startDate desc nullsLast, type, party");
    }

    public TranslatableString title() {
        return TranslatableString.tr("{type} of {agreement} ({party})",
                "type", getType().getTitle(),
                "agreement", getAgreement().getReference(),
                "party", getParty().getName());
    }

    @javax.jdo.annotations.Column(name = "agreementId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Agreement agreement;

    @javax.jdo.annotations.Column(name = "partyId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private Party party;

    @javax.jdo.annotations.Persistent(defaultFetchGroup = "true")
    @javax.jdo.annotations.Column(name = "typeId", allowsNull = "false")
    @Getter @Setter
    private AgreementRoleType type;

    @javax.jdo.annotations.Column(length = NameType.Meta.MAX_LEN, allowsNull = "true")
    @Getter @Setter
    private String externalReference;

    public AgreementRole changeExternalReference(String externalReference) {
        setExternalReference(externalReference);
        return this;
    }

    public String default0ChangeExternalReference() {
        return getExternalReference();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED)
    @Getter @Setter
    private LocalDate startDate;

    @javax.jdo.annotations.Persistent
    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED)
    @Getter @Setter
    private LocalDate endDate;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @Override
    public AgreementRole changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        helper.changeDates(startDate, endDate);
        return this;
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return null;
    }

    @Override
    public LocalDate default0ChangeDates() {
        return helper.default0ChangeDates();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return helper.default1ChangeDates();
    }

    @Override
    public String validateChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return helper.validateChangeDates(startDate, endDate);
    }

    // //////////////////////////////////////

    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return getInterval().overlap(getAgreement().getEffectiveInterval());

    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED, hidden = Where.ALL_TABLES)
    @Override
    public AgreementRole getPredecessor() {
        return helper.getPredecessor(getAgreement().getRoles(), Predicates.matchingRole(this.getType()));
    }

    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED, hidden = Where.ALL_TABLES)
    @Override
    public AgreementRole getSuccessor() {
        return helper.getSuccessor(getAgreement().getRoles(), Predicates.matchingRole(this.getType()));
    }

    @CollectionLayout(render = RenderType.EAGERLY)
    @Override
    public SortedSet<AgreementRole> getTimeline() {
        return helper.getTimeline(getAgreement().getRoles(), Predicates.matchingRole(this.getType()));
    }

    // //////////////////////////////////////

    static final class SiblingFactory implements WithIntervalContiguous.Factory<AgreementRole> {
        private final AgreementRole ar;
        private final Party party;

        public SiblingFactory(final AgreementRole ar, final Party party) {
            this.ar = ar;
            this.party = party;
        }

        @Override
        public AgreementRole newRole(final LocalDate startDate, final LocalDate endDate) {
            return ar.getAgreement().createRole(ar.getType(), party, startDate, endDate);
        }
    }

    public AgreementRole succeededBy(
            final Party party,
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return helper.succeededBy(startDate, endDate, new SiblingFactory(this, party));
    }

    public LocalDate default1SucceededBy() {
        return helper.default1SucceededBy();
    }

    public String validateSucceededBy(
            final Party party,
            final LocalDate startDate,
            final LocalDate endDate) {
        String invalidReasonIfAny = helper.validateSucceededBy(startDate, endDate);
        if (invalidReasonIfAny != null) {
            return invalidReasonIfAny;
        }

        if (party == getParty()) {
            return "Successor's party cannot be the same as this object's party";
        }
        final AgreementRole successor = getSuccessor();
        if (successor != null && party == successor.getParty()) {
            return "Successor's party cannot be the same as that of existing successor";
        }
        return null;
    }

    public AgreementRole precededBy(
            final Party party,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final LocalDate endDate) {

        return helper.precededBy(startDate, endDate, new SiblingFactory(this, party));
    }

    public LocalDate default2PrecededBy() {
        return helper.default2PrecededBy();
    }

    public String validatePrecededBy(
            final Party party,
            final LocalDate startDate,
            final LocalDate endDate) {
        final String invalidReasonIfAny = helper.validatePrecededBy(startDate, endDate);
        if (invalidReasonIfAny != null) {
            return invalidReasonIfAny;
        }

        if (party == getParty()) {
            return "Predecessor's party cannot be the same as this object's party";
        }
        final AgreementRole predecessor = getPredecessor();
        if (predecessor != null && party == predecessor.getParty()) {
            return "Predecessor's party cannot be the same as that of existing predecessor";
        }
        return null;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(mappedBy = "role")
    @CollectionLayout(render = RenderType.EAGERLY)
    @Collection(editing = Editing.DISABLED)
    @Getter @Setter
    private SortedSet<AgreementRoleCommunicationChannel> communicationChannels = new TreeSet<>();

    // //////////////////////////////////////

    public AgreementRole addCommunicationChannel(
            final AgreementRoleCommunicationChannelType type,
            final CommunicationChannel communicationChannel,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        createAgreementRoleCommunicationChannel(type, communicationChannel, startDate, endDate);
        return this;
    }

    public List<AgreementRoleCommunicationChannelType> choices0AddCommunicationChannel() {
        return getAgreement().getType().getRoleChannelTypesApplicableTo();
    }

    public List<CommunicationChannel> choices1AddCommunicationChannel() {
        return Lists.newArrayList(communicationChannelRepository.findByOwner(getParty()));
    }

    public CommunicationChannel default1AddCommunicationChannel() {
        final SortedSet<CommunicationChannel> partyChannels =
                communicationChannelRepository.findByOwner(getParty());
        return !partyChannels.isEmpty() ? partyChannels.first() : null;
    }

    public String validateAddCommunicationChannel(
            final AgreementRoleCommunicationChannelType type,
            final CommunicationChannel communicationChannel,
            final LocalDate startDate,
            final LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return "End date cannot be earlier than start date";
        }
        if (!Sets.filter(getCommunicationChannels(), type.matchingCommunicationChannel()).isEmpty()) {
            return "Add a successor/predecessor from existing communication channel";
        }
        final SortedSet<CommunicationChannel> partyChannels =
                communicationChannelRepository.findByOwner(getParty());
        if (!partyChannels.contains(communicationChannel)) {
            return "Communication channel must be one of those of this party";
        }
        return null;
    }

    @Programmatic
    public AgreementRoleCommunicationChannel createAgreementRoleCommunicationChannel(
            final AgreementRoleCommunicationChannelType type,
            final CommunicationChannel cc,
            final LocalDate startDate,
            final LocalDate endDate) {
        final AgreementRoleCommunicationChannel arcc = newTransientInstance(AgreementRoleCommunicationChannel.class);
        arcc.setType(type);
        arcc.setStartDate(startDate);
        arcc.setEndDate(endDate);
        arcc.setCommunicationChannel(cc);
        arcc.setRole(this);
        persistIfNotAlready(arcc);
        return arcc;
    }

    // //////////////////////////////////////

    public final static class Predicates {

        private Predicates() {
        }

        /**
         * A {@link Predicate} that tests whether the role's
         * {@link AgreementRole#getType() type} is the specified value.
         */
        public static Predicate<AgreementRole> whetherTypeIs(final AgreementRoleType art) {
            return new Predicate<AgreementRole>() {

                @Override
                public boolean apply(final AgreementRole input) {
                    return input != null && input.getType() == art;
                }
            };
        }

        /**
         * A {@link Predicate} that tests whether the role's
         * {@link AgreementRole#getAgreement() agreement}'s
         * {@link Agreement#getType() type} is the specified value.
         */
        public static Predicate<AgreementRole> whetherAgreementTypeIs(final AgreementType at) {
            return new Predicate<AgreementRole>() {

                @Override
                public boolean apply(final AgreementRole input) {
                    return input != null && input.getAgreement().getType() == at;
                }
            };
        }

        @Programmatic
        public static Predicate<? super AgreementRole> matchingRoleAndPeriod(
                final AgreementRoleType art,
                final LocalDate startDate,
                final LocalDate endDate) {
            return new Predicate<AgreementRole>() {
                @Override
                public boolean apply(final AgreementRole ar) {
                    return Objects.equal(ar.getType(), art) && ar.getInterval().overlaps(new LocalDateInterval(startDate, endDate));
                }
            };
        }

        @Programmatic
        public static Predicate<? super AgreementRole> matchingRole(
                final AgreementRoleType art) {
            return new Predicate<AgreementRole>() {
                @Override
                public boolean apply(final AgreementRole ar) {
                    return Objects.equal(ar.getType(), art);
                }
            };
        }

    }

    public final static class Functions {

        private Functions() {
        }

        /**
         * A {@link Function} that obtains the role's
         * {@link AgreementRole#getParty() party} attribute.
         */
        public static <T extends Party> Function<AgreementRole, T> partyOf() {
            return new Function<AgreementRole, T>() {
                @SuppressWarnings("unchecked")
                public T apply(final AgreementRole agreementRole) {
                    return (T) (agreementRole != null ? agreementRole.getParty() : null);
                }
            };
        }

        /**
         * A {@link Function} that obtains the role's
         * {@link AgreementRole#getAgreement() agreement} attribute.
         */
        public static <T extends Agreement> Function<AgreementRole, T> agreementOf() {
            return new Function<AgreementRole, T>() {
                @SuppressWarnings("unchecked")
                public T apply(final AgreementRole agreementRole) {
                    return (T) (agreementRole != null ? agreementRole.getAgreement() : null);
                }
            };
        }

        /**
         * A {@link Function} that obtains the role's
         * {@link org.estatio.dom.agreement.AgreementRole#getEffectiveInterval()}
         * effective end date} attribute.
         */
        public static Function<AgreementRole, LocalDate> effectiveEndDateOf() {
            return new Function<AgreementRole, LocalDate>() {
                @Override
                public LocalDate apply(final AgreementRole input) {
                    return input != null ? input.getEffectiveInterval().endDate() : null;
                }
            };
        }

    }

    // //////////////////////////////////////

    /**
     * Called by migration API.
     */
    @Programmatic
    public void addCommunicationChannel(
            final AgreementRoleCommunicationChannelType type,
            final CommunicationChannel communicationChannel,
            final LocalDate startDate) {
        if (type == null || communicationChannel == null) {
            return;
        }
        AgreementRoleCommunicationChannel arcc = findCommunicationChannel(type, startDate);
        if (arcc != null) {
            return;
        }

        createAgreementRoleCommunicationChannel(type, communicationChannel, startDate, null);
    }

    private AgreementRoleCommunicationChannel findCommunicationChannel(
            final AgreementRoleCommunicationChannelType type, final LocalDate date) {
        return agreementRoleCommunicationChannelRepository.findByRoleAndTypeAndContainsDate(this, type, date);
    }

    // //////////////////////////////////////

    @Inject
    private CommunicationChannelRepository communicationChannelRepository;

    @Inject
    private AgreementRoleCommunicationChannelRepository agreementRoleCommunicationChannelRepository;

    @Inject
    TitleService titleService;

}
