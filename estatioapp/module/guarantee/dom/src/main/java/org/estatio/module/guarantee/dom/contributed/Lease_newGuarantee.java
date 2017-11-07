package org.estatio.module.guarantee.dom.contributed;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.base.dom.types.ReferenceType;

import org.estatio.module.guarantee.dom.Guarantee;
import org.estatio.module.guarantee.dom.GuaranteeType;
import org.estatio.module.lease.dom.Lease;

/**
 * Cannot be inlined (needs to be a mixin) because Lease does not know about guarantees
 */
@Mixin
public class Lease_newGuarantee {

    private final Lease lease;

    public Lease_newGuarantee(final Lease lease) {
        this.lease = lease;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @MemberOrder(name = "guarantees", sequence = "1")
    public Guarantee newGuarantee(
            final @Parameter(regexPattern = ReferenceType.Meta.REGEX) String reference,
            final String name,
            final GuaranteeType guaranteeType,
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate,
            final String description,
            final @Parameter(optionality = Optionality.OPTIONAL) BigDecimal contractualAmount,
            final BigDecimal startAmount
    ) {
        return leaseGuaranteeService.newGuarantee(lease, reference, name, guaranteeType, startDate, endDate, description, contractualAmount, startAmount);
    }


    @Inject
    LeaseGuaranteeService leaseGuaranteeService;

}
