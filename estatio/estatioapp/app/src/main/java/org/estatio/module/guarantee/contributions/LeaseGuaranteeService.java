package org.estatio.module.guarantee.contributions;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.base.dom.types.ReferenceType;

import org.estatio.module.base.dom.UdoDomainService;
import org.estatio.module.guarantee.dom.Guarantee;
import org.estatio.module.guarantee.dom.GuaranteeRepository;
import org.estatio.module.guarantee.dom.GuaranteeType;
import org.estatio.module.lease.dom.Lease;

@DomainService(nature = NatureOfService.DOMAIN)
public class LeaseGuaranteeService extends UdoDomainService<Guarantee> {


    public LeaseGuaranteeService() {
        super(LeaseGuaranteeService.class);
    }


    @Programmatic
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



    @Programmatic
    public List<Guarantee> guarantees(final Lease lease) {
        return guaranteeRepository.findByLease(lease);
    }





    @Inject
    private GuaranteeRepository guaranteeRepository;

}
