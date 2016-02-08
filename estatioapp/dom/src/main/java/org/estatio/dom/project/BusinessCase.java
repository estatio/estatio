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

import lombok.Getter;
import lombok.Setter;

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

	@Column(allowsNull = "false")
    @PropertyLayout(multiLine = 5, describedAs = "Reason for the project and expected benefits")
	@MemberOrder(sequence="1")
	@Getter @Setter
	private String description;

	// //////////////////////////////////////

	@Column(name= "projectId", allowsNull = "false")
	@MemberOrder(sequence="8")
	@Getter @Setter
	private Project project;

	// //////////////////////////////////////

	@Column(allowsNull = "false")
	@MemberOrder(sequence="3")
	@Getter @Setter
	private LocalDate date;

	// //////////////////////////////////////

	@Column(allowsNull = "true")
	@MemberOrder(sequence="5")
	@Getter @Setter
	private LocalDate lastUpdated;

	// //////////////////////////////////////

	@Column(allowsNull = "true")
	@MemberOrder(sequence="4")
	@Getter @Setter
	private LocalDate nextReviewDate;

	// //////////////////////////////////////

	@Column(allowsNull = "false")
	@MemberOrder(sequence="2")
	@Getter @Setter
	private Integer businessCaseVersion;

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
		
		LocalDate now = LocalDate.now();
		if (reviewDate.isBefore(now)) {
			return "A review date should not be in the past";
		}
		
		return null;
	}
	
	// //////////////////////////////////////

	@MemberOrder(sequence="7")
	@Column(name="nextBusinessCaseId")
	@Getter @Setter
	private BusinessCase next;

	// //////////////////////////////////////
	
//	@Column(name="previousBusinessCaseId")
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