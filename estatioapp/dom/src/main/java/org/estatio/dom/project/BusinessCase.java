package org.estatio.dom.project;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.Chained;
import org.estatio.dom.UdoDomainObject;
import org.estatio.dom.apptenancy.WithApplicationTenancyGlobalAndCountry;

@PersistenceCapable(identityType = IdentityType.DATASTORE)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Queries({
    @Query(
            name = "findByProject", language = "JDOQL",
            value = "SELECT " +
                    "FROM org.estatio.dom.project.BusinessCase " +
                    "WHERE project == :project "),
    @Query(
            name = "findByProjectAndVersion", language = "JDOQL",
            value = "SELECT " +
                    "FROM org.estatio.dom.project.BusinessCase " +
                    "WHERE project == :project && businessCaseVersion == :businessCaseVersion"),
    @Query(
            name = "findByProjectAndActiveVersion", language = "JDOQL",
            value = "SELECT " +
                    "FROM org.estatio.dom.project.BusinessCase " +
                    "WHERE project == :project && isActiveVersion == :isActiveVersion"),
    @Query(
            name = "findActiveBusinessCaseOnProject", language = "JDOQL",
            value = "SELECT " +
                    "FROM org.estatio.dom.project.BusinessCase " +
                    "WHERE project == :project && next == null")                      
})
@DomainObject(editing=Editing.DISABLED)
public class BusinessCase extends UdoDomainObject<BusinessCase> implements Chained<BusinessCase>, WithApplicationTenancyGlobalAndCountry{

	public BusinessCase() {
		super("project, date, lastUpdated desc nullsLast, description");
	}

	//region > identificatiom
	public TranslatableString title() {
		return TranslatableString.tr("{name}", "name", "Businesscase for " + this.getProject().getReference() );
	}
	//endregion
	// //////////////////////////////////////
	
	private String description;

	@Column(allowsNull = "false")
    @PropertyLayout(multiLine = 5, describedAs = "Reason for the project and expected benefits")
	@MemberOrder(sequence="1")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// //////////////////////////////////////

	private Project project;
	
	@Column(name= "projectId", allowsNull = "false")
	@MemberOrder(sequence="8")
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	// //////////////////////////////////////

	private LocalDate date;
	
	@Column(allowsNull = "false")
	@MemberOrder(sequence="3")
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	// //////////////////////////////////////
	
	private LocalDate lastUpdated;
	
	@Column(allowsNull = "true")
	@MemberOrder(sequence="5")
	public LocalDate getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDate date) {
		this.lastUpdated = date;
	}

	// //////////////////////////////////////
	
	private LocalDate nextReviewDate;
	
	@Column(allowsNull = "true")
	@MemberOrder(sequence="4")
	public LocalDate getNextReviewDate() {
		return nextReviewDate;
	}

	public void setNextReviewDate(LocalDate date) {
		this.nextReviewDate = date;
	}

	// //////////////////////////////////////

	private Integer businessCaseVersion;
	
	@Column(allowsNull = "false")
	@MemberOrder(sequence="2")
	public Integer getBusinessCaseVersion() {
		return businessCaseVersion;
	}

	public void setBusinessCaseVersion(Integer businessCaseVersion) {
		this.businessCaseVersion = businessCaseVersion;
	}
	
	// //////////////////////////////////////
	
	@Action(semantics=SemanticsOf.NON_IDEMPOTENT)
	public BusinessCase updateBusinessCase(
			@ParameterLayout(
					named = "Description",
					multiLine = 5
					)
			final String businessCaseDescription,
			@ParameterLayout(named = "Next review date")
			final LocalDate reviewDate){
		
		new LocalDate();
		final LocalDate now = LocalDate.now();
		
		BusinessCase nextBusinesscase = businesscases.newBusinessCase(this.getProject(), businessCaseDescription, reviewDate, this.date, now, this.getBusinessCaseVersion() + 1);
		this.setNext(nextBusinesscase);
		
		return nextBusinesscase;
	}
	
	public String default0UpdateBusinessCase(){
		return this.getDescription();
	}
	
	public LocalDate default1UpdateBusinessCase(){
		return this.getNextReviewDate();
	}
	
	public boolean hideUpdateBusinessCase(final String businessCaseDescription, final LocalDate reviewDate) {
		
		if (this.getNext()==null) {
			return false;
		}
		
		return true;
	}
	
	public String validateUpdateBusinessCase(final String businessCaseDescription, final LocalDate reviewDate) {
		
		if (this.getNext()!=null) {
			return "This is no active version of the business case and cannot be updated";
		}
		
		new LocalDate();
		LocalDate now = LocalDate.now();
		if (reviewDate.isBefore(now)) {
			return "A review date should not be in the past";
		}
		
		return null;
	}
	
	// //////////////////////////////////////
	
	@Column(name="nextBusinessCaseId")
	private BusinessCase next;
	
	@Override
	@MemberOrder(sequence="7")
	public BusinessCase getNext() {
		return next;
	}
	
	public void setNext(BusinessCase next) {
		this.next = next;
	}
	
	// //////////////////////////////////////
	
	@Column(name="previousBusinessCaseId")
	@Persistent(mappedBy="next")
	private BusinessCase previous;
	
	@Override
	@MemberOrder(sequence="6")
	public BusinessCase getPrevious() {
		return previous;
	}
	
	// //////////////////////////////////////
	
	@Inject
	BusinessCases businesscases;

	@MemberOrder(sequence="9")
	@Override public ApplicationTenancy getApplicationTenancy() {
		return getProject().getApplicationTenancy();
	}
}