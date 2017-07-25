package org.estatio.capex.dom.coda;

import javax.inject.Inject;
import javax.jdo.annotations.Column;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ViewModel;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture2;
import org.isisaddons.module.excel.dom.FixtureAwareRowHandler;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.capex.dom.invoice.IncomingInvoiceType;
import org.estatio.capex.dom.util.PeriodUtil;
import org.estatio.dom.charge.Applicability;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@ViewModel
@NoArgsConstructor
public class CodaMappingImport implements FixtureAwareRowHandler<CodaMappingImport> {

    @Getter @Setter
    private String documentType;

    @Getter @Setter
    private String incomingInvoiceType;

    @Getter @Setter
    private String transactionType;

    @Getter @Setter
    private String atPath;

    @Getter @Setter
    private String chargeReference;

    @Getter @Setter
    private String chargeName;

    @Getter @Setter
    private String projectType;

    @Getter @Setter
    private String propertyOwnershipType;

    @Getter @Setter
    private String period;

    @Getter @Setter
    private String codaElementLevel;

    @Getter @Setter
    private String codaElementCode;

    @Getter @Setter
    private String codaElementName;

    @Getter @Setter
    private String comment;

    @Getter @Setter
    @Column(allowsNull = "true")
    private LocalDate startDate;

    @Getter @Setter
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
