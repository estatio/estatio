package org.estatio.capex.dom.coda;

import javax.inject.Inject;
import javax.jdo.annotations.Column;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.charge.Applicability;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;

import lombok.Getter;
import lombok.Setter;


@ViewModel
@Setter @Getter
public class CodaMappingImport implements FixtureAwareRowHandler<CodaMappingImport> {

    private String documentType;

    private String transactionType;

    private String atPath;

    private String chargeReference;

    private String chargeName;

    private String hasProperty;

    private String hasProject;

    private String projectType;

    private String hasBudget;

    private String propertyIsFullyOwned;

    private String period;

    private String el5Code;

    private String el5Name;

    private String comment;


    @Column(allowsNull = "false", length = 12)
    private CodaElementLevel codaElementLevel;

    @Column(allowsNull = "false", length = 50, name = "codaElementId")
    private CodaElement codaElement;

    @Column(allowsNull = "true")
    private LocalDate startDate;

    @Column(allowsNull = "true")
    private LocalDate endDate;


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

    @Override public void handleRow(final CodaMappingImport previousRow) {

        atPath = atPath == null ? previousRow.atPath : atPath;

        if (documentType != null && chargeName != null) {
            DocumentType documentTypeEnum = DocumentType.valueOf(documentType);

            CodaElement codaElement = codaElementRepository.findOrCreate(CodaElementLevel.LEVEL_5, el5Code, el5Name);
            Charge charge = chargeRepository.findOrCreate(atPath, chargeReference != null ? chargeReference : chargeNameToReference(chargeName), chargeName, "", Applicability.INCOMING);

            final LocalDateInterval interval = period == null ? new LocalDateInterval() : PeriodUtil.yearFromPeriod(period);

            final CodaTransactionType codaTransactionType = valueOfElseDefault(transactionType, CodaTransactionType.STAT);

            final CodaMappingFilter hasProject = valueOfElseDefault(this.hasProject, CodaMappingFilter.AMBIGUOUS);
            final CodaMappingFilter hasProperty = valueOfElseDefault(this.hasProperty, CodaMappingFilter.AMBIGUOUS);
            final CodaMappingFilter hasBudget = valueOfElseDefault(this.hasBudget, CodaMappingFilter.AMBIGUOUS);

            codaMappingRepository.findOrCreate(
                    atPath,
                    documentTypeEnum,
                    codaTransactionType,
                    charge,
                    hasProject,
                    hasProperty,
                    hasBudget,
                    asBoolean(propertyIsFullyOwned, true),
                    interval.startDate(),
                    interval.endDate(),
                    startDate,
                    endDate,
                    codaElement);
        }
    }


    private boolean asBoolean(final String stringValue, final boolean defaultValue) {
        return stringValue == null ? defaultValue : parseBoolean2(stringValue);
    }

    private static boolean parseBoolean2(final String stringValue) {
        return stringValue == "YES" ? true : false;
    }

    private static <E extends Enum<E>> E valueOfElseDefault(final String stringValue, final E defaultValue) {
        return stringValue == null ? (E) defaultValue :  Enum.valueOf(defaultValue.getDeclaringClass(), stringValue) ;
    }

    private String chargeNameToReference(final String chargeName) {
        return chargeName.toUpperCase();
    }

    @Inject ChargeRepository chargeRepository;

    @Inject CodaElementRepository codaElementRepository;

    @Inject CodaMappingRepository codaMappingRepository;


}
