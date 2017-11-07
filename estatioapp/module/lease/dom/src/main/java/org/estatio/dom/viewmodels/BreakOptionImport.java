package org.estatio.dom.viewmodels;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;
import org.joda.time.Period;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.excel.dom.ExcelFixture;
import org.isisaddons.module.excel.dom.ExcelFixtureRowHandler;

import org.incode.module.base.dom.utils.JodaPeriodUtils;

import org.estatio.dom.Importable;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.dom.lease.breaks.BreakExerciseType;
import org.estatio.dom.lease.breaks.BreakOption;
import org.estatio.dom.lease.breaks.BreakOptionRepository;
import org.estatio.dom.lease.breaks.BreakType;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        nature = Nature.VIEW_MODEL,
        objectType = "org.estatio.dom.viewmodels.BreakOptionImport"
)
public class BreakOptionImport implements ExcelFixtureRowHandler, Importable {

    @Getter @Setter
    private String leaseReference;

    @Getter @Setter
    private String breakType;

    @Getter @Setter
    private String breakExcerciseType;

    @Getter @Setter
    private LocalDate breakDate;

    @Getter @Setter
    private LocalDate notificationDate;

    @Getter @Setter
    private String notificationPeriod;

    @Getter @Setter
    private String description;

//    @Override
//    public List<Class> importAfter() {
//        return Lists.newArrayList(LeaseImport.class);
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

        final Lease lease = fetchLease(leaseReference);
        final BreakType breakTypeValue = BreakType.valueOf(breakType);
        final BreakExerciseType breakExerciseTypeValue = BreakExerciseType.valueOf(breakExcerciseType);
        if (notificationDate != null) {
            final Period period = new Period(notificationDate, breakDate);
            notificationPeriod = JodaPeriodUtils.asSimpleString(period);
        }
        BreakOption br = breakOptionRepository.findByLeaseAndTypeAndBreakDateAndExerciseType(lease, breakTypeValue, breakDate, breakExerciseTypeValue);
        if (br == null) {
            breakOptionRepository.newBreakOption(lease, breakDate, notificationPeriod, breakTypeValue, breakExerciseTypeValue, description);
        }

        return Lists.newArrayList();
    }

    private Lease fetchLease(final String leaseReference) {
        final Lease lease;
        lease = leaseRepository.findLeaseByReference(leaseReference.trim().replaceAll("~", "+"));
        if (lease == null) {
            throw new ApplicationException(String.format("Lease with reference %s not found.", leaseReference));
        }
        return lease;
    }

    //region > injected services
    @Inject
    private BreakOptionRepository breakOptionRepository;

    @Inject
    private LeaseRepository leaseRepository;
    //endregion

}
