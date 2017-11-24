package org.estatio.module.capex.dom.project;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.tax.dom.Tax;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = ProjectItem.class
)
public class ProjectItemRepository {

    @Programmatic
    public ProjectItem findByProjectAndCharge(final Project project, final Charge charge) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        ProjectItem.class,
                        "findByProjectAndCharge",
                        "project", project,
                        "charge", charge
                ));
    }


    @Programmatic
    public ProjectItem create(
            final Project project,
            final Charge charge,
            final String description,
            final BigDecimal budgetedAmount,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Tax tax) {
        final ProjectItem projectItem = repositoryService.instantiate(ProjectItem.class);
        projectItem.setProject(project);
        projectItem.setCharge(charge);
        projectItem.setDescription(description);
        projectItem.setBudgetedAmount(budgetedAmount);
        projectItem.setStartDate(startDate);
        projectItem.setEndDate(endDate);
        projectItem.setProperty(property);
        projectItem.setTax(tax);
        repositoryService.persistAndFlush(projectItem);
        return projectItem;
    }

    @Programmatic
    public ProjectItem findOrCreate(
            final Project project,
            final Charge charge,
            final String description,
            final BigDecimal budgetedAmount,
            final LocalDate startDate,
            final LocalDate endDate,
            final Property property,
            final Tax tax
    ) {
        ProjectItem projectItem = findByProjectAndCharge(project, charge);
        if (projectItem == null) {
            projectItem = create(project, charge, description, budgetedAmount, startDate, endDate,
                    property, tax);
        }
        return projectItem;
    }


    @Inject
    RepositoryService repositoryService;
}
