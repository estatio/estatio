package org.estatio.module.capex.imports;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.ExcelMetaDataEnabled;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.estatio.module.charge.imports.ChargeImport;

import lombok.Getter;
import lombok.Setter;

public class IncomingChargeImportAdapter implements FixtureAwareRowHandler<IncomingChargeImportAdapter>, ExcelMetaDataEnabled {

    public static final String ITA_WORKTYPE_PREFIX = "ITWT";

    public static final String ITA_OLD_WORKTYPE_PREFIX = "OLD";

    @Getter @Setter @Nullable
    private String excelSheetName;

    @Getter @Setter @Nullable
    private Integer excelRowNumber;

    @Getter @Setter @Nullable
    private String reference;

    @Getter @Setter @Nullable
    private String name;

    @Getter @Setter @Nullable
    private String description;

    @Getter @Setter @Nullable
    private String mappedTo;

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

    public IncomingChargeImportAdapter handle(final IncomingChargeImportAdapter previousRow){
        if (getReference()!=null) {
            ChargeImport line = new ChargeImport();
            serviceRegistry2.injectServicesInto(line);
            line.setReference(getReference());
            line.setName(getName());
            line.setDescription(getDescription());
            line.setApplicability("INCOMING");
            line.setAtPath("/ITA");
            line.setChargeGroupReference("I");
            line.setChargeGroupName("Incoming");
            line.setExternalReference(getMappedTo());
            line.importData(null);
        }
        return this;
    }

    @Override
    public void handleRow(final IncomingChargeImportAdapter previousRow) {

            if(executionContext != null && excelFixture2 != null) {
                if (executionContext.getParameterAsBoolean("testMode")!=null && executionContext.getParameterAsBoolean("testMode")){
                    executionContext.addResult(excelFixture2, this.handle(previousRow));
                } else {
                    this.handle(previousRow);
                }
            }

    }

    @Inject ServiceRegistry2 serviceRegistry2;

}

