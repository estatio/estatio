package org.estatio.module.capex.imports;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelMetaDataEnabled;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

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

    public ProjectImportAdapter handle(final ProjectImportAdapter previousRow){
        if (getNoCommessa()!=null) {
            ProjectImport line = new ProjectImport();
            serviceRegistry2.injectServicesInto(line);
            line.setProjectReference(deriveProjectReference(getNoCommessa(), getCentroDiCosto()));
            line.setProjectName(deriveProjectName(getCausale()));
            line.setAtPath("/ITA");
            if (getArchived()!=null && getArchived().equals("YES")){
                line.setArchived(true);
            }
            line.importData(null);
        }
        return this;
    }

    @Override
    public void handleRow(final ProjectImportAdapter previousRow) {

        if(executionContext != null && excelFixture2 != null) {
            if (executionContext.getParameterAsBoolean("testMode")!=null && executionContext.getParameterAsBoolean("testMode")){
                executionContext.addResult(excelFixture2, this.handle(previousRow));
            } else {
                this.handle(previousRow);
            }
        }

    }

    public static String deriveProjectReference(final String noCommessa, final String centroDiCosto ){
        String noCommessaToReference = ITA_PROJECT_PREFIX.concat(noCommessa);
        return handleDoubles(noCommessaToReference, centroDiCosto);
    }

    public static String deriveProjectReference(final Integer noCommessa, final String centroDiCosto ){
        return deriveProjectReference(noCommessa.toString(), centroDiCosto);
    }

    public static String handleDoubles(final String possibleDoubleReference, final String centroDiCosto) {
        List<String> doubles =
                Lists.newArrayList(
                        "ITPR154",
                        "ITPR184",
                        "ITPR190",
                        "ITPR192");
        return doubles.contains(possibleDoubleReference) ?
                possibleDoubleReference + " [DOUBLE] " + centroDiCosto
                : possibleDoubleReference;
    }

    private String deriveProjectName(final String input){
        StringBuilder builder = new StringBuilder();
        builder
                .append(ITA_PROJECT_PREFIX)
                .append(getNoCommessa().toString())
                .append(" ")
                .append(getCentroDiCosto());
        if (getCausale()!=null){
            builder
                    .append(" - ")
                    .append(clean(getCausale()));
        }
        return limitLength(builder.toString(), 50);
    }

    private String clean(final String input){
        if (input==null){
            return null;
        }
        String result = input.trim();
        return result.trim();
    }

    String limitLength(final String input, final int length) {
        if (input==null) return input;
        if (input.length()<=length){
            return input;
        } else {
            return input.substring(0, length);
        }
    }

    @Inject ServiceRegistry2 serviceRegistry2;

}

