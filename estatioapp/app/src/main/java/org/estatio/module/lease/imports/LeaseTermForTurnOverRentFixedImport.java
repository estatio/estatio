package org.estatio.module.lease.imports;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.message.MessageService;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.estatio.module.base.dom.Importable;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForFixed;
import org.estatio.module.lease.dom.LeaseTermRepository;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.LeaseTermForTurnOverRentFixedImport"
)
public class LeaseTermForTurnOverRentFixedImport implements ExcelFixtureRowHandler, Importable {

    private static final Logger LOG = LoggerFactory.getLogger(LeaseTermForTurnOverRentFixedImport.class);

    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String leaseExternalReference;

    @Getter @Setter
    private LocalDate startDatePrevious;

    @Getter @Setter
    private BigDecimal valuePrevious;

    @Getter @Setter
    private LocalDate startDateCurrent;

    @Getter @Setter
    private BigDecimal valueCurrent;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter @Setter
    private BigDecimal value;

    @Programmatic
    @Override
    public List<Object> handleRow(FixtureScript.ExecutionContext executionContext, ExcelFixture excelFixture, Object o) {
        return importData(null);
    }

    public List<Object> importData() {
        return importData(null);
    }

    @Programmatic
    @Override
    public List<Object> importData(Object previousRow) {
        Lease leaseToUpdate = leaseRepository.findLeaseByReference(getLeaseReference());
        if (leaseToUpdate == null) {
            messageService.warnUser(String.format("Lease with reference %s not found", getLeaseReference()));
            return Lists.newArrayList();
        }
        LeaseItem itemToUpdate = leaseToUpdate.findFirstItemOfType(LeaseItemType.TURNOVER_RENT_FIXED);
        if (itemToUpdate == null) {
            messageService.warnUser(String.format("No lease item of type TURNOVER_RENT_FIXED found on lease with reference %s", getLeaseReference()));
            return Lists.newArrayList();
        }
        if (getValuePrevious()!=null && getStartDatePrevious() !=null) {
            updateOrCreateTerm(itemToUpdate, getStartDatePrevious(), getStartDateCurrent()!=null ? getStartDateCurrent().minusDays(1) : null, getValuePrevious());
        }
        if (getValueCurrent()!=null && getStartDateCurrent() !=null) {
            updateOrCreateTerm(itemToUpdate, getStartDateCurrent(), getStartDate()!=null ? getStartDate().minusDays(1) : null, getValueCurrent());
        }
        if (getValue()!=null && getStartDate() !=null) {
            updateOrCreateTerm(itemToUpdate, getStartDate(), getEndDate(), getValue());
        }

        return Lists.newArrayList();
    }

    void updateOrCreateTerm(final LeaseItem itemToUpdate, final LocalDate startDate, final LocalDate endDate, final BigDecimal value) {
        LeaseTermForFixed termToUpdate = (LeaseTermForFixed) leaseTermRepository.findByLeaseItemAndStartDate(itemToUpdate, startDate);
        if (termToUpdate!=null){
            termToUpdate.setValue(value);
            termToUpdate.setEndDate(endDate!=null ? endDate : termToUpdate.getEndDate());
        } else {
            LeaseTermForFixed newTerm = (LeaseTermForFixed) itemToUpdate.newTerm(startDate, endDate!=null ? endDate : startDate.plusYears(1).minusDays(1));
            newTerm.setValue(value);
        }
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    LeaseTermRepository leaseTermRepository;

    @Inject MessageService messageService;
}
