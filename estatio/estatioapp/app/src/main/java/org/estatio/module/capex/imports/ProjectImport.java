package org.estatio.module.capex.imports;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.WrapperFactory;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.PropertyRepository;
import org.estatio.module.base.dom.Importable;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectItemRepository;
import org.estatio.module.capex.dom.project.ProjectRepository;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.imports.ProjectImport"
)
public class ProjectImport implements Importable, ExcelFixtureRowHandler, FixtureAwareRowHandler<ProjectImport> {

    @Getter @Setter
    private String projectReference;

    @Getter @Setter
    private String projectName;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String parentReference;

    @Getter @Setter
    private boolean archived;

    @Getter @Setter
    private String itemWorkTypeReference;

    @Getter @Setter
    private String itemDescription;

    @Getter @Setter
    private BigDecimal itemBudgetedAmount;

    @Getter @Setter
    private LocalDate itemStartDate;

    @Getter @Setter
    private LocalDate itemEndDate;

    @Getter @Setter
    private String itemPropertyReference;

    @Getter @Setter
    private String itemTaxReference;

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        if (previousRow != null) {
            // TODO: support sparse sheets ?
        }
        Project parent = null;
        if (getParentReference() != null) {
            parent = projectRepository.findByReference(getParentReference());
            if (parent == null) {
                throw new ApplicationException(String.format("Parent with reference %s not found.", getParentReference()));
            }
            if (!parent.getAtPath().equals(getAtPath())) {
                throw new ApplicationException(String.format("AtPath parent %s does not match %s.", getParentReference(), getAtPath()));
            }
            if (!parent.getItems().isEmpty()) {
                // TODO: (ECP-438) until we find out more about the process, prevent a the choice of a project having items
                throw new ApplicationException(String.format("Parent with reference %s has items and cannot be a parent therefore.", getAtPath()));
            }
        }
        Project project = findOrCreateProjectAndUpdateParent(getProjectReference(), getProjectName(), getStartDate(), getEndDate(), getAtPath(), parent);
        if (isArchived()) {
            project.archive();
        }

        if (getItemWorkTypeReference() != null) {
            Charge charge = chargeRepository.findByReference(getItemWorkTypeReference());
            Property property = propertyRepository.findPropertyByReference(getItemPropertyReference());
            Tax tax = taxRepository.findByReference(getItemTaxReference());

            projectItemRepository.upsert(project, charge, getItemDescription(), getItemBudgetedAmount(), getItemStartDate(), getItemEndDate(), property, tax);
        }
        return Lists.newArrayList(project);
    }

    Project findOrCreateProjectAndUpdateParent(final String reference, final String name, final LocalDate startDate, final LocalDate endDate, final String atPath, final Project parent) {
        Project project = projectRepository.findOrCreate(reference, name, startDate, endDate, atPath, parent);
        if (parent != null)
            project.setParent(parent);
        return project;
    }

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        return importData(previousRow);
    }

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private FixtureScript.ExecutionContext executionContext;

    /**
     * To allow for usage within fixture scripts also.
     */
    @Setter
    private ExcelFixture2 excelFixture2;

    @Override
    public void handleRow(final ProjectImport previousRow) {

        if (executionContext != null && excelFixture2 != null) {
            if (executionContext.getParameterAsBoolean("testMode") != null && executionContext.getParameterAsBoolean("testMode")) {
                executionContext.addResult(excelFixture2, this.importData(previousRow));
            } else {
                this.importData(previousRow);
            }
        }

    }

    @Inject ProjectRepository projectRepository;

    @Inject ProjectItemRepository projectItemRepository;

    @Inject ChargeRepository chargeRepository;

    @Inject PropertyRepository propertyRepository;

    @Inject TaxRepository taxRepository;

    @Inject WrapperFactory wrapperFactory;
}
