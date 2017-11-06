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
package org.estatio.capex.dom.project;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.TitleBuffer;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.UdoDomainObject;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.role.IPartyRoleType;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
		identityType = IdentityType.DATASTORE
		,schema = "dbo"
)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Queries({
		@javax.jdo.annotations.Query(
				name = "findByProject", language = "JDOQL",
				value = "SELECT " +
						"FROM org.estatio.capex.dom.project.ProjectRole "
						+ "WHERE project == :project "),
		@javax.jdo.annotations.Query(
				name = "findByParty", language = "JDOQL",
				value = "SELECT " +
						"FROM org.estatio.capex.dom.project.ProjectRole "
						+ "WHERE party == :party "),
})
@DomainObject(
		editing = Editing.DISABLED,
		objectType = "org.estatio.capex.dom.project.ProjectRole"
)
public class ProjectRole extends UdoDomainObject<ProjectRole>  {

	public String title() {

		final TitleBuffer buf = new TitleBuffer()
				.append(titleService.titleOf(getParty()))
				.append(titleService.titleOf(getType()))
				.append("for")
				.append(titleService.titleOf(getProject()));
		return buf.toString();
	}

	public ProjectRole() {
		super("project, startDate desc nullsLast, type, party");
	}

	@Column(name = "projectId", allowsNull = "false")
	@Property(hidden = Where.REFERENCES_PARENT)
	@Getter @Setter
	private Project project;

	@Column(name = "partyId", allowsNull = "false")
	@Property(hidden = Where.REFERENCES_PARENT)
	@Getter @Setter
	private Party party;

	@Column(allowsNull = "false", length = IPartyRoleType.Meta.MAX_LEN)
	@Getter @Setter
	private ProjectRoleTypeEnum type;

	@Column(allowsNull = "true")
	@Getter @Setter
	private LocalDate startDate;

	@Column(allowsNull = "true")
	@Getter @Setter
	private LocalDate endDate;

	@Action(semantics = SemanticsOf.IDEMPOTENT)
	public ProjectRole changeDates(
			final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
			final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
		setStartDate(startDate);
		setEndDate(endDate);
		return this;
	}

	public LocalDate default0ChangeDates() {
		return getStartDate();
	}

	public LocalDate default1ChangeDates() {
		return getEndDate();
	}

	public String validateChangeDates(
			final LocalDate startDate,
			final LocalDate endDate) {
		if(startDate != null && endDate != null && startDate.isAfter(endDate)) {
			return "End date cannot be earlier than start date";
		}
		return null;
	}

	@Programmatic
	public LocalDateInterval getInterval() {
		return LocalDateInterval.including(getStartDate(), getEndDate());
	}

	@Programmatic
	public boolean isCurrent() {
		return isActiveOn(getClockService().now());
	}

	private boolean isActiveOn(final LocalDate localDate) {
		return getInterval().contains(localDate);
	}

	@Override
	@Property(hidden = Where.EVERYWHERE)
	public ApplicationTenancy getApplicationTenancy() {
		return getProject().getApplicationTenancy();
	}


	@Inject
	TitleService titleService;

}
