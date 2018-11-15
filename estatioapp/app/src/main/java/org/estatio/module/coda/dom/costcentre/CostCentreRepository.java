package org.estatio.module.coda.dom.costcentre;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = CostCentre.class,
        objectType = "coda.CostCentreRepository"
)
public class CostCentreRepository {

    @Programmatic
    public List<CostCentre> listAll() {
        return repositoryService.allInstances(CostCentre.class);
    }

    @Programmatic
    public CostCentre findByElement3(
            final String element3) {
        return repositoryService.uniqueMatch(
                new org.apache.isis.applib.query.QueryDefault<>(
                        CostCentre.class,
                        "findByElement3",
                        "element3", element3));
    }


    @Programmatic
    public CostCentre upsert(final String element3, final String extRef3Segment2) {
        CostCentre costCentre = findByElement3(element3);
        if (costCentre == null) {
            costCentre = repositoryService.persist(new CostCentre(element3, extRef3Segment2));
        } else {
            costCentre.setExtRef3Segment2(extRef3Segment2);
        }
        return costCentre;
    }

    @javax.inject.Inject
    RepositoryService repositoryService;

}
