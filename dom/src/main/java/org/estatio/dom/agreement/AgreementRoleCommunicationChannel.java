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

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberGroups;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.communicationchannel.CommunicationChannel;
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
                    "&& endDate == :endDate"),
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
@MemberGroups({"General", "Dates", "Related"})
public class AgreementRoleCommunicationChannel extends EstatioTransactionalObject<AgreementRoleCommunicationChannel, Status> implements WithIntervalMutable<AgreementRoleCommunicationChannel>{

    public AgreementRoleCommunicationChannel() {
        super("startDate desc, type, communicationChannel, role", Status.LOCKED, Status.UNLOCKED);
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

    @javax.jdo.annotations.Column(name = "LEASEROLE_ID")
    private AgreementRole role;

    @Title(sequence="2")
    @MemberOrder(sequence = "1")
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

    @MemberOrder(sequence = "2")
    @Title(sequence="1", append=":")
    @Disabled
    public AgreementRoleCommunicationChannelType getType() {
        return type;
    }

    public void setType(AgreementRoleCommunicationChannelType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "COMMUNICATIONCHANNEL_ID")
    private CommunicationChannel communicationChannel;

    @Title(sequence="3", prepend=",")
    @MemberOrder(sequence = "3")
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

    @MemberOrder(name="Dates", sequence = "4")
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

    @MemberOrder(name="Dates", sequence = "5")
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

    @MemberOrder(name="endDate", sequence="1")
    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public AgreementRoleCommunicationChannel changeDates(
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
    public AgreementRole getParentWithInterval() {
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

    @Hidden // TODO (where = Where.ALL_TABLES)
    @MemberOrder(name="Related", sequence="1")
    @Named("Previous Channel")
    @Disabled
    @Optional
    @Override
    public AgreementRoleCommunicationChannel getPrevious() {
        return null;
    }

    @Hidden // TODO (where = Where.ALL_TABLES)
    @MemberOrder(name="Related", sequence="2")
    @Named("Next Channel")
    @Disabled
    @Optional
    @Override
    public AgreementRoleCommunicationChannel getNext() {
        return null;
    }


}
