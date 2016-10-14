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
package org.estatio.dom.party;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainObject2;
import org.incode.module.base.dom.WithIntervalMutable;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import lombok.Getter;
import lombok.Setter;

// REVIEW: is this in scope?
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByPartyAndPartyRegistrationTypeAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.party.PartyRegistration "
                        + "WHERE party == :party "
                        + "   && type == :type "
                        + "   && startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByPartyAndPartyRegistrationTypeAndEndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.party.PartyRegistration "
                        + "WHERE party == :party "
                        + "   && type == :type "
                        + "   && endDate == :endDate")
})
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public class PartyRegistration
        extends UdoDomainObject2<PartyRegistration>
        implements WithIntervalMutable<PartyRegistration>, WithApplicationTenancyCountry {

    public PartyRegistration() {
        // TODO: I made this up...
        super("party, startDate desc nullsLast");
    }

    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getParty().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "partyId", allowsNull = "false")
    @Getter @Setter
    private Party party;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length= PartyRegistrationType.Type.MAX_LEN)
    @Getter @Setter
    private PartyRegistrationType type;

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate startDate;

    @Property(optionality = Optionality.OPTIONAL, editing = Editing.DISABLED)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate endDate;

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<PartyRegistration> changeDates =
            new WithIntervalMutable.Helper<>(this);

    WithIntervalMutable.Helper<PartyRegistration> getChangeDates() {
        return changeDates;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @Override
    public PartyRegistration changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return getChangeDates().changeDates(startDate, endDate);
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return null;
    }

    @Override
    public LocalDate default0ChangeDates() {
        return getChangeDates().default0ChangeDates();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return getChangeDates().default1ChangeDates();
    }

    @Override
    public String validateChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return getChangeDates().validateChangeDates(startDate, endDate);
    }

    // //////////////////////////////////////

    @Programmatic
    @Override
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Programmatic
    @Override
    public LocalDateInterval getEffectiveInterval() {
        return getInterval();
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

}
