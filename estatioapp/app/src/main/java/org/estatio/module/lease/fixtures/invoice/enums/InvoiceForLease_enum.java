package org.estatio.module.lease.fixtures.invoice.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Objects;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.PersonaWithBuilderScript;
import org.apache.isis.applib.fixturescripts.PersonaWithFinder;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.core.commons.ensure.Ensure;

import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.currency.fixtures.enums.Currency_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.fixtures.invoice.builders.InvoiceForLeaseBuilder;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.party.fixtures.organisation.enums.Organisation_enum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import static org.hamcrest.CoreMatchers.is;
import static org.incode.module.base.integtests.VT.ldix;

@AllArgsConstructor()
@Getter
@Accessors(chain = true)
public enum InvoiceForLease_enum implements PersonaWithFinder<InvoiceForLease>, PersonaWithBuilderScript<InvoiceForLease, InvoiceForLeaseBuilder> {

    OxfMiracl005Gb(
            ApplicationTenancy_enum.GbOxf, Lease_enum.OxfMiracl005Gb,
            Organisation_enum.HelloWorldGb, Organisation_enum.MiracleGb,
            PaymentMethod.DIRECT_DEBIT, Currency_enum.EUR,
            new ItemsSpec[] {
                new ItemsSpec(LeaseItemType.RENT_DISCOUNT_FIXED, 3)
            },
            // prereqs
            new PersonaWithBuilderScript[] {
                    LeaseItemForDiscount_enum.OxfMiracle005bGb
            }
    ),
    KalPoison001Nl(
            ApplicationTenancy_enum.NlKal, Lease_enum.KalPoison001Nl,
            Organisation_enum.AcmeNl, Organisation_enum.PoisonNl,
            PaymentMethod.DIRECT_DEBIT, Currency_enum.EUR,
            new ItemsSpec[] {
                new ItemsSpec(LeaseItemType.RENT, 3)
            },
            // prereqs
            new PersonaWithBuilderScript[] {
                    LeaseItemForRent_enum.KalPoison001Nl
            }
    ),
    OxfPoison003Gb(
            ApplicationTenancy_enum.GbOxf, Lease_enum.OxfPoison003Gb,
            Organisation_enum.HelloWorldGb, Organisation_enum.PoisonGb,
            PaymentMethod.DIRECT_DEBIT, Currency_enum.EUR,
            new ItemsSpec[] {
                new ItemsSpec(LeaseItemType.RENT, 3)
            },
            // prereqs
            new PersonaWithBuilderScript[] {
                    LeaseItemForRent_enum.OxfPoison003Gb,
                    LeaseItemForServiceCharge_enum.OxfPoison003Gb,
                    LeaseItemForTurnoverRent_enum.OxfPoison003Gb
            }
    ),
    ;

    private final ApplicationTenancy_enum applicationTenancy_d;
    private final Lease_enum lease_d;
    private final Organisation_enum seller_d;
    private final Organisation_enum buyer_d;
    private final PaymentMethod paymentMethod;
    private final Currency_enum currency_d;

    private final ItemsSpec[] itemsSpecs;
    private final PersonaWithBuilderScript<?,?>[] prereqs;

    @AllArgsConstructor
    @Data
    public static class ItemsSpec {
        private final LeaseItemType leaseItemType;
        final int durationInMonths;
    }

    public LocalDate getDueDate() {
        Ensure.ensureThatArg(lease_d.getRef(), is(lease_d.getRef()));
        return lease_d.getStartDate().plusYears(1);
    }

    @Override
    public InvoiceForLease findUsing(final ServiceRegistry2 serviceRegistry) {
        final InvoiceForLeaseRepository repository = serviceRegistry.lookupService(InvoiceForLeaseRepository.class);
        final Lease lease = lease_d.findUsing(serviceRegistry);
        final List<InvoiceForLease> invoices = repository.findByLease(lease);
        return invoices.
                stream().filter(x ->
                    Objects.equal(x.getApplicationTenancyPath(), applicationTenancy_d.getPath()) &&
                    Objects.equal(x.getBuyer(), buyer_d.findUsing(serviceRegistry)) &&
                    Objects.equal(x.getSeller(), seller_d.findUsing(serviceRegistry)) &&
                    Objects.equal(x.getDueDate(), getDueDate())
                )
                .findFirst()
                .get(); // fail fast if not found
    }

    @Override
    public InvoiceForLeaseBuilder builder() {
        return new InvoiceForLeaseBuilder()
                .setPrereq((f,ec) -> {
                    // simply make sure these objects exist
                    for (PersonaWithBuilderScript<?, ?> prereq : prereqs) {
                        f.objectFor(prereq, ec);
                    }
                })
                .setPrereq((f,ec) -> f.setApplicationTenancy(f.objectFor(applicationTenancy_d, ec)))
                .setPrereq((f,ec) -> f.setLease(f.objectFor(lease_d, ec)))
                .setPrereq((f,ec) -> f.setSeller(f.objectFor(seller_d, ec)))
                .setPrereq((f,ec) -> f.setBuyer(f.objectFor(buyer_d, ec)))
                .setPaymentMethod(paymentMethod)
                .setPrereq((f,ec) -> f.setCurrency(f.objectFor(currency_d, ec)))
                .setDueDate(getDueDate())
                .setItemSpecs(Arrays.stream(InvoiceForLease_enum.this.getItemsSpecs())
                        .map(x -> new InvoiceForLeaseBuilder.ItemsSpec(
                                        x.leaseItemType,
                                        getDueDate(),
                                        ldix(getDueDate(), getDueDate().plusMonths(x.durationInMonths))))
                        .collect(Collectors.toList())
                )
                ;
    }


}
