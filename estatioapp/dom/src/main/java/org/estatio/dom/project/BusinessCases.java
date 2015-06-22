package org.estatio.dom.project;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.estatio.dom.UdoDomainRepositoryAndFactory;

@DomainService(repositoryFor = BusinessCase.class, nature=NatureOfService.DOMAIN)
public class BusinessCases extends UdoDomainRepositoryAndFactory<BusinessCase> {

	public BusinessCases(){
			super(BusinessCases.class, BusinessCase.class);
	}
	
	@Programmatic
	public BusinessCase newBusinessCase(
			final Project project,
			final String businessCaseDescription,
			final LocalDate reviewDate,
			final LocalDate date,
			final LocalDate lastUpdated,
			final Integer businessCaseVersion
			){
		// Create businesscase instance
		BusinessCase businesscase = newTransientInstance(BusinessCase.class);
		
		// Set values
		businesscase.setDescription(businessCaseDescription);
		businesscase.setDate(date);
		businesscase.setNextReviewDate(reviewDate);
		businesscase.setLastUpdated(lastUpdated);
		businesscase.setProject(project);
		businesscase.setBusinessCaseVersion(businessCaseVersion);
		// Persist it
		persistIfNotAlready(businesscase);
		
		return businesscase;
	}
	
	@Programmatic
	public BusinessCase newBusinessCase(
			final Project project,
			final String businessCaseDescription,
			final LocalDate reviewDate,
			final LocalDate date,
			final LocalDate lastUpdated,
			final Integer businessCaseVersion,
			final BusinessCase next
	){
		// Create businesscase instance
		BusinessCase businesscase = newTransientInstance(BusinessCase.class);
		
		// Set values
		businesscase.setDescription(businessCaseDescription);
		businesscase.setDate(date);
		businesscase.setNextReviewDate(reviewDate);
		businesscase.setLastUpdated(lastUpdated);
		businesscase.setProject(project);
		businesscase.setBusinessCaseVersion(businessCaseVersion);
		businesscase.setNext(next);
		// Persist it
		persistIfNotAlready(businesscase);
		
		return businesscase;
	}
	
	@Programmatic
	public List<BusinessCase> businessCaseHistory(final Project project){
		return allMatches("findByProject", "project", project);
	}
	
	@Programmatic
	public BusinessCase findActiveBusinessCaseOnProject(final Project project){
		return uniqueMatch("findActiveBusinessCaseOnProject", "project", project);
	}
	
	@Programmatic
	public BusinessCase previousVersion(final BusinessCase businessCase){
		return uniqueMatch("findByProjectAndVersion", "project", businessCase.getProject(), "businessCaseVersion", businessCase.getBusinessCaseVersion() - 1);
	}

	@Programmatic
	public BusinessCase nextVersion(final BusinessCase businessCase){
		return uniqueMatch("findByProjectAndVersion", "project", businessCase.getProject(), "businessCaseVersion", businessCase.getBusinessCaseVersion() + 1);
	}

}
