package org.estatio.dom.guarantee.contributed;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.types.ReferenceType;

import org.estatio.dom.UdoDomainService;
import org.estatio.dom.guarantee.Guarantee;
import org.estatio.dom.guarantee.GuaranteeRepository;
import org.estatio.dom.guarantee.GuaranteeType;
import org.estatio.dom.lease.Lease;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class Lease_newGuaranteeContribution extends UdoDomainService<Guarantee> {


    public Lease_newGuaranteeContribution() {
        super(Lease_newGuaranteeContribution.class);
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    public Guarantee newGuarantee(
            final Lease lease,
            final @Parameter(regexPattern = ReferenceType.Meta.REGEX) String reference,
            final String name,
            final GuaranteeType guaranteeType,
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate,
            final String description,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal contractualAmount,
            final BigDecimal startAmount
    ) {

        return guaranteeRepository.newGuarantee(lease,reference,name,guaranteeType,startDate,endDate,description,contractualAmount,startAmount);
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(render = RenderType.LAZILY)
    public List<Guarantee> guarantees(final Lease lease) {
        return guaranteeRepository.findByLease(lease);
    }

    @Inject
    private GuaranteeRepository guaranteeRepository;

}
