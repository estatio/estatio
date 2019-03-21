package org.incode.module.document.dom.impl.applicability;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;

/**
 * TODO: remove this once move to DocumentTypeData, DocumentTemplateData and RenderingStrategyData
 */
@DomainService(
        nature = NatureOfService.DOMAIN,
        repositoryFor = Applicability.class
)
public class ApplicabilityRepository {

    public String getId() {
        return "incodeDocuments.ApplicabilityRepository";
    }

    //region > create
    @Programmatic
    public Applicability create(
            final DocumentTemplate documentTemplate,
            final String domainClassName,
            final String rendererModelFactoryClassName,
            final String attachmentAdvisorClassName
            ) {
        Applicability applicability = new Applicability(documentTemplate, domainClassName, rendererModelFactoryClassName, attachmentAdvisorClassName);
        repositoryService.persistAndFlush(applicability);
        return applicability;
    }
    //endregion

    //region > delete (programmatic)
    public void delete(final Applicability applicability) {
        repositoryService.removeAndFlush(applicability);
    }
    //endregion

    //region > injected
    @Inject
    RepositoryService repositoryService;

    //endregion

}
