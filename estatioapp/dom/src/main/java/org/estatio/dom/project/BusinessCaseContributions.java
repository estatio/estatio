package org.estatio.dom.project;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

@DomainService(nature=NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class BusinessCaseContributions  {

	@Action(semantics=SemanticsOf.NON_IDEMPOTENT)
	@ActionLayout(contributed=Contributed.AS_ACTION)
    @MemberOrder(sequence = "1")
	public BusinessCase newBusinessCase(
			final Project project,
			@ParameterLayout(
					named = "Description",
					multiLine = 5
					)
			final String businessCaseDescription,
			@ParameterLayout(named = "Next review date")
			final LocalDate reviewDate
			){
		
		new LocalDate();
		final LocalDate now = LocalDate.now();
		return businesscases.newBusinessCase(project, businessCaseDescription, reviewDate, now, null, 1);
	}
	
	public boolean hideNewBusinessCase(final Project project, final String businessCaseDescription, final LocalDate reviewDate){

		if (!businesscases.businessCaseHistory(project).isEmpty()){
			return true;
		}
		
		return false;
	}
	
	public String validateNewBusinessCase(final Project project, final String businessCaseDescription, final LocalDate reviewDate){

		if (!businesscases.businessCaseHistory(project).isEmpty()){
			return "This project has a business case already; use update business case instead";
		}
		
		new LocalDate();
		LocalDate now = LocalDate.now();
		if (reviewDate.isBefore(now)) {
			return "A review date should not be in the past";
		}
		
		return null;
	}
	
	@Action(semantics=SemanticsOf.SAFE)
	@ActionLayout(contributed=Contributed.AS_ASSOCIATION)
	public BusinessCase businessCase(final Project project){
		return businesscases.findActiveBusinessCaseOnProject(project);
	}
	
	@Inject 
	BusinessCases businesscases;

}
