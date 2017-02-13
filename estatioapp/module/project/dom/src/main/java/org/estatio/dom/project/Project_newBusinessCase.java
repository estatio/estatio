package org.estatio.dom.project;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.clock.ClockService;

@Mixin(method="exec")
public class Project_newBusinessCase {

	private final Project project;

	public Project_newBusinessCase(final Project project) {
		this.project = project;
	}

	@Action(semantics=SemanticsOf.NON_IDEMPOTENT)
	@ActionLayout(contributed=Contributed.AS_ACTION)
    @MemberOrder(sequence = "1")
	public BusinessCase exec(
			@ParameterLayout(multiLine = BusinessCase.DescriptionType.Meta.MULTI_LINE)
			final String description,
			final LocalDate nextReviewDate
		){
		
		final LocalDate now = clockService.now();
		return businessCaseRepository.newBusinessCase(project, description, nextReviewDate, now, null, 1);
	}
	
	public boolean hideExec(){
		return !businessCaseRepository.businessCaseHistory(project).isEmpty();
	}
	
	public String validateExec(final String businessCaseDescription, final LocalDate reviewDate){

		if (!businessCaseRepository.businessCaseHistory(project).isEmpty()){
			return "This project has a business case already; use update business case instead";
		}
		
		LocalDate now = clockService.now();
		if (reviewDate.isBefore(now)) {
			return "A review date should not be in the past";
		}
		
		return null;
	}
	

	@Inject
	ClockService clockService;

	@Inject
	BusinessCaseRepository businessCaseRepository;

}
