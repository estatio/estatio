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

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.name.Named;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.filter.Filter;
import org.apache.isis.applib.util.ObjectContracts;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.services.clock.ClockService;

@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
        name = "findByAgreementAndPartyAndTypeAndStartDate", language = "JDOQL", 
        value = "SELECT " +
        		"FROM org.estatio.dom.agreement.AgreementRole " +
        		"WHERE agreement == :agreement " +
        		"&& party == :party " +
        		"&& type == :type " +
        		"&& startDate == :startDate"),
	@javax.jdo.annotations.Query(
        name = "findByAgreementAndPartyAndTypeAndEndDate", language = "JDOQL", 
        value = "SELECT " +
                "FROM org.estatio.dom.agreement.AgreementRole " +
                "WHERE agreement == :agreement " +
                "&& party == :party " +
                "&& type == :type " +
                "&& endDate == :endDate"),
	@javax.jdo.annotations.Query(
        name = "findByAgreementAndTypeAndContainsDate", language = "JDOQL", 
        value = "SELECT " +
                "FROM org.estatio.dom.agreement.AgreementRole " +
                "WHERE agreement == :agreement " +
                "&& type == :type "+ 
                "&& (startDate == null | startDate < :date) "+
                "&& (endDate == null | endDate > :date) ")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class AgreementRole extends EstatioTransactionalObject<AgreementRole, Status> implements WithIntervalMutable<AgreementRole> {

    public AgreementRole() {
        super("agreement, startDate desc nullsLast, type, party", Status.LOCKED, Status.UNLOCKED);
    }

    // //////////////////////////////////////

    private Status status;

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

    @javax.jdo.annotations.Column(name = "AGREEMENT_ID")
    private Agreement<?> agreement;

    @Title(sequence = "3", prepend = ":")
    @MemberOrder(sequence = "1")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Agreement<?> getAgreement() {
        return agreement;
    }

    public void setAgreement(final Agreement<?> agreement) {
        this.agreement = agreement;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "PARTY_ID")
    private Party party;

    @Title(sequence = "2", prepend = ":")
    @MemberOrder(sequence = "2")
    @Hidden(where = Where.REFERENCES_PARENT)
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "TYPE_ID")
    private AgreementRoleType type;

    @Title(sequence = "1")
    @MemberOrder(sequence = "3")
    @Hidden(where=Where.ALL_TABLES)
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

    @MemberOrder(name="Dates", sequence = "4")
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

    @MemberOrder(name="Dates", sequence = "5")
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

    @MemberOrder(name="endDate", sequence="1")
    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public AgreementRole changeDates(
            final @Named("Start Date") LocalDate startDate, 
            final @Named("End Date") LocalDate endDate) {
        setStartDate(startDate);
        setEndDate(endDate);
        return this;
    }

    public String disableChangeDates(
            final LocalDate startDate, 
            final LocalDate endDate) {
        return getStatus().isLocked()? "Cannot modify when locked": null;
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
        return startDate.isBefore(endDate)?null:"Start date must be before end date";
    }
    
    // //////////////////////////////////////

    @Hidden
    @Override
    public Agreement<?> getParentWithInterval() {
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

    @MemberOrder(name="Related", sequence = "9.1")
    @Named("Previous Role")
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public AgreementRole getPrevious() {
        return WithInterval.Util.find(getAgreement().getRoles(), matchingEndDate(getType(), getStartDate()));
    }

    @MemberOrder(name="Related", sequence = "9.2")
    @Named("Next Role")
    @Hidden(where=Where.ALL_TABLES)
    @Disabled
    @Optional
    @Override
    public AgreementRole getNext() {
        return WithInterval.Util.find(getAgreement().getRoles(), matchingStartDate(getType(), getEndDate()));
    }

    @SuppressWarnings("unused")
    private static Predicate<AgreementRole> not(final AgreementRole ar) {
        return new Predicate<AgreementRole>(){
            @Override
            public boolean apply(AgreementRole input) {
                return input != null && input != ar;
            }
        };
    }

    private static Predicate<AgreementRole> matchingStartDate(final AgreementRoleType type, final LocalDate startDate) {
        return new Predicate<AgreementRole>(){
            @Override
            public boolean apply(final AgreementRole ar) {
                if(startDate == null) { return false; }
                if(ar == null) { return false; }
                if(!Objects.equal(ar.getType(), type)) { return false; } 
                if(!Objects.equal(ar.getStartDate(), startDate)) { return false; }
                return true;
            }
        };
    }

    private static Predicate<AgreementRole> matchingEndDate(final AgreementRoleType type, final LocalDate endDate) {
        return new Predicate<AgreementRole>(){
            @Override
            public boolean apply(final AgreementRole ar) {
                if(endDate == null) { return false; }
                if(ar == null) { return false; }
                if(!Objects.equal(ar.getType(), type)) { return false; } 
                if(!Objects.equal(ar.getEndDate(), endDate)) { return false; }
                return true;
            }
        };
    }
    
    // //////////////////////////////////////
    
    @MemberOrder(name="Next", sequence = "1")
    public void succeededBy(
            final Party party, 
            final @Named("Start date") LocalDate startDate, 
            final @Named("End date") @Optional LocalDate endDate) {
        final AgreementRole successor = getNext();
        if(successor != null) {
            successor.setStartDate(endDate);
        }
        setEndDate(startDate);
        getAgreement().newRole(getType(), party, startDate, endDate);
    }
    
    public LocalDate default1SucceededBy() {
        return getEndDate();
    }
    
    public String validateSucceededBy(
            final Party party, 
            final LocalDate startDate, 
            final LocalDate endDate) {
        if(party == getParty()) {
            return "Successor's party cannot be the same as this object's party";
        }
        if(getStartDate() != null && !getStartDate().isBefore(startDate)) {
            return "Successor must start after existing";
        }
        final AgreementRole successor = getNext();
        if(successor != null) {
            if (party == successor.getParty()) {
                return "Successor's party cannot be the same as that of existing successor";
            }
            if (endDate == null) {
                return "An end date is required because a successor already exists";
            }
            if(successor.getEndDate() != null && !endDate.isBefore(successor.getEndDate())) {
                return "Successor must end prior to existing successor";
            }
        }
        return null;
    }

    // //////////////////////////////////////

    @MemberOrder(name="Next", sequence = "2")
    public void precededBy(
            final Party party, 
            final @Named("Start date") @Optional LocalDate startDate, 
            final @Named("End date") LocalDate endDate) {
        final AgreementRole predecessor = getPrevious();
        if(predecessor != null) {
            predecessor.setEndDate(startDate);
        }
        setStartDate(endDate);
        getAgreement().newRole(getType(), party, startDate, endDate);
    }
    
    public LocalDate default2PrecededBy() {
        return getStartDate();
    }
    
    public String validatePrecededBy(
            final Party party, 
            final LocalDate startDate, 
            final LocalDate endDate) {
        if(party == getParty()) {
            return "Predecessor's party cannot be the same as this object's party";
        }
        if(getEndDate() != null && !getEndDate().isAfter(endDate)) {
            return "Predecessor must end before existing";
        }
        final AgreementRole predecessor = getPrevious();
        if(predecessor != null) {
            if (party == predecessor.getParty()) {
                return "Predecessor's party cannot be the same as that of existing predecessor";
            }
            if (startDate == null) {
                return "A start date is required because a predecessor already exists";
            }
            if(predecessor.getStartDate() != null && !startDate.isAfter(predecessor.getStartDate())) {
                return "Predecessor must start after existing predecessor";
            }
        }
        return null;
    }

    
    // //////////////////////////////////////

    @MemberOrder(name="End date", sequence = "1")
    public void updateDates(
            final @Named("Start date") @Optional LocalDate startDate, 
            final @Named("End date") @Optional LocalDate endDate) {
        
        final AgreementRole predecessor = getPrevious();
        if(predecessor != null) {
            predecessor.setEndDate(startDate);
        }
        final AgreementRole successor = getNext();
        if(successor != null) {
            successor.setStartDate(endDate);
        }
        setStartDate(startDate);
        setEndDate(endDate);
    }
    
    public LocalDate default0UpdateDates() {
        return getStartDate();
    }
    public LocalDate default1UpdateDates() {
        return getEndDate();
    }

    public String validateUpdateDates(
            final LocalDate startDate, 
            final LocalDate endDate) {

        if(startDate != null && endDate != null && !startDate.isBefore(endDate)) {
            return "Start date cannot be on/after the end date";
        }
        final AgreementRole predecessor = getPrevious();
        if (predecessor != null) {
            if(startDate == null) {
                return "Start date cannot be set to null if there is a predecessor";
            }
            if(predecessor.getStartDate() != null && !predecessor.getStartDate().isBefore(startDate)) {
                return "Start date cannot be on/before start of current predecessor";
            }
        }
        final AgreementRole successor = getNext();
        if (successor != null) {
            if(endDate == null) {
                return "End date cannot be set to null if there is a successor";
            }
            if(successor.getEndDate() != null && !successor.getEndDate().isAfter(endDate)) {
                return "End date cannot be on/after end of current successor";
            }
        }
        return null;
    }
    
    
    // //////////////////////////////////////

    private SortedSet<AgreementRoleCommunicationChannel> communicationChannels = new TreeSet<AgreementRoleCommunicationChannel>();

    @Disabled
    @Render(Type.EAGERLY)
    @MemberOrder(sequence = "1")
    public SortedSet<AgreementRoleCommunicationChannel> getCommunicationChannels() {
        return communicationChannels;
    }

    public void setCommunicationChannels(final SortedSet<AgreementRoleCommunicationChannel> communinationChannels) {
        this.communicationChannels = communinationChannels;
    }

    public void addToCommunicationChannels(final AgreementRoleCommunicationChannel channel) {
        if (channel == null || getCommunicationChannels().contains(channel)) {
            return;
        }
        channel.clearRole();
        channel.setRole(this);
        getCommunicationChannels().add(channel);
    }

    public void removeFromCommunicationChannels(final AgreementRoleCommunicationChannel channel) {
        if (channel == null || !getCommunicationChannels().contains(channel)) {
            return;
        }
        channel.setRole(null);
        getCommunicationChannels().remove(channel);
    }

    // //////////////////////////////////////

    @Programmatic
    public AgreementRoleCommunicationChannel findCommunicationChannel(final AgreementRoleCommunicationChannelType type, final LocalDate date) {
        return firstMatch(AgreementRoleCommunicationChannel.class, new Filter<AgreementRoleCommunicationChannel>() {
            @Override
            public boolean accept(AgreementRoleCommunicationChannel t) {
                return t.getType() == type && getInterval().contains(date);
            }
        });
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.IDEMPOTENT)
    @MemberOrder(name="communicationChannels", sequence="1")
    public AgreementRole addCommunicationChannel(@Named("Type") AgreementRoleCommunicationChannelType type, @Named("Communication Channel") CommunicationChannel communicationChannel) {
        if (type == null || communicationChannel == null) {
            return this;
        } 
        AgreementRoleCommunicationChannel arcc = findCommunicationChannel(type, clockService.now());
        if (arcc != null) {
            return this;
        } 
        arcc = newTransientInstance(AgreementRoleCommunicationChannel.class);
        arcc.setStartDate(startDate);
        arcc.setCommunicationChannel(communicationChannel);
        arcc.setType(type);
        arcc.setStatus(Status.UNLOCKED);
        persistIfNotAlready(arcc);
        addToCommunicationChannels(arcc);
        return this;
    }

    public List<AgreementRoleCommunicationChannelType> choices0AddCommunicationChannel() {
        return getAgreement().getAgreementType().getRoleChannelTypesApplicableTo();
    }

    public List<CommunicationChannel> choices1AddCommunicationChannel() {
        return Lists.newArrayList(getParty().getCommunicationChannels());
    }
    
    public CommunicationChannel default1AddCommunicationChannel() {
        final SortedSet<CommunicationChannel> partyChannels = getParty().getCommunicationChannels();
        return !partyChannels.isEmpty() ? partyChannels.first() : null;
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "7")
    public boolean isCurrent() {
        return isActiveOn(clockService.now());
    }

    private boolean isActiveOn(LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

    private ClockService clockService;

    public void injectClockService(final ClockService clockService) {
        this.clockService = clockService;
    }

    private AgreementRoles agreementRoles;
    
    public void injectAgreementRoles(AgreementRoles agreementRoles) {
        this.agreementRoles = agreementRoles;
    }



}
