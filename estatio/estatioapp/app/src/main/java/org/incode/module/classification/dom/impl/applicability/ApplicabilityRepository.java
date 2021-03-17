package org.incode.module.classification.dom.impl.applicability;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Applicability.class
)
public class ApplicabilityRepository {

    //region > findByDomainTypeAndUnderAtPath (programmatic)
    @Programmatic
    public List<Applicability> findByDomainTypeAndUnderAtPath(final Class<?> domainType, final String atPath) {
        return repositoryService.allMatches(
                new QueryDefault<>(Applicability.class,
                        "findByDomainTypeAndUnderAtPath",
                        "domainType", domainType.getName(),
                        "atPath", atPath));
    }
    //endregion

    //region > injected
    @Inject
    RepositoryService repositoryService;
    //endregion

}
