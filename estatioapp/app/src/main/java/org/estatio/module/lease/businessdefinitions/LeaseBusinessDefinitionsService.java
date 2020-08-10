package org.estatio.module.lease.businessdefinitions;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.registry.ServiceRegistry2;

import org.estatio.module.lease.dom.Lease;

@DomainService(
        nature = NatureOfService.DOMAIN,
        objectType = "businessdefinitions.LeaseBusinessDefinitionsService"
)
public class LeaseBusinessDefinitionsService {

    public LocalDate getLeaseEvaluationDateOnReferenceDate(final Lease lease, final LocalDate referenceDate){
        //TODO: find a better pattern to bring in services with company specific data ... ?
        final Object leaseEvaluationDateService = serviceRegistry2.getRegisteredServices().stream()
                .filter(s -> s instanceof ILeaseEvaluationDateDefinition)
                .findFirst().orElse(null);
        if (leaseEvaluationDateService!=null){
            ILeaseEvaluationDateDefinition leaseEvaluationDateDefinition = (ILeaseEvaluationDateDefinition) leaseEvaluationDateService;
            return leaseEvaluationDateDefinition.leaseEvaluationDateFor(lease, referenceDate);
        } else {
            messageService.warnUser("LeaseEvaluationDateDefinition could not be found; please contact support");
            return null;
        }
    }

    @Inject MessageService messageService;

    @Inject ServiceRegistry2 serviceRegistry2;

}
