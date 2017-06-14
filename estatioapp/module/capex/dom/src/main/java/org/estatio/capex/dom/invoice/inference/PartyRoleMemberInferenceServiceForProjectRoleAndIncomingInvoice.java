package org.estatio.capex.dom.invoice.inference;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.capex.dom.project.Project;
import org.estatio.capex.dom.project.ProjectRole;
import org.estatio.capex.dom.project.ProjectRoleRepository;
import org.estatio.capex.dom.project.ProjectRoleTypeEnum;
import org.estatio.dom.party.Person;
import org.estatio.dom.party.role.PartyRoleMemberInferenceServiceAbstract;

@DomainService(nature = NatureOfService.DOMAIN)
public class PartyRoleMemberInferenceServiceForProjectRoleAndIncomingInvoice
        extends PartyRoleMemberInferenceServiceAbstract<ProjectRoleTypeEnum, IncomingInvoice> {

    public PartyRoleMemberInferenceServiceForProjectRoleAndIncomingInvoice() {
        super(IncomingInvoice.class,
                ProjectRoleTypeEnum.PROJECT_MANAGER
        );
    }

    @Override
    protected List<Person> doInfer(
            final ProjectRoleTypeEnum roleType,
            final IncomingInvoice incomingInvoice) {

        final Project project = inferProject(incomingInvoice);
        if(project == null) {
            // can't go any further
            return null;
        }

        final List<ProjectRole> projectRoles =
                projectRoleRepository.findByProject(project);

        return projectRoles.stream()
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
