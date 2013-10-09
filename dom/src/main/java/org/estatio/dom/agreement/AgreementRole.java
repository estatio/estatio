/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithIntervalContiguous;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelContributions;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER, 
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByAgreementAndPartyAndTypeAndContainsDate", language = "JDOQL",
                value = "SELECT " +
                        "FROM org.estatio.dom.agreement.AgreementRole "
                        + "WHERE agreement == :agreement "
                        + "&& party == :party "
                        + "&& type == :type "
                        + "&& (startDate == null || startDate <= :date) "
                        + "&& (endDate == null || endDate > :date) "),
        @javax.jdo.annotations.Query(
                name = "findByAgreementAndTypeAndContainsDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.AgreementRole "
                        + "WHERE agreement == :agreement "
                        + "&& type == :type "
                        + "&& (startDate == null || startDate < :date) "
                        + "&& (endDate == null || endDate > :date) ")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class AgreementRole extends EstatioTransactionalObject<AgreementRole, Status> 
        implements WithIntervalContiguous<AgreementRole> {

    private final WithIntervalContiguous.Helper<AgreementRole> helper = 
                new WithIntervalContiguous.Helper<AgreementRole>(this);

    // //////////////////////////////////////

    public AgreementRole() {
        super("agreement, startDate desc nullsLast, type, party", Status.UNLOCKED, Status.LOCKED);
    }

    @Override
    public Status getLockable() {
        return getStatus();
    }

    @Override
    public void setLockable(final Status lockable) {
        setStatus(lockable);
    }

    // //////////////////////////////////////

    private Status status;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Hidden
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    // //////////////////////////////////////

    private Agreement<?> agreement;

    @javax.jdo.annotations.Column(name = "agreementId", allowsNull = "false")
    @Title(sequence = "3", prepend = ":")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Agreement<?> getAgreement() {
        return agreement;
    }

    public void setAgreement(final Agreement<?> agreement) {
        this.agreement = agreement;
    }

    // //////////////////////////////////////

    private Party party;

    @javax.jdo.annotations.Column(name = "partyId", allowsNull = "false")
    @Title(sequence = "2", prepend = ":")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // //////////////////////////////////////

    private AgreementRoleType type;

    @javax.jdo.annotations.Column(name = "typeId", allowsNull = "false")
    @Title(sequence = "1")
    @Disabled
    public AgreementRoleType getType() {
        return type;
    }

    public void setType(final AgreementRoleType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @Optional
    @Disabled
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @Optional
    @Disabled
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public AgreementRole changeDates(
            final @Named("Start Date") @Optional LocalDate startDate,
            final @Named("End Date") @Optional LocalDate endDate) {
        helper.changeDates(startDate, endDate);
        return this;
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return isLocked() ? "Cannot modify when locked" : null;
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

    @Hidden
    @Override
    public Agreement<?> getWithIntervalParent() {
        return getAgreement();
    }

    @Hidden
    @Override
    public LocalDate getEffectiveStartDate() {
        return WithInterval.Util.effectiveStartDateOf(this);
    }

    @Hidden
    @Override
    public LocalDate getEffectiveEndDate() {
        return WithInterval.Util.effectiveEndDateOf(this);
    }

    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getEffectiveStartDate(), getEffectiveEndDate());
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public AgreementRole getPredecessor() {
        return helper.getPredecessor(getAgreement().getRoles(), getType().matchingRole());
    }

    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public AgreementRole getSuccessor() {
        return helper.getSuccessor(getAgreement().getRoles(), getType().matchingRole());
    }

    @Render(Type.EAGERLY)
    @Override
    public SortedSet<AgreementRole> getTimeline() {
        return helper.getTimeline(getAgreement().getRoles(), getType().matchingRole());
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
            final @Named("Start date") LocalDate startDate,
            final @Named("End date") @Optional LocalDate endDate) {
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
            final @Named("Start date") @Optional LocalDate startDate,
            final @Named("End date") LocalDate endDate) {

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
    private SortedSet<AgreementRoleCommunicationChannel> communicationChannels = 
            new TreeSet<AgreementRoleCommunicationChannel>();

    @Disabled
    @Render(Type.EAGERLY)
    public SortedSet<AgreementRoleCommunicationChannel> getCommunicationChannels() {
        return communicationChannels;
    }

    public void setCommunicationChannels(final SortedSet<AgreementRoleCommunicationChannel> communinationChannels) {
        this.communicationChannels = communinationChannels;
    }

    // //////////////////////////////////////

    @Named("Create Initial")
    public AgreementRole newCommunicationChannel(
            final @Named("Type") AgreementRoleCommunicationChannelType type,
            final CommunicationChannel communicationChannel,
            final @Named("Start date") @Optional LocalDate startDate,
            final @Named("End date") @Optional LocalDate endDate) {
        createAgreementRoleCommunicationChannel(type, communicationChannel, startDate, endDate);
        return this;
    }

    public List<AgreementRoleCommunicationChannelType> choices0NewCommunicationChannel() {
        return getAgreement().getAgreementType().getRoleChannelTypesApplicableTo();
    }

    public List<CommunicationChannel> choices1NewCommunicationChannel() {
        return Lists.newArrayList(communicationChannelContributions.communicationChannels(getParty()));
    }

    public CommunicationChannel default1NewCommunicationChannel() {
        final SortedSet<CommunicationChannel> partyChannels = 
                communicationChannelContributions.communicationChannels(getParty());
        return !partyChannels.isEmpty() ? partyChannels.first() : null;
    }

    public String validateNewCommunicationChannel(
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
                communicationChannelContributions.communicationChannels(getParty());
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
        arcc.setStatus(Status.UNLOCKED);
        arcc.setCommunicationChannel(cc);

        // JDO will take care of bidir relationship
        arcc.setRole(this);
        persistIfNotAlready(arcc);

        return arcc;
    }

    // //////////////////////////////////////

    /**
     * A {@link Predicate} that tests whether the role's {@link AgreementRole#getType() type}
     * is the specified value.
     */
    public static Predicate<AgreementRole> whetherTypeIs(final AgreementRoleType art) {
        return new Predicate<AgreementRole>(){

            @Override
            public boolean apply(final AgreementRole input) {
                return input != null && input.getType() == art;
            }};
    }

    
    /**
     * A {@link Predicate} that tests whether the role's {@link AgreementRole#getAgreement() agreement}'s
     * {@link Agreement#getAgreementType() type} is the specified value.
     */
    public static Predicate<AgreementRole> whetherAgreementTypeIs(final AgreementType at) {
        return new Predicate<AgreementRole>(){
            
            @Override
            public boolean apply(final AgreementRole input) {
                return input != null && input.getAgreement().getAgreementType() == at;
            }};
    }
    
    /**
     * A {@link Predicate} that tests whether the role's {@link AgreementRole#isCurrent() current}
     * status is the specified value.
     */
    public static Predicate<AgreementRole> whetherCurrentIs(final boolean current) {
        return new Predicate<AgreementRole>() {
            public boolean apply(final AgreementRole candidate) {
                return candidate != null && candidate.isCurrent() == current;
            }
        };
    }

    /**
     * A {@link Function} that obtains the role's {@link AgreementRole#getParty() party} attribute.
     */
    static Function<AgreementRole, Party> partyOf() {
        return new Function<AgreementRole, Party>() {
            public Party apply(final AgreementRole agreementRole) {
                return agreementRole != null ? agreementRole.getParty() : null;
            }
        };
    }

    /**
     * A {@link Function} that obtains the role's {@link AgreementRole#getEffectiveEndDate() effective end date} 
     * attribute.
     */
    static Function<AgreementRole, LocalDate> effectiveEndDateOf() {
        return new Function<AgreementRole, LocalDate>() {
            @Override
            public LocalDate apply(final AgreementRole input) {
                return input != null? input.getEffectiveEndDate(): null;
            }};
    }
    

    // //////////////////////////////////////

    /**
     * Called by migration API.
     */
    @Programmatic
    public void addCommunicationChannel(
            final AgreementRoleCommunicationChannelType type,
            final CommunicationChannel communicationChannel) {
        if (type == null || communicationChannel == null) {
            return;
        }
        AgreementRoleCommunicationChannel arcc = findCommunicationChannel(type, getClockService().now());
        if (arcc != null) {
            return;
        }

        createAgreementRoleCommunicationChannel(type, communicationChannel, startDate, null);
    }

    private AgreementRoleCommunicationChannel findCommunicationChannel(
            final AgreementRoleCommunicationChannelType type, final LocalDate date) {
        return agreementRoleCommunicationChannels.findByRoleAndTypeAndContainsDate(this, type, date);
    }

    // //////////////////////////////////////

    private CommunicationChannelContributions communicationChannelContributions;

    public final void injectCommunicationChannelContributions(
            final CommunicationChannelContributions communicationChannelContributions) {
        this.communicationChannelContributions = communicationChannelContributions;
    }

    private AgreementRoleCommunicationChannels agreementRoleCommunicationChannels;

    public final void injectAgreementRoleCommunicationChannels(
            final AgreementRoleCommunicationChannels agreementRoleCommunicationChannels) {
        this.agreementRoleCommunicationChannels = agreementRoleCommunicationChannels;
    }
}
