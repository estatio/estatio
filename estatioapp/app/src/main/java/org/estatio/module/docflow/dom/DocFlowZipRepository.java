package org.estatio.module.docflow.dom;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;

@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = DocFlowZip.class,
        objectType = "docflow.DocFlowZipRepository"
)
public class DocFlowZipRepository {

    @Programmatic
    public List<DocFlowZip> listAll() {
        return repositoryService.allInstances(DocFlowZip.class);
    }

    @Programmatic
    public DocFlowZip findBySdiId(final long sdiId) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        DocFlowZip.class,
                        "findBySdiId",
                        "sdiId", sdiId));
    }

    @Programmatic
    public Optional<DocFlowZip> optFindBySdiId(final long sdiId) {
        return Optional.ofNullable(findBySdiId(sdiId));
    }



    @Programmatic
    public DocFlowZip persist(final DocFlowZip docFlowZip) {
        return repositoryService.persistAndFlush(docFlowZip);
    }


    @Inject
    RepositoryService repositoryService;

}
