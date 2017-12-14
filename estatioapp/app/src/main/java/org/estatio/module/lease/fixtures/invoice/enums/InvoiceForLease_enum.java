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
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

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

    /*
    private static final OrganisationAndComms_enum seller_d = OrganisationAndComms_enum.HelloWorldGb;
    private static final OrganisationAndComms_enum buyer_d = OrganisationAndComms_enum.PoisonGb;
    private static final Lease_enum lease_d = Lease_enum.OxfPoison003Gb;
    private static final ApplicationTenancy_enum applicationTenancy_d = ApplicationTenancy_enum.GbOxf;

    public static final String PARTY_REF_SELLER = seller_d.getRef();
    public static final String PARTY_REF_BUYER = buyer_d.getRef();
    public static final String LEASE_REF = lease_d.getRef();
    public static final String AT_PATH = applicationTenancy_d.getPath();

    // simply within the lease's start/end date
    public static LocalDate startDateFor(final Lease lease) {
        Ensure.ensureThatArg(lease.getReference(), is(lease_d.getRef()));
        return lease.getStartDate().plusYears(1);
    }

    public InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003() {
        this(null, null);
    }

    public InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003(final String friendlyName, final String localName) {
        super(friendlyName, localName);
    }

    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, OrganisationAndComms_enum.HelloWorldNl.builder());

        executionContext.executeChild(this, LeaseItemForRent_enum.OxfPoison003Gb.builder());
        executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfPoison003Gb.builder());
        executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfPoison003Gb.builder());

        // exec

        final ApplicationTenancy applicationTenancy = applicationTenancies.findTenancyByPath(AT_PATH);
        final Lease lease = leaseRepository.findLeaseByReference(LEASE_REF);

        final LocalDate startDate = startDateFor(lease);

        final InvoiceForLease invoice = createInvoiceAndNumerator(
                applicationTenancy,
                lease, PARTY_REF_SELLER,
                PARTY_REF_BUYER, PaymentMethod.DIRECT_DEBIT,
                Currency_enum.EUR.getReference(),
                startDate, executionContext);

        createInvoiceItemsForTermsOfFirstLeaseItemOfType(
                invoice, LeaseItemType.RENT,
                startDate, ldix(startDate, startDate.plusMonths(3)),
                executionContext);
    }
     */
    OxfMiracl005Gb(
            ApplicationTenancy_enum.GbOxf, Lease_enum.OxfMiracl005Gb,
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.MiracleGb,
            PaymentMethod.DIRECT_DEBIT, Currency_enum.EUR,
            new ItemsSpec[] {
                new ItemsSpec(LeaseItemType.RENT_DISCOUNT_FIXED, 3)
            },
            new PersonaWithBuilderScript[] {
                    //OrganisationAndComms_enum.HelloWorldGb,
                    LeaseItemForDiscount_enum.OxfMiracle005bGb
            }
    ),
    KalPoison001Nl(
            ApplicationTenancy_enum.NlKal, Lease_enum.KalPoison001Nl,
            OrganisationAndComms_enum.AcmeNl, OrganisationAndComms_enum.PoisonNl,
            PaymentMethod.DIRECT_DEBIT, Currency_enum.EUR,
            new ItemsSpec[] {
                new ItemsSpec(LeaseItemType.RENT, 3)
            },
            new PersonaWithBuilderScript[] {
                    LeaseItemForRent_enum.KalPoison001Nl
            }
    ),
    OxfPoison003Gb(
            ApplicationTenancy_enum.GbOxf, Lease_enum.OxfPoison003Gb,
            OrganisationAndComms_enum.HelloWorldGb, OrganisationAndComms_enum.PoisonGb,
            PaymentMethod.DIRECT_DEBIT, Currency_enum.EUR,
            new ItemsSpec[] {
                new ItemsSpec(LeaseItemType.RENT, 3)
            },
            new PersonaWithBuilderScript[] {
                    // OrganisationAndComms_enum.HelloWorldNl
                    LeaseItemForRent_enum.OxfPoison003Gb,
                    LeaseItemForServiceCharge_enum.OxfPoison003Gb,
                    LeaseItemForTurnoverRent_enum.OxfPoison003Gb
            }
    ),
    ;

    private final ApplicationTenancy_enum applicationTenancy_d;
    private final Lease_enum lease_d;
    private final OrganisationAndComms_enum seller_d;
    private final OrganisationAndComms_enum buyer_d;
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

    LocalDate getDueDate() {
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
