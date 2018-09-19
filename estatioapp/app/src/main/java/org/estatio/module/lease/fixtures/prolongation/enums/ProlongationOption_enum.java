package org.estatio.module.lease.fixtures.prolongation.enums;

import java.util.List;

import com.google.common.base.Objects;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.breaks.prolongation.ProlongationOption;
import org.estatio.module.lease.dom.breaks.prolongation.ProlongationOptionRepository;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForEntryFee_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForMarketing_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTax_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.fixtures.prolongation.builders.ProlongationOptionBuilder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum ProlongationOption_enum implements PersonaWithFinder<ProlongationOption>, PersonaWithBuilderScript<ProlongationOption, ProlongationOptionBuilder> {

    OxfTopModel001(
            Lease_enum.OxfTopModel001Gb, "5y", "6m",
            // prereqs
            new PersonaWithBuilderScript[] {
                    LeaseItemForRent_enum.OxfTopModel001Gb,
                    LeaseItemForServiceCharge_enum.OxfTopModel001Gb,
                    LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA,
                    LeaseItemForTurnoverRent_enum.OxfTopModel001Gb,
                    LeaseItemForDiscount_enum.OxfTopModel001Gb,
                    LeaseItemForEntryFee_enum.OxfTopModel001Gb,
                    LeaseItemForTax_enum.OxfTopModel001Gb,
                    LeaseItemForDeposit_enum.OxfTopModel001Gb,
                    LeaseItemForMarketing_enum.OxfTopModel001Gb,
            }
    ),
    ;

    private final Lease_enum lease_d;
    private final String prolongationPeriod;
    private final String notificationPeriod;

    private final PersonaWithBuilderScript<?,?>[] prereqs;

    @Override
    public ProlongationOption findUsing(final ServiceRegistry2 serviceRegistry) {
        final ProlongationOptionRepository repository = serviceRegistry.lookupService(ProlongationOptionRepository.class);
        final Lease lease = lease_d.findUsing(serviceRegistry);
        final List<ProlongationOption> options = repository.findByLease(lease);
        return options.
                stream().filter(x ->
                    Objects.equal(x.getProlongationPeriod(), prolongationPeriod)
                )
                .findFirst()
                .get(); // fail fast if not found
    }

    @Override
    public ProlongationOptionBuilder builder() {
        return new ProlongationOptionBuilder()
                .setPrereq((f,ec) -> {
                    // simply make sure these objects exist
                    for (PersonaWithBuilderScript<?, ?> prereq : prereqs) {
                        f.objectFor(prereq, ec);
                    }
                })
                .setPrereq((f,ec) -> f.setLease(f.objectFor(lease_d, ec)))
                .setProlongationPeriod(prolongationPeriod)
                .setNotificationPeriod(notificationPeriod)
                ;
    }


}
