package org.estatio.module.lease.dom.amendments;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.agreement.dom.role.AgreementRoleType;
import org.estatio.module.agreement.dom.role.AgreementRoleTypeRepository;
import org.estatio.module.agreement.dom.type.AgreementTypeRepository;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Amendment.class
)
public class AmendmentRepository {

    final public static String NAME_SUFFIX = "-AMND";

    @Programmatic
    public List<Amendment> listAll() {
        return repositoryService.allInstances(Amendment.class);
    }

    @Programmatic
    public List<Amendment> findByLease(final Lease lease) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Amendment.class,
                        "findByLease",
                        "lease", lease));
    }

    @Programmatic
    public List<Amendment> findByState(final AmendmentState state) {
        return repositoryService.allMatches(
                new QueryDefault<>(
                        Amendment.class,
                        "findByState",
                        "state", state));
    }

    @Programmatic
    public Amendment create(
            final Lease lease,
            final AmendmentState state,
            final LocalDate startDate,
            final LocalDate endDate) {

        final Amendment amendment = new Amendment();
        amendment.setReference(lease.getReference());
        amendment.setName(lease.getReference().concat(NAME_SUFFIX));
        amendment.setType(agreementTypeRepository.find(AmendmentAgreementTypeEnum.AMENDMENT));
        amendment.setLease(lease);
        amendment.setState(state);
        amendment.setStartDate(startDate);
        amendment.setEndDate(endDate);
        serviceRegistry2.injectServicesInto(amendment);
        repositoryService.persistAndFlush(amendment);

        final AgreementRoleType artLandlord = agreementRoleTypeRepository
                        .find(LeaseAgreementRoleTypeEnum.LANDLORD);
        amendment.newRole(artLandlord, lease.getPrimaryParty(), null ,null);
        final AgreementRoleType artTenant = agreementRoleTypeRepository
                .find(LeaseAgreementRoleTypeEnum.TENANT);
        amendment.newRole(artTenant, lease.getSecondaryParty(), null ,null);

        return amendment;
    }

    @Inject
    RepositoryService repositoryService;

    @Inject
    ServiceRegistry2 serviceRegistry2;

    @Inject
    AgreementTypeRepository agreementTypeRepository;

    @Inject
    AgreementRoleTypeRepository agreementRoleTypeRepository;


}
