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

import org.estatio.dom.UdoDomainObject;
import org.incode.module.base.dom.WithIntervalContiguous;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobalAndCountry;
import org.estatio.dom.party.Party;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;

import com.google.common.base.Function;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import lombok.Getter;
import lombok.Setter;

/**
 * Identifies the {@link #getParty() party} that plays a particular
 * {@link #getType() type} of role with respect to a {@link #getProgram() program
 * }, for a particular {@link #getInterval() interval of time}.
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "EstatioProject"  // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
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
@DomainObject(
        objectType = "org.estatio.dom.project.ProjectRole"
)
@DomainObjectLayout(bookmarking=BookmarkPolicy.AS_CHILD)
public class ProjectRole
        extends UdoDomainObject<ProjectRole>
        implements WithIntervalContiguous<ProjectRole>, WithApplicationTenancyGlobalAndCountry
{

    private WithIntervalContiguous.Helper<ProjectRole> helper =
            new WithIntervalContiguous.Helper<>(this);

    // //////////////////////////////////////

    public ProjectRole() {
        super("project, startDate desc nullsLast, type, party");
    }

    public String title() {
        return TitleBuilder.start()
                .withTupleElement(getProject())
                .withTupleElement(getParty())
                .withName(getType())
                .toString();
    }

    @javax.jdo.annotations.Column(allowsNull = "false", name="projectId")
    @Property(editing=Editing.DISABLED, hidden=Where.REFERENCES_PARENT)
    @Getter @Setter
    private Project project;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "partyId", allowsNull = "false")
    @Property(editing=Editing.DISABLED, hidden=Where.REFERENCES_PARENT)
    @Getter @Setter
    private Party party;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull = "false", length = ProjectRoleType.Type.MAX_LEN)
    @Property(editing=Editing.DISABLED)
    @Getter @Setter
    private ProjectRoleType type;

    // //////////////////////////////////////

    @Property(editing=Editing.DISABLED, optionality=Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate startDate;

    @Property(editing=Editing.DISABLED, optionality=Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate endDate;

    // //////////////////////////////////////

    @Action(semantics=SemanticsOf.IDEMPOTENT)
    @Override
    public ProjectRole changeDates(
            final @Parameter(optionality=Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality=Optionality.OPTIONAL) LocalDate endDate) {
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
        for (Iterator<ProjectRole> it = projectRoleRepository.findByProject(project).iterator(); it.hasNext();){
        	
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

    @Inject ProjectRoleRepository projectRoleRepository;

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
            return pr.getProject().projectRoleRepository.createRole(pr.getProject(),pr.getType(), party, startDate, endDate);
        }
    }

    public ProjectRole succeededBy(
            final Party party,
            final LocalDate startDate,
            final @Parameter(optionality=Optionality.OPTIONAL) LocalDate endDate) {
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
            final @Parameter(optionality=Optionality.OPTIONAL) LocalDate startDate,
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
