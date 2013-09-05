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

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.NotPersisted;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
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

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findByRoleAndTypeAndStartDate", language = "JDOQL", 
            value = "SELECT " +
                    "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannel " +
                    "WHERE role == :agreementRole " +
                    "&& type == :type " +
                    "&& startDate == :startDate"),
    @javax.jdo.annotations.Query(
            name = "findByRoleAndTypeAndEndDate", language = "JDOQL", 
            value = "SELECT " +
                    "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannel " +
                    "WHERE role == :agreementRole " +
                    "&& type == :type " +
                    "&& endDate == :endDate")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class AgreementRoleCommunicationChannel extends EstatioTransactionalObject<AgreementRoleCommunicationChannel, Status> implements WithIntervalContiguous<AgreementRoleCommunicationChannel>{

    private WithIntervalContiguous.Helper<AgreementRoleCommunicationChannel> helper = 
            new WithIntervalContiguous.Helper<AgreementRoleCommunicationChannel>(this);

    // //////////////////////////////////////

    public AgreementRoleCommunicationChannel() {
        super("role, startDate desc nullsLast, type, communicationChannel", Status.UNLOCKED, Status.LOCKED);
    }

    // //////////////////////////////////////

    private Status status;

    // @javax.jdo.annotations.Column(allowsNull="false")
    @Optional

    @Hidden
    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(final Status status) {
        this.status = status;
    }


    // //////////////////////////////////////

    private AgreementRole role;

    @javax.jdo.annotations.Column(name = "AGREEMENTROLE_ID", allowsNull="false")
    @Title(sequence="2")
    @Hidden(where = Where.REFERENCES_PARENT)
    @Disabled
    public AgreementRole getRole() {
        return role;
    }

    public void setRole(AgreementRole agreementRole) {
        this.role = agreementRole;
    }

    public void modifyRole(final AgreementRole role) {
        AgreementRole currentRole = getRole();
        if (role == null || role.equals(currentRole)) {
            return;
        }
        setRole(role);
    }

    public void clearRole() {
        AgreementRole currentRole = getRole();
        if (currentRole == null) {
            return;
        }
        setRole(null);
    }

    // //////////////////////////////////////

    private AgreementRoleCommunicationChannelType type;

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence="1", append=":")
    @Disabled
    public AgreementRoleCommunicationChannelType getType() {
        return type;
    }

    public void setType(AgreementRoleCommunicationChannelType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private CommunicationChannel communicationChannel;

    @javax.jdo.annotations.Column(name = "COMMUNICATIONCHANNEL_ID", allowsNull="false")
    @Title(sequence="3", prepend=",")
    @Disabled
    public CommunicationChannel getCommunicationChannel() {
        return communicationChannel;
    }

    public void setCommunicationChannel(CommunicationChannel communicationChannel) {
        this.communicationChannel = communicationChannel;
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
    public void setStartDate(LocalDate startDate) {
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
    public void setEndDate(LocalDate localDate) {
        this.endDate = localDate;
    }

    
    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public AgreementRoleCommunicationChannel changeDates(
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
        return getStartDate();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return getEndDate();
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
    public AgreementRole getWithIntervalParent() {
        return getRole();
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

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getEffectiveStartDate(), getEffectiveEndDate());
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public AgreementRoleCommunicationChannel getPredecessor() {
        return helper.getPredecessor(getRole().getCommunicationChannels(), getType().matchingCommunicationChannel());
    }

    @Hidden(where = Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public AgreementRoleCommunicationChannel getSuccessor() {
        return helper.getSuccessor(getRole().getCommunicationChannels(), getType().matchingCommunicationChannel());
    }

    @Render(Type.EAGERLY)
    @Override
    public SortedSet<AgreementRoleCommunicationChannel> getTimeline() {
        return helper.getTimeline(getRole().getCommunicationChannels(), getType().matchingCommunicationChannel());
    }

    // //////////////////////////////////////


    static final class SiblingFactory implements WithIntervalContiguous.Factory<AgreementRoleCommunicationChannel> {
        private final AgreementRoleCommunicationChannel arcc;
        private final CommunicationChannel cc;

        public SiblingFactory(AgreementRoleCommunicationChannel arcc, CommunicationChannel cc) {
            this.arcc = arcc;
            this.cc = cc;
        }

        @Override
        public AgreementRoleCommunicationChannel newRole(LocalDate startDate, LocalDate endDate) {
            return arcc.getRole().createAgreementRoleCommunicationChannel(arcc.getType(), cc, startDate, endDate);
        }
    }

    public AgreementRoleCommunicationChannel succeededBy(
            final CommunicationChannel communicationChannel,
            final @Named("Start date") LocalDate startDate,
            final @Named("End date") @Optional LocalDate endDate) {
        return helper.succeededBy(
                startDate, endDate, new SiblingFactory(this, communicationChannel));
    }

    public List<CommunicationChannel> choices0SucceededBy() {
        return Lists.newArrayList(communicationChannelsForRolesParty());
    }

    public CommunicationChannel default0SucceededBy() {
        final SortedSet<CommunicationChannel> partyChannels = communicationChannelsForRolesParty();
        return !partyChannels.isEmpty() ? partyChannels.first() : null;
    }

    public LocalDate default1SucceededBy() {
        return helper.default1SucceededBy();
    }

    public String validateSucceededBy(
            final CommunicationChannel communicationChannel,
            final LocalDate startDate,
            final LocalDate endDate) {
        String invalidReasonIfAny = helper.validateSucceededBy(startDate, endDate);
        if (invalidReasonIfAny != null) {
            return invalidReasonIfAny;
        }

        if (communicationChannel == getCommunicationChannel()) {
            return "Successor's communication channel cannot be the same as this object's communication channel";
        }
        final AgreementRoleCommunicationChannel successor = getSuccessor();
        if (successor != null && communicationChannel == successor.getCommunicationChannel()) {
            return "Successor's communication channel cannot be the same as that of existing successor";
        }
        final SortedSet<CommunicationChannel> partyChannels = communicationChannelsForRolesParty();
        if(!partyChannels.contains(communicationChannel)) {
            return "Successor's communication channel must be one of those of the parent role's party";
        }

        return null;
    }


    public AgreementRoleCommunicationChannel precededBy(
            final CommunicationChannel communicationChannel,
            final @Named("Start date") @Optional LocalDate startDate,
            final @Named("End date") LocalDate endDate) {

        return helper.precededBy(startDate, endDate, new SiblingFactory(this, communicationChannel));
    }

    public List<CommunicationChannel> choices0PrecededBy() {
        return Lists.newArrayList(communicationChannelsForRolesParty());
    }

    public CommunicationChannel default0PrecededBy() {
        final SortedSet<CommunicationChannel> partyChannels = communicationChannelsForRolesParty();
        return !partyChannels.isEmpty() ? partyChannels.first() : null;
    }

    public LocalDate default2PrecededBy() {
        return helper.default2PrecededBy();
    }

    public String validatePrecededBy(
            final CommunicationChannel communicationChannel,
            final LocalDate startDate,
            final LocalDate endDate) {
        final String invalidReasonIfAny = helper.validatePrecededBy(startDate, endDate);
        if (invalidReasonIfAny != null) {
            return invalidReasonIfAny;
        }

        if (communicationChannel == getCommunicationChannel()) {
            return "Successor's communication channel cannot be the same as this object's communication channel";
        }
        final AgreementRoleCommunicationChannel predecessor = getPredecessor();
        if (predecessor != null && communicationChannel == predecessor.getCommunicationChannel()) {
            return "Predecessor's communication channel cannot be the same as that of existing predecessor";
        }
        final SortedSet<CommunicationChannel> partyChannels = communicationChannelsForRolesParty();
        if(!partyChannels.contains(communicationChannel)) {
            return "Predecessor's communication channel must be one of those of the parent role's party";
        }
        return null;
    }

    // //////////////////////////////////////
    
    private SortedSet<CommunicationChannel> communicationChannelsForRolesParty() {
        return communicationChannelContributions.communicationChannels(getRole().getParty());
    }

    
    // //////////////////////////////////////

    private CommunicationChannelContributions communicationChannelContributions;
    public void injectCommunicationChannelContributions(CommunicationChannelContributions communicationChannelContributions) {
        this.communicationChannelContributions = communicationChannelContributions;
    }


}
