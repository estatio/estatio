package org.estatio.dom.project;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

@Mixin(method = "exec")
public class Project_businessCase {

	private final Project project;

	public Project_businessCase(final Project project) {
		this.project = project;
	}

	@Action(semantics=SemanticsOf.SAFE)
	@ActionLayout(contributed=Contributed.AS_ASSOCIATION)
	public BusinessCase exec(){
		return businessCaseRepository.findActiveBusinessCaseOnProject(project);
	}
	
	@Inject
	BusinessCaseRepository businessCaseRepository;

}
