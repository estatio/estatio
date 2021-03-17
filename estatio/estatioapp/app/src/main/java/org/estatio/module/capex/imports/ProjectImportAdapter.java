package org.estatio.module.capex.imports;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelMetaDataEnabled;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.estatio.module.capex.dom.project.Project;
import org.estatio.module.capex.dom.project.ProjectRepository;

import lombok.Getter;
import lombok.Setter;

public class ProjectImportAdapter implements FixtureAwareRowHandler<ProjectImportAdapter>, ExcelMetaDataEnabled {

    public static final String ITA_PROJECT_PREFIX = "ITPR";

    @Getter @Setter @Nullable
    private String excelSheetName;

    @Getter @Setter @Nullable
    private Integer excelRowNumber;

    @Getter @Setter @Nullable
    private String archived;

    @Getter @Setter @Nullable
    private Integer noCommessa;

    @Getter @Setter @Nullable
    private String centroDiCosto;

    @Getter @Setter @Nullable
    private String cc;

    @Getter @Setter @Nullable
    private String causale;

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

    public ProjectImportAdapter handle(final ProjectImportAdapter previousRow) {
        if (getNoCommessa() != null) {
            ProjectImport line = new ProjectImport();
            serviceRegistry2.injectServicesInto(line);
            line.setProjectReference(deriveProjectReference(getNoCommessa(), getCentroDiCosto()));
            line.setProjectName(deriveProjectName(getCausale()));
            line.setAtPath("/ITA");
            if (getArchived() != null && getArchived().equals("YES")) {
                line.setArchived(true);
            }

            Project newProject = (Project) line.importData(null).get(0);

            if (previousRow != null && previousRow.getNoCommessa().equals(getNoCommessa())) {
                newProject.setName(deriveProjectName(getCausale()));
            }
        }
        return this;
    }

    @Override
    public void handleRow(final ProjectImportAdapter previousRow) {

        if (executionContext != null && excelFixture2 != null) {
            if (executionContext.getParameterAsBoolean("testMode") != null && executionContext.getParameterAsBoolean("testMode")) {
                executionContext.addResult(excelFixture2, this.handle(previousRow));
            } else {
                this.handle(previousRow);
            }
        }

    }

    public static String deriveProjectReference(final String noCommessa) {
        return noCommessa.length() == 3 ? ITA_PROJECT_PREFIX.concat(noCommessa) : ITA_PROJECT_PREFIX.concat("0" + noCommessa);
    }

    public static String deriveProjectReference(final Integer noCommessa, final String centroDiCosto) {
        return deriveProjectReference(noCommessa.toString());
    }

    private String deriveProjectName(final String input) {
        final Project projectIfAny = projectRepository.findByReference(deriveProjectReference(getNoCommessa().toString()));
        StringBuilder builder = new StringBuilder()
                .append(deriveProjectReference(getNoCommessa().toString()));

        if (projectIfAny == null) {
            builder
                    .append(" ")
                    .append(getCentroDiCosto());
        }
        if (getCausale() != null) {
            builder
                    .append(" - ")
                    .append(clean(getCausale()));
        }
        return limitLength(builder.toString(), 50);
    }

    private String clean(final String input) {
        if (input == null) {
            return null;
        }
        String result = input.trim();
        return result.trim();
    }

    String limitLength(final String input, final int length) {
        if (input == null)
            return input;
        if (input.length() <= length) {
            return input;
        } else {
            return input.substring(0, length);
        }
    }

    @Inject ServiceRegistry2 serviceRegistry2;

    @Inject ProjectRepository projectRepository;

}

