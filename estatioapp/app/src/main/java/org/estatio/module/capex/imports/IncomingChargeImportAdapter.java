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

    public static final String ITA_INCOMING_CHARGE_PREFIX = "I";

    @Getter @Setter @Nullable
    private String excelSheetName;

    @Getter @Setter @Nullable
    private Integer excelRowNumber;

    @Getter @Setter @Nullable
    private Integer no;

    @Getter @Setter @Nullable
    private String descriptionIta;

    @Getter @Setter @Nullable
    private String descriptionEng;

    @Getter @Setter @Nullable
    private String comment;

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
        if (getNo()!=null) {
            ChargeImport line = new ChargeImport();
            serviceRegistry2.injectServicesInto(line);
            line.setReference(deriveChargeReference(getNo()));
            line.setName(deriveChargeName());
            line.setDescription(deriveDescription());
            line.setApplicability("INCOMING");
            line.setAtPath("/ITA");
            line.setChargeGroupReference("I");
            line.setChargeGroupName("Incoming");
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

    private String deriveChargeReference(final Integer input){
        return ITA_INCOMING_CHARGE_PREFIX.concat(input.toString());
    }

    private String deriveChargeName(){
        StringBuilder builder = new StringBuilder();
        builder
                .append(getNo().toString())
                .append(" ");
        if (getDescriptionEng()!=null && getDescriptionEng().length()>0){
            builder.append(clean(getDescriptionEng()));

        } else {
            builder.append(clean(getDescriptionIta()));
        }
        return limitLength(builder.toString(), 50);
    }

    private String deriveDescription(){
        StringBuilder builder = new StringBuilder();
        if (getDescriptionEng()!=null && getDescriptionEng().length()>0){
            builder.append(clean(getDescriptionEng()))
                    .append(" | ");

        }
        builder.append(clean(getDescriptionIta()));
        if (getComment()!=null){
            builder.append(" (")
                    .append(getComment())
                    .append(")");
        }
        return limitLength(builder.toString(), 254);
    }

    private String clean(final String input){
        if (input==null){
            return null;
        }
        String result = input.trim();
        return result.trim();
    }

    String limitLength(final String input, final int length) {
        if (input.length()<=length){
            return input;
        } else {
            return input.substring(0, length);
        }
    }

    @Inject ServiceRegistry2 serviceRegistry2;

}

