package org.estatio.module.lease.fixtures.amortisation.enums;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.amortisation.AmortisationSchedule;
import org.estatio.module.lease.dom.amortisation.AmortisationScheduleRepository;
import org.estatio.module.lease.fixtures.amortisation.builders.AmortisationScheduleBuilder;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Accessors(chain = true)
public enum AmortisationSchedule_enum implements PersonaWithFinder<AmortisationSchedule>, PersonaWithBuilderScript<AmortisationSchedule, AmortisationScheduleBuilder> {

    OxfTopModel(Lease_enum.OxfTopModel001Gb, Charge_enum.GbDiscount, new BigDecimal("36000.00"), new LocalDate(2020,8,1), BigInteger.valueOf(1)),
    OxfMiracl(Lease_enum.OxfMiracl005Gb, Charge_enum.GbDiscount, new BigDecimal("18000.00"), new LocalDate(2020,9,2), BigInteger.valueOf(1));

    private final Lease_enum lease_d;

    private final Charge_enum charge_d;

    private BigDecimal scheduledValue;

    private LocalDate startDate;

    private BigInteger sequence;

    @Override
    public AmortisationSchedule findUsing(final ServiceRegistry2 serviceRegistry) {

        final Lease lease = lease_d.findUsing(serviceRegistry);
        final Charge charge = charge_d.findUsing(serviceRegistry);

        final AmortisationScheduleRepository repository = serviceRegistry.lookupService(
                AmortisationScheduleRepository.class);
        return repository.findUnique(lease, charge, startDate, sequence );
    }

    @Override
    public AmortisationScheduleBuilder builder() {
        return new AmortisationScheduleBuilder()
                .setPrereq((f,ec) -> f.setLease(f.objectFor(lease_d, ec)))
                .setPrereq((f,ec) -> f.setCharge(f.objectFor(charge_d, ec)))
                .setScheduledValue(scheduledValue)
                .setStartDate(startDate)
                .setSequence(sequence)
                ;
    }
}
