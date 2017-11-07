package org.estatio.dom.viewmodels;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.dom.Importable;
import org.estatio.module.lease.dom.Fraction;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForDeposit;
import org.estatio.module.lease.dom.LeaseTermStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LeaseTermForDepositImport"
)
public class LeaseTermForDepositImport implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(LeaseTermForDepositImport.class);

    // leaseItem fields
    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String itemTypeName;

    @Getter @Setter
    private BigInteger itemSequence;

    @Getter @Setter
    private LocalDate itemStartDate;

    @Getter @Setter
    private LocalDate itemNextDueDate;

    @Getter @Setter
    private String itemChargeReference;

    @Getter @Setter
    private LocalDate itemEpochDate;

    @Getter @Setter
    private String itemInvoicingFrequency;

    @Getter @Setter
    private String itemPaymentMethod;

    @Getter @Setter
    private String itemStatus;

    @Getter @Setter
    private String itemAtPath;

    // generic term fields

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private String status;

    // deposit term fields
    @Getter @Setter
    private String fraction;

    @Getter @Setter
    private LocalDate fixedDepositCalculationDate;

    @Getter @Setter
    private boolean includeVat;

    @Getter @Setter
    private BigDecimal manualDepositValue;

    @Getter @Setter
    private Boolean useManualDepositValue;

    // source fields

    @Getter @Setter
    private String sourceItemTypeName;

    @Getter @Setter
    private BigInteger sourceItemSequence;

    @Getter @Setter
    private LocalDate sourceItemStartDate;


//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(LeaseItem.class, LeaseTermForIndexable.class);
//    }

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object previousRow) {
        return importData(previousRow);
    }

    // REVIEW: other import view models have @Action annotation here...  but in any case, is this view model actually ever surfaced in the UI?
    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(final Object previousRow) {

        // Early validation
        if (LeaseItemType.valueOf(itemTypeName) != LeaseItemType.DEPOSIT) {
            throw new IllegalArgumentException("Not a deposit type");
        }

        // Find or create deposit item
        final LeaseItemImport leaseItemImport = new LeaseItemImport(
                leaseReference,
                itemTypeName,
                itemSequence,
                itemStartDate,
                itemChargeReference,
                itemEpochDate,
                itemNextDueDate,
                itemInvoicingFrequency,
                itemPaymentMethod,
                itemStatus,
                itemAtPath);

        domainObjectContainer.injectServicesInto(leaseItemImport);
        LeaseItem depositItem = leaseItemImport.importItem(false);

        // link to source item if sourceItemTypeName is given
        if (sourceItemTypeName != null) {
            // Find source item and find or create source link
            final LeaseItemType sourceItemType = LeaseItemType.valueOf(sourceItemTypeName);
            LeaseItem rentItem = depositItem.getLease().findItem(sourceItemType, sourceItemStartDate, sourceItemSequence);

            depositItem.findOrCreateSourceItem(rentItem);
        }

        LeaseTermForDeposit term = (LeaseTermForDeposit) depositItem.findTermWithSequence(BigInteger.ONE);
        if (term == null) {
            // the start date of the term defaults to the start date of the item (which in turn defaults to the start date of the lease)
            term = (LeaseTermForDeposit) depositItem.newTerm(ObjectUtils.firstNonNull(startDate, depositItem.getStartDate()), endDate);
        }
        //set deposit term values
        term.setStatus(LeaseTermStatus.valueOf(status));
        // fraction defaults to Manual
        Fraction fractionOrDefault = fraction != null ? Fraction.valueOf(fraction) : Fraction.MANUAL;
        term.setFraction(fractionOrDefault);
        // when fraction is set to manual and fixed deposit calculation date is not given the startdate of the item is used
        if (fraction.equals("MANUAL") && fixedDepositCalculationDate == null) {
            term.setFixedDepositCalculationDate(term.getLeaseItem().getStartDate());
        } else {
            term.setFixedDepositCalculationDate(fixedDepositCalculationDate);
        }
        term.setIncludeVat(includeVat);
        if (useManualDepositValue != null && useManualDepositValue) {
            term.setManualDepositValue(manualDepositValue);
        }

        return Lists.newArrayList(term);
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    private DomainObjectContainer domainObjectContainer;

}
