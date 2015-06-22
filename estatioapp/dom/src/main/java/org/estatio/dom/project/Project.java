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

import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.JdoColumnScale;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainObject;
import org.estatio.dom.WithReferenceUnique;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobalAndCountry;
import org.estatio.dom.currency.Currency;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Unique(members={"reference"})
@Queries({
		@Query(name = "findByReference", language = "JDOQL", value = "SELECT "
				+ "FROM org.estatio.dom.project.Project "
				+ "WHERE reference == :reference "),
		@Query(name = "matchByReferenceOrName", language = "JDOQL", value = "SELECT "
				+ "FROM org.estatio.dom.project.Project "
				+ "WHERE reference.matches(:matcher) || name.matches(:matcher) "),
		@Query(name = "findByProgram", language = "JDOQL", value = "SELECT "
				+ "FROM org.estatio.dom.project.Project "
				+ "WHERE program == :program ") })
@DomainObject(editing = Editing.DISABLED, autoCompleteRepository = Projects.class, autoCompleteAction = "autoComplete")
public class Project extends UdoDomainObject<Project> implements
		WithReferenceUnique, WithApplicationTenancyGlobalAndCountry {

	public Project() {
		super("reference, name, startDate");
	}

	//region > identificatiom
	public TranslatableString title() {
		return TranslatableString.tr("{name}", "name", "[" + getReference() + "] " + getName());
	}
	//endregion
	// //////////////////////////////////////

	// //////////////////////////////////////

	private String reference;

	@Column(allowsNull = "false")
	@Property(regexPattern = RegexValidation.REFERENCE)
	@PropertyLayout(describedAs = "Unique reference code for this project")
	@MemberOrder(sequence="1")
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	// //////////////////////////////////////

	private String name;

	@Column(allowsNull = "false")
	@MemberOrder(sequence="2")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// //////////////////////////////////////

	private LocalDate startDate;

	@Column(allowsNull = "true")
	@MemberOrder(sequence="3")
	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	// //////////////////////////////////////

	private LocalDate endDate;

	@Column(allowsNull = "true")
	@Persistent
	@MemberOrder(sequence="4")
	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	// //////////////////////////////////////

	private Program program;

	@Column(name = "programId", allowsNull = "false")
	@Property(hidden = Where.REFERENCES_PARENT)
	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

	// //////////////////////////////////////

	//region > relatedObject (property)
	private String relatedObject;

	@MemberOrder(sequence = "5")
	@Column(allowsNull = "true")
	public String getRelatedObject() {
		return relatedObject;
	}

	public void setRelatedObject(final String relatedObject) {
		this.relatedObject = relatedObject;
	}
	//endregion

	private Currency currency;

	@Column(allowsNull = "true")
	@MemberOrder(sequence="6")
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	// //////////////////////////////////////

	private BigDecimal estimatedCost;

	@Column(allowsNull = "true", scale = JdoColumnScale.MONEY)
	@MemberOrder(sequence="7")
	public BigDecimal getEstimatedCost() {
		return estimatedCost;
	}

	public void setEstimatedCost(BigDecimal estimatedCost) {
		this.estimatedCost = estimatedCost;
	}

	// //////////////////////////////////////

	private ProjectPhase projectPhase;

	@Column(allowsNull = "true")
	@MemberOrder(sequence="4.5")
	public ProjectPhase getProjectPhase() {
		return projectPhase;
	}

	public void setProjectPhase(ProjectPhase projectPhase) {
		this.projectPhase = projectPhase;
	}

//	public Project postponeOneWeek(
//			@ParameterLayout(named = "Reason") String reason) {
//		setStartDate(getStartDate().plusWeeks(1));
//		return this;
//	}

	// //////////////////////////////////////
	
    //TODO: decouple sorted set [momentarily needed by code in  ProjectRole getPredecessor() etc.

	@javax.jdo.annotations.Persistent(mappedBy = "project")
	private SortedSet<ProjectRole> roles = new TreeSet<ProjectRole>();

	@CollectionLayout(render = RenderType.EAGERLY, hidden = Where.EVERYWHERE)
	public SortedSet<ProjectRole> getRoles() {
		return roles;
	}

	// public void setRoles(final SortedSet<ProjectRole> roles) {
	// this.roles = roles;
	// }
	
	// //////////////////////////////////////
	
	public Project updateDates(
			@Parameter(optionality=Optionality.OPTIONAL)
			@ParameterLayout(named = "Start date")
			final LocalDate startDate,
			@Parameter(optionality=Optionality.OPTIONAL)
			@ParameterLayout(named = "End date")
			final LocalDate endDate
			){
		
		this.setStartDate(startDate);
		this.setEndDate(endDate);
		return this;
	}
	
	public LocalDate default0UpdateDates(){
		return this.getStartDate();
	}
	
	public LocalDate default1UpdateDates(){
		return this.getEndDate();
	}
	
	public String validateUpdateDates(final LocalDate startDate, final LocalDate endDate){
		
		if (startDate.isAfter(endDate)) {
			return "Start date cannot be later than End date";
		}
		
		return null;
	}	
	
	// //////////////////////////////////////
	
	public Project updateCost(
			@Parameter(optionality=Optionality.OPTIONAL)
			@ParameterLayout(named = "Currency")
			final Currency currency,
			@Parameter(optionality=Optionality.OPTIONAL)
			@ParameterLayout(named = "Estimated cost")
			final BigDecimal estimatedCost
			){
		
		this.setCurrency(currency);;
		this.setEstimatedCost(estimatedCost);
		return this;
	}
	
	public Currency default0UpdateCost(){
		return this.getCurrency();
	}
	
	public BigDecimal default1UpdateCost(){
		return this.getEstimatedCost();
	}
	
	// //////////////////////////////////////
	
	public Project changeProject(
			@ParameterLayout(named = "Project name")
			final String name,
			@ParameterLayout(named = "Project phase")
			final ProjectPhase projectPhase){
		this.setName(name);
		this.setProjectPhase(projectPhase);
		return this;
	}
	
	public String default0ChangeProject(){
		return this.getName();
	}
	
	public ProjectPhase default1ChangeProject(){
		return this.getProjectPhase();
	}

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

	@Inject
	public ProjectRoles projectRoles;

	// //////////////////////////////////////
}
