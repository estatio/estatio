package org.estatio.dom.guarantee.contributed;

import org.apache.isis.applib.annotation.*;
import org.estatio.dom.RegexValidation;
import org.estatio.dom.UdoDomainService;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.guarantee.GuaranteeType;
import org.estatio.dom.guarantee.Guarantees;
import org.estatio.dom.lease.Lease;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class OnLease extends UdoDomainService<Guarantee> {


    public OnLease() {
        super(OnLease.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Guarantee newGuarantee(
            final Lease lease,
            final @Parameter(regexPattern = RegexValidation.REFERENCE) String reference,
            final String name,
            final GuaranteeType guaranteeType,
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate,
            final String description,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal contractualAmount,
            final BigDecimal startAmount
    ) {

        return guarantees.newGuarantee(lease,reference,name,guaranteeType,startDate,endDate,description,contractualAmount,startAmount);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.LAZILY)
    public List<Guarantee> guarantees(final Lease lease) {
        return guarantees.findByLease(lease);
    }

    @Inject
    private Guarantees guarantees;

}
