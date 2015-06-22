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
import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.UdoDomainObject;
import org.estatio.dom.WithIntervalContiguous;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobalAndCountry;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;

import com.google.common.base.Function;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

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
                name = "findByProject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProjectRole "
                        + "WHERE project == :project "),
        @javax.jdo.annotations.Query(
                name = "findByProjectAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProjectRole "
                        + "WHERE project == :project "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(
                name = "findByProjectAndPartyAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProjectRole "
                        + "WHERE project == :project "
                        + "&& party == :party "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(
                name = "findByProjectAndPartyAndTypeAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProjectRole "
                        + "WHERE project == :project "
                        + "&& party == :party "
                        + "&& type == :type "
                        + "&& startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findByProjectAndPartyAndTypeAndEndDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProjectRole "
                        + "WHERE project == :project "
                        + "&& party == :party "
                        + "&& type == :type "
                        + "&& endDate == :endDate"),
        @javax.jdo.annotations.Query(
                name = "findByParty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.project.ProjectRole "
                        + "WHERE party == :party ")
})

@DomainObjectLayout(bookmarking=BookmarkPolicy.AS_CHILD)
public class ProjectRole
        extends UdoDomainObject<ProjectRole>
        implements WithIntervalContiguous<ProjectRole>, WithApplicationTenancyGlobalAndCountry
{

    private WithIntervalContiguous.Helper<ProjectRole> helper =
            new WithIntervalContiguous.Helper<ProjectRole>(this);

    // //////////////////////////////////////

    public ProjectRole() {
        super("project, startDate desc nullsLast, type, party");
    }

    // //////////////////////////////////////

    private Project project;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Title(sequence = "3", prepend = ":")
    @Property(editing=Editing.DISABLED, hidden=Where.REFERENCES_PARENT)
    public Project getProject() {
        return project;
    }

    public void setProject(final Project project) {
        this.project = project;
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

    private ProjectRoleType type;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TYPE_ENUM)
    @Property(editing=Editing.DISABLED)
    @Title(sequence = "1")
    public ProjectRoleType getType() {
        return type;
    }

    public void setType(final ProjectRoleType type) {
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
    public ProjectRole changeDates(
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
        for (Iterator<ProjectRole> it = projectRoles.findByProject(project).iterator(); it.hasNext();){
        	
        	ProjectRole pr = it.next();
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
    public ProjectRole getPredecessor() {
        return helper.getPredecessor(getProject().getRoles(), getType().matchingRole());
    }

    @Property(editing=Editing.DISABLED, optionality=Optionality.OPTIONAL, hidden=Where.ALL_TABLES)
    @Override
    public ProjectRole getSuccessor() {
        return helper.getSuccessor(getProject().getRoles(), getType().matchingRole());
    }

    @CollectionLayout(render=RenderType.EAGERLY)
    @Override
    public SortedSet<ProjectRole> getTimeline() {
        return helper.getTimeline(getProject().getRoles(), getType().matchingRole());
    }

    @Inject ProjectRoles projectRoles;

    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify.",
            hidden = Where.PARENTED_TABLES
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getProject().getApplicationTenancy();
    }

    // //////////////////////////////////////

    static final class SiblingFactory implements WithIntervalContiguous.Factory<ProjectRole> {
        private final ProjectRole pr;
        private final Party party;

        public SiblingFactory(final ProjectRole pr, final Party party) {
            this.pr = pr;
            this.party = party;
        }

        @Override
        public ProjectRole newRole(final LocalDate startDate, final LocalDate endDate) {
            return pr.getProject().projectRoles.createRole(pr.getProject(),pr.getType(), party, startDate, endDate);
        }
    }

    public ProjectRole succeededBy(
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
        final ProjectRole successor = getSuccessor();
        if (successor != null && party == successor.getParty()) {
            return "Successor's party cannot be the same as that of existing successor";
        }
        return null;
    }

    public ProjectRole precededBy(
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
        final ProjectRole predecessor = getPredecessor();
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
         * {@link ProjectRole#getParty() party} attribute.
         */
        public static <T extends Party> Function<ProjectRole, T> partyOf() {
            return new Function<ProjectRole, T>() {
                @SuppressWarnings("unchecked")
                public T apply(final ProjectRole projectRole) {
                    return (T) (projectRole != null ? projectRole.getParty() : null);
                }
            };
        }

        /**
         * A {@link Function} that obtains the role's
         * {@link ProjectRole#getProgram() program} attribute.
         */
        public static <T extends Project> Function<ProjectRole, T> projectOf() {
            return new Function<ProjectRole, T>() {
                @SuppressWarnings("unchecked")
                public T apply(final ProjectRole projectRole) {
                    return (T) (projectRole != null ? projectRole.getProject() : null);
                }
            };
        }
    }

}
