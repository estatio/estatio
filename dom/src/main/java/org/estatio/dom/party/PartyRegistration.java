/**
 * or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.dom.party;

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

import org.estatio.dom.EstatioTransactionalObject;
import org.estatio.dom.Status;
import org.estatio.dom.WithInterval;
import org.estatio.dom.WithIntervalMutable;
import org.estatio.dom.valuetypes.LocalDateInterval;

// REVIEW: is this in scope?
@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "VERSION")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByPartyAndPartyRegistrationTypeAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.party.PartyRegistration "
                        + "WHERE party == :party "
                        + "&& partyRegistrationType == :partyRegistrationType "
                        + "&& startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByPartyAndPartyRegistrationTypeAndEndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.party.PartyRegistration "
                        + "WHERE party == :party "
                        + "&& partyRegistrationType == :partyRegistrationType "
                        + "&& endDate == :endDate")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public class PartyRegistration extends EstatioTransactionalObject<PartyRegistration, Status> implements WithIntervalMutable<PartyRegistration> {

    public PartyRegistration() {
        // TODO: I made this up...
        super("party, startDate desc nullsLast", Status.LOCKED, Status.UNLOCKED);
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

    @javax.jdo.annotations.Column(name = "PARTY_ID")
    private Party party;

    @MemberOrder(sequence = "1")
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // //////////////////////////////////////

    private PartyRegistrationType partyRegistrationType;

    @MemberOrder(sequence = "1")
    public PartyRegistrationType getPartyRegistrationType() {
        return partyRegistrationType;
    }

    public void setPartyRegistrationType(final PartyRegistrationType partyRegistrationType) {
        this.partyRegistrationType = partyRegistrationType;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate startDate;

    @MemberOrder(name = "Dates", sequence = "1")
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

    @MemberOrder(name = "Dates", sequence = "1")
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

    @MemberOrder(name = "endDate", sequence = "1")
    @ActionSemantics(Of.IDEMPOTENT)
    @Override
    public PartyRegistration changeDates(
            final @Named("Start Date") LocalDate startDate,
            final @Named("End Date") LocalDate endDate) {
        setStartDate(startDate);
        setEndDate(endDate);
        return this;
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return getStatus().isLocked() ? "Cannot modify when locked" : null;
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
        return startDate.isBefore(endDate) ? null : "Start date must be before end date";
    }

    // //////////////////////////////////////

    @Hidden
    @Override
    public WithInterval<?> getParentWithInterval() {
        return null;
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
    @Override
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Hidden
    // TODO (where=Where.ALL_TABLES)
    @MemberOrder(name = "Related", sequence = "9.1")
    @Named("Previous Registration")
    @Disabled
    @Optional
    @Override
    public PartyRegistration getPrevious() {
        return null;
    }

    @Hidden
    // TODO (where=Where.ALL_TABLES)
    @MemberOrder(name = "Related", sequence = "9.2")
    @Named("Next Registration")
    @Disabled
    @Optional
    @Override
    public PartyRegistration getNext() {
        return null;
    }

    // //////////////////////////////////////

}
