package org.estatio.module.docflow.dom;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.query.QueryDefault;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;

import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

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
    public DocFlowZip findBySdId(final long sdId) {
        return repositoryService.uniqueMatch(
                new QueryDefault<>(
                        DocFlowZip.class,
                        "findBySdId",
                        "sdId", sdId));
    }

    @Programmatic
    public DocFlowZip persist(final DocFlowZip docFlowZip) {
        return repositoryService.persistAndFlush(docFlowZip);
    }


    @Inject
    RepositoryService repositoryService;

}
