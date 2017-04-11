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

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyRepository;

import org.incode.module.base.dom.types.MoneyType;
import org.incode.module.base.dom.types.NameType;
import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.with.WithReferenceUnique;
import org.incode.module.docfragment.dom.types.AtPathType;

import org.estatio.dom.UdoDomainObject;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobalAndCountry;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
		identityType = IdentityType.DATASTORE
		,schema = "dbo"	// Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Unique(members={"reference"})
@Queries({
		@Query(name = "findByReference", language = "JDOQL", value = "SELECT "
				+ "FROM org.estatio.dom.project.Project "
				+ "WHERE reference == :reference "),
		@Query(name = "matchByReferenceOrName", language = "JDOQL", value = "SELECT "
				+ "FROM org.estatio.dom.project.Project "
				+ "WHERE reference.matches(:matcher) || name.matches(:matcher) ") })
@DomainObject(
		editing = Editing.DISABLED,
		objectType = "org.estatio.dom.project.Project"		// TODO: externalize mapping
)
public class Project extends UdoDomainObject<Project> implements
		WithReferenceUnique, WithApplicationTenancyGlobalAndCountry {

	public Project() {
		super("reference, name, startDate");
	}

	public TranslatableString title() {
		return TranslatableString.tr("{name}", "name", "[" + getReference() + "] " + getName());
	}


	@Column(length = ReferenceType.Meta.MAX_LEN, allowsNull = "false")
	@Property(regexPattern = ReferenceType.Meta.REGEX)
	@PropertyLayout(describedAs = "Unique reference code for this project")
	@Getter @Setter
	private String reference;

	@Column(length = NameType.Meta.MAX_LEN, allowsNull = "false")
    @Getter @Setter
    private String name;

	@Column(allowsNull = "true")
    @Getter @Setter
    private LocalDate startDate;

	@Column(allowsNull = "true")
	@Persistent
	@MemberOrder(sequence="4")
    @Getter @Setter
    private LocalDate endDate;

	@Column(allowsNull = "true", scale = MoneyType.Meta.SCALE)
	@MemberOrder(sequence="7")
    @Getter @Setter
    private BigDecimal budgetedAmount;

	@Column(allowsNull = "false", length = AtPathType.Meta.MAX_LEN)
	@Getter @Setter
	@Property(hidden = Where.EVERYWHERE)
	private String atPath;

	@PropertyLayout(
			named = "Application Level",
			describedAs = "Determines those users for whom this object is available to view and/or modify.",
			hidden = Where.PARENTED_TABLES
	)
	public ApplicationTenancy getApplicationTenancy() {
		return applicationTenancyRepository.findByPath(getAtPath());
	}

	@Persistent(mappedBy = "parent", dependentElement = "true")
	@Getter @Setter
	private SortedSet<Project> children = new TreeSet<Project>();

	@Column(allowsNull = "true")
	@Getter @Setter
	private Project parent;

	@Inject
	ApplicationTenancyRepository applicationTenancyRepository;

}
