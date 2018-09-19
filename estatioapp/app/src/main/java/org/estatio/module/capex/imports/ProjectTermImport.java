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
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.module.capex.imports.ProjectTermImport"
)
public class ProjectTermImport implements Importable, ExcelFixtureRowHandler {

    @Getter @Setter
    private String projectReference;

    @Getter @Setter
    private BigDecimal budgetedAmount;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;


    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        if (previousRow!=null){
            // TODO: support sparse sheets ?
        }

        Project project = projectRepository.findByReference(getProjectReference());
        if (project == null) {
            throw new ApplicationException(String.format("Project with reference %s not found.", getProjectReference()));
        }

        wrapperFactory.wrap(project).newProjectTerm(getBudgetedAmount(), getStartDate(), getEndDate());
        return Lists.newArrayList(project);

    }

    @Override
    public List<Object> handleRow(final FixtureScript.ExecutionContext executionContext, final ExcelFixture excelFixture, final Object previousRow) {
        return importData(previousRow);
    }

    @Inject ProjectRepository projectRepository;

    @Inject WrapperFactory wrapperFactory;
}
