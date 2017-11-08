package org.estatio.capex.dom.invoice.inference;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRole;
import org.estatio.module.capex.dom.project.ProjectRoleRepository;
import org.estatio.module.capex.dom.project.ProjectRoleTypeEnum;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.role.PartyRoleMemberInferenceServiceAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleMemberInferenceServiceForProjectRoleAndIncomingInvoice
        extends PartyRoleMemberInferenceServiceAbstract<ProjectRoleTypeEnum, IncomingInvoice> {

    public PartyRoleMemberInferenceServiceForProjectRoleAndIncomingInvoice() {
        super(IncomingInvoice.class, ProjectRoleTypeEnum.class);
    }

    @Override
    protected List<Person> doInferMembersOf(
            final ProjectRoleTypeEnum roleType,
            final IncomingInvoice incomingInvoice) {

        final Project project = inferProject(incomingInvoice);
        if(project == null) {
            // can't go any further
            return null;
        }

        final List<ProjectRole> projectRoles =
                projectRoleRepository.findByProject(project);

        return currentPersonsFor(projectRoles);
    }

    @Override
    protected List<Person> doInferMembersOf(final ProjectRoleTypeEnum partyRoleType) {
        final List<ProjectRole> projectRoles =
                projectRoleRepository.listAll();

        return currentPersonsFor(projectRoles);
    }

    private List<Person> currentPersonsFor(final List<ProjectRole> projectRoles) {
        return projectRoles.stream()
                .filter(ProjectRole::isCurrent)
                .map(ProjectRole::getParty)
                .filter(Person.class::isInstance)
                .map(Person.class::cast)
                .collect(Collectors.toList());
    }

    private Project inferProject(final IncomingInvoice incomingInvoice) {

        final Collection<IncomingInvoiceItem> items =
                incomingInvoice.getItems()
                        .stream()
                        .filter(IncomingInvoiceItem.class::isInstance)
                        .map(IncomingInvoiceItem.class::cast)
                        .collect(Collectors.toList());

        for (IncomingInvoiceItem item : items) {
            Project project = item.getProject();
            if(project != null) {
                return project;
            }
        }
        return null;
    }


    @Inject
    ProjectRoleRepository projectRoleRepository;

}
