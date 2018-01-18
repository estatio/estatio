package org.estatio.module.lease.fixtures.breakoptions.enums;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.breaks.BreakExerciseType;
import org.estatio.module.lease.dom.breaks.BreakOption;
import org.estatio.module.lease.dom.breaks.BreakOptionRepository;
import org.estatio.module.lease.dom.breaks.BreakType;
import org.estatio.module.lease.fixtures.breakoptions.builders.BreakOptionBuilder;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.estatio.module.lease.dom.breaks.BreakExerciseType.*;
import static org.estatio.module.lease.dom.breaks.BreakType.*;
import static org.estatio.module.lease.fixtures.breakoptions.builders.BreakOptionBuilder.*;
import static org.estatio.module.lease.fixtures.breakoptions.builders.BreakOptionBuilder.SortOf.*;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum BreakOption_enum implements PersonaWithFinder<BreakOption>, PersonaWithBuilderScript<BreakOption, BreakOptionBuilder> {

    /*
    public static final String LEASE_REF = Lease_enum.OxfTopModel001Gb.getRef();

    @Override
    protected void execute(ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA.builder());
        executionContext.executeChild(this, LeaseItemForServiceChargeBudgeted_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForEntryFee_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForTax_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
        executionContext.executeChild(this, LeaseItemForMarketing_enum.OxfTopModel001Gb.builder());


        // exec
        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);
        newBreakOptionPlusYears(
                lease, 5, "6m", BreakType.FIXED, BreakExerciseType.MUTUAL, null, executionContext);
        newBreakOptionAtEndDate(
                lease, "6m", BreakType.ROLLING, BreakExerciseType.MUTUAL, null, executionContext);
    }

     */
    OxfMediaX002Gb_FIXED(
            Lease_enum.OxfMediaX002Gb, PLUS_YEARS, 5, "6m", FIXED, MUTUAL, null
    ),
    OxfMediaX002Gb_ROLLING(
            Lease_enum.OxfMediaX002Gb, END_DATE, null, "6m", ROLLING, MUTUAL, null
    ),
    OxfPoison003Gb_FIXED(
            Lease_enum.OxfPoison003Gb, PLUS_YEARS, 5, "6m", FIXED, MUTUAL, null
    ),
    OxfPoison003Gb_ROLLING(
            Lease_enum.OxfPoison003Gb, END_DATE, null, "6m", ROLLING, MUTUAL, null
    ),
    OxfTopModel001Gb_FIXED(
            Lease_enum.OxfTopModel001Gb, PLUS_YEARS, 5, "6m", FIXED, MUTUAL, null
    ),
    OxfTopModel001Gb_ROLLING(
            Lease_enum.OxfTopModel001Gb, END_DATE, null, "6m", ROLLING, MUTUAL, null
    ),
    ;

    private final Lease_enum lease_d;
    private final SortOf sortOf;
    private final Integer years;
    private final String notificationPeriodStr;
    private final BreakType breakType;
    private final BreakExerciseType exerciseType;
    private final String description;

    @Override
    public BreakOption findUsing(final ServiceRegistry2 serviceRegistry) {
        final Lease lease = lease_d.findUsing(serviceRegistry);
        final LocalDate breakDate = breakDateFor(lease, sortOf, years);

        final BreakOptionRepository repository = serviceRegistry.lookupService(BreakOptionRepository.class);
        return repository.findByLeaseAndTypeAndBreakDateAndExerciseType(lease, breakType, breakDate, exerciseType);
    }

    @Override
    public BreakOptionBuilder builder() {
        return new BreakOptionBuilder()
                .setPrereq((f,ec) -> f.setLease(f.objectFor(lease_d, ec)))
                .setSortOf(sortOf)
                .setYears(years)
                .setBreakType(breakType)
                .setExerciseType(exerciseType)
                .setNotificationPeriodStr(notificationPeriodStr)
                .setDescription(description);
    }


}
