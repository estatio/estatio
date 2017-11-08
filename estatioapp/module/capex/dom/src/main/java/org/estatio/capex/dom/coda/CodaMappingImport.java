package org.estatio.capex.dom.coda;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.Column;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.module.capex.dom.util.PeriodUtil;
import org.estatio.module.charge.dom.Applicability;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@ViewModel
@NoArgsConstructor
public class CodaMappingImport implements FixtureAwareRowHandler<CodaMappingImport> {

    @Getter @Setter @Nullable
    private String documentType;

    @Getter @Setter @Nullable
    private String incomingInvoiceType;

    @Getter @Setter @Nullable
    private String transactionType;

    @Getter @Setter @Nullable
    private String atPath;

    @Getter @Setter @Nullable
    private String chargeReference;

    @Getter @Setter @Nullable
    private String chargeName;

    @Getter @Setter @Nullable
    private String projectType;

    @Getter @Setter @Nullable
    private String propertyOwnershipType;

    @Getter @Setter @Nullable
    private String period;

    @Getter @Setter @Nullable
    private String codaElementLevel;

    @Getter @Setter @Nullable
    private String codaElementCode;

    @Getter @Setter @Nullable
    private String codaElementName;

    @Getter @Setter @Nullable
    private String comment;

    @Getter @Setter @Nullable
    @Column(allowsNull = "true")
    private LocalDate startDate;

    @Getter @Setter @Nullable
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

    public CodaMappingImport(final CodaMapping codaMapping) {

        this.documentType = codaMapping.getDocumentType().name();
        this.incomingInvoiceType = codaMapping.getIncomingInvoiceType().name();
        this.transactionType = codaMapping.getCodaTransactionType().name();
        this.atPath = codaMapping.getAtPath();
        this.chargeReference = codaMapping.getCharge().getReference();
        this.chargeName = codaMapping.getCharge().getName();
        this.propertyOwnershipType = codaMapping.isPropertyIsFullyOwned() ? "FULL" : "PARTIAL";
        this.period = PeriodUtil.periodFromInterval(new LocalDateInterval(codaMapping.getStartDate(), codaMapping.getEndDate()));
        this.codaElementLevel = codaMapping.getCodaElement().getLevel().name();
        this.codaElementCode = codaMapping.getCodaElement().getCode();
        this.codaElementName = codaMapping.getCodaElement().getName();
        this.startDate = codaMapping.getStartDate();
        this.endDate = codaMapping.getEndDate();
    }

    @Override public void handleRow(final CodaMappingImport previousRow) {

        atPath = atPath == null && previousRow != null ? previousRow.atPath : atPath;

        if (codaElementName == null){
            String.format("");
        }

        if ((documentType != null || incomingInvoiceType != null) && chargeName != null) {
            IncomingInvoiceType incomingInvoiceTypeEnum = IncomingInvoiceType.valueOf(incomingInvoiceType);
            DocumentType documentTypeEnum = incomingInvoiceTypeEnum == null ? DocumentType.valueOf(documentType) : DocumentType.INVOICE_IN;
            CodaElementLevel codaElementLevelEnum = CodaElementLevel.valueOf(codaElementLevel);
            CodaElement codaElement = codaElementRepository.findOrCreate(codaElementLevelEnum, codaElementCode, codaElementName);
            Charge charge = chargeRepository.findOrCreate(atPath, chargeReference != null ? chargeReference : chargeNameToReference(chargeName), chargeName, "", Applicability.INCOMING);

            final LocalDateInterval interval = period == null ? new LocalDateInterval() : PeriodUtil.yearFromPeriod(period);

            final CodaTransactionType codaTransactionType = valueOfElseDefault(transactionType, CodaTransactionType.STAT);

            codaMappingRepository.findOrCreate(
                    atPath,
                    documentTypeEnum,
                    incomingInvoiceTypeEnum,
                    codaTransactionType,
                    charge,
                    propertyFullyOwned(propertyOwnershipType),
                    interval.startDate(),
                    interval.endDate(),
                    startDate,
                    endDate, codaElement);
        }
    }

    private static boolean propertyFullyOwned(final String propertyOwnershipType) {
        return propertyOwnershipType != null && propertyOwnershipType.equals("PARTIAL") ? false : true;
    }

    private static <E extends Enum<E>> E valueOfElseDefault(final String stringValue, final E defaultValue) {
        return stringValue == null ? (E) defaultValue :  Enum.valueOf(defaultValue.getDeclaringClass(), stringValue) ;
    }

    private String chargeNameToReference(final String chargeName) {
        return chargeName.toUpperCase();
    }

    @Inject
    ChargeRepository chargeRepository;

    @Inject
    CodaElementRepository codaElementRepository;

    @Inject
    CodaMappingRepository codaMappingRepository;


}
