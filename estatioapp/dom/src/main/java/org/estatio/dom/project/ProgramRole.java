/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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
package org.estatio.dom.project;

import java.util.Iterator;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.UdoDomainObject;
import org.estatio.dom.WithIntervalContiguous;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobalAndCountry;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;

/**
 * Identifies the {@link #getParty() party} that plays a particular
 * {@link #getType() type} of role with respect to a {@link #getProgram() program
 * }, for a particular {@link #getInterval() interval of time}.
 */
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByProgram", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProgramRole "
                        + "WHERE program == :program "),
        @javax.jdo.annotations.Query(
                name = "findByProgramAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProgramRole "
                        + "WHERE program == :program "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(
                name = "findByProgramAndPartyAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProgramRole "
                        + "WHERE program == :program "
                        + "&& party == :party "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(
                name = "findByProgramAndPartyAndTypeAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProgramRole "
                        + "WHERE program == :program "
                        + "&& party == :party "
                        + "&& type == :type "
                        + "&& startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByProgramAndPartyAndTypeAndEndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProgramRole "
                        + "WHERE program == :program "
                        + "&& party == :party "
                        + "&& type == :type "
                        + "&& endDate == :endDate"),
        @javax.jdo.annotations.Query(
                name = "findByParty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProgramRole "
                        + "WHERE party == :party ")
})

@DomainObjectLayout(bookmarking=BookmarkPolicy.AS_CHILD)
public class ProgramRole
        extends UdoDomainObject<ProgramRole>
        implements WithIntervalContiguous<ProgramRole>, WithApplicationTenancyGlobalAndCountry {

    private WithIntervalContiguous.Helper<ProgramRole> helper =
            new WithIntervalContiguous.Helper<ProgramRole>(this);

    // //////////////////////////////////////

    public ProgramRole() {
        super("program, startDate desc nullsLast, type, party");
    }

    // //////////////////////////////////////

    private Program program;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Title(sequence = "3", prepend = ":")
    @Property(editing=Editing.DISABLED, hidden=Where.REFERENCES_PARENT)
    public Program getProgram() {
        return program;
    }

    public void setProgram(final Program program) {
        this.program = program;
    }

    // //////////////////////////////////////

    private Party party;

    @javax.jdo.annotations.Column(name = "partyId", allowsNull = "false")
    @Title(sequence = "2", prepend = ":")
    @Property(editing=Editing.DISABLED, hidden=Where.REFERENCES_PARENT)
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // //////////////////////////////////////

    private ProgramRoleType type;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TYPE_ENUM)
    @Property(editing=Editing.DISABLED)
    @Title(sequence = "1")
    public ProgramRoleType getType() {
        return type;
    }

    public void setType(final ProgramRoleType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @Property(editing=Editing.DISABLED, optionality=Optionality.OPTIONAL)
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

//    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @Property(editing=Editing.DISABLED, optionality=Optionality.OPTIONAL)
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    @Action(semantics=SemanticsOf.IDEMPOTENT)
    @Override
    public ProgramRole changeDates(
            final @ParameterLayout(named = "Start Date") @Parameter(optionality=Optionality.OPTIONAL) LocalDate startDate,
            final @ParameterLayout(named = "End Date") @Parameter(optionality=Optionality.OPTIONAL) LocalDate endDate) {
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
    	
        LocalDateInterval newInterval = new LocalDateInterval(startDate, endDate);
        for (Iterator<ProgramRole> it = programRoles.findByProgram(program).iterator(); it.hasNext();){
        	
        	ProgramRole pr = it.next();
        	if (!(pr.equals(this)) && pr.getParty().equals(party) && pr.getType().equals(type)){
        		
        		LocalDateInterval oldInterval = new LocalDateInterval(pr.getStartDate(), pr.getEndDate());
        		
        		if (newInterval.overlaps(oldInterval)) {
        			return "Same party, same role, cannot have overlapping period";
        		}
        		
        	}
        	
        }
    	
        return helper.validateChangeDates(startDate, endDate);
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Override
    @Programmatic
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

    @Property(editing=Editing.DISABLED, optionality=Optionality.OPTIONAL, hidden=Where.ALL_TABLES)
    @Override
    public ProgramRole getPredecessor() {
        return helper.getPredecessor(getProgram().getRoles(), getType().matchingRole());
//    	return helper.getPredecessor(programRoles.findByProgramSet(getProgram()), getType().matchingRole());
    }

    @Property(editing=Editing.DISABLED, optionality=Optionality.OPTIONAL, hidden=Where.ALL_TABLES)
    @Override
    public ProgramRole getSuccessor() {
        return helper.getSuccessor(getProgram().getRoles(), getType().matchingRole());
//    	return helper.getSuccessor(programRoles.findByProgramSet(getProgram()), getType().matchingRole());
    }

    @CollectionLayout(render=RenderType.EAGERLY)
    @Override
    public SortedSet<ProgramRole> getTimeline() {
        return helper.getTimeline(getProgram().getRoles(), getType().matchingRole());
//        return helper.getTimeline(programRoles.findByProgramSet(getProgram()), getType().matchingRole());
    }

    @Inject ProgramRoles programRoles;

    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify.",
            hidden = Where.PARENTED_TABLES
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getProgram().getApplicationTenancy();
    }

    // //////////////////////////////////////

    static final class SiblingFactory implements WithIntervalContiguous.Factory<ProgramRole> {
        private final ProgramRole pr;
        private final Party party;

        public SiblingFactory(final ProgramRole pr, final Party party) {
            this.pr = pr;
            this.party = party;
        }

        @Override
        public ProgramRole newRole(final LocalDate startDate, final LocalDate endDate) {
            return pr.getProgram().programRoles.createRole(pr.getProgram(), pr.getType(), party, startDate, endDate);
        }
    }

    public ProgramRole succeededBy(
            final Party party,
            final @ParameterLayout(named = "Start date") LocalDate startDate,
            final @ParameterLayout(named = "End Date") @Parameter(optionality=Optionality.OPTIONAL) LocalDate endDate) {
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
        final ProgramRole successor = getSuccessor();
        if (successor != null && party == successor.getParty()) {
            return "Successor's party cannot be the same as that of existing successor";
        }
        return null;
    }

    public ProgramRole precededBy(
            final Party party,
            final @ParameterLayout(named = "Start date") @Parameter(optionality=Optionality.OPTIONAL) LocalDate startDate,
            final @ParameterLayout(named = "End date") LocalDate endDate) {

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
        final ProgramRole predecessor = getPredecessor();
        if (predecessor != null && party == predecessor.getParty()) {
            return "Predecessor's party cannot be the same as that of existing predecessor";
        }
        return null;
    }

    // //////////////////////////////////////

    public final static class Functions {

        private Functions() {
        }

        /**
         * A {@link Function} that obtains the role's
         * {@link ProgramRole#getParty() party} attribute.
         */
        public static <T extends Party> Function<ProgramRole, T> partyOf() {
            return new Function<ProgramRole, T>() {
                @SuppressWarnings("unchecked")
                public T apply(final ProgramRole programRole) {
                    return (T) (programRole != null ? programRole.getParty() : null);
                }
            };
        }

        /**
         * A {@link Function} that obtains the role's
         * {@link ProgramRole#getProgram() program} attribute.
         */
        public static <T extends Program> Function<ProgramRole, T> programOf() {
            return new Function<ProgramRole, T>() {
                @SuppressWarnings("unchecked")
                public T apply(final ProgramRole programRole) {
                    return (T) (programRole != null ? programRole.getProgram() : null);
                }
            };
        }
    }

}
