package org.estatio.module.lease.businessdefinitions;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.lease.dom.Lease;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "businessdefinitions.LeaseBusinessDefinitionsService"
)
public class LeaseBusinessDefinitionsService {

    public LocalDate getLeaseEvaluationDateOnReferenceDate(final Lease lease, final LocalDate referenceDate){
        if (leaseEvaluationDateDefinition==null) leaseEvaluationDateDefinition = ILeaseEvaluationDateDefinition.NOOP; // For testing
        return leaseEvaluationDateDefinition.leaseEvaluationDateFor(lease, referenceDate);
    }

    @Inject
    ILeaseEvaluationDateDefinition leaseEvaluationDateDefinition;

}
