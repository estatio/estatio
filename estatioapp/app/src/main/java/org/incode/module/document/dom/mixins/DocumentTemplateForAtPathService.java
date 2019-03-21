package org.incode.module.document.dom.mixins;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;
import org.incode.module.document.dom.spi.ApplicationTenancyService;

@DomainService(nature = NatureOfService.DOMAIN)
public class DocumentTemplateForAtPathService {

    @Programmatic
    public List<DocumentTemplate> documentTemplatesForPreview(final Object domainObject) {
        final List<DocumentTemplate> templates = Lists.newArrayList();

        final String atPath = atPathFor(domainObject);
        if(atPath == null) {
            return templates;
        }

        final List<DocumentTemplate> templatesForPath =
                documentTemplateRepository.findByApplicableToAtPath(atPath);

        return Lists.newArrayList(
                templatesForPath.stream()
                                .filter(template -> canPreview(template))
                                .collect(Collectors.toList()));
    }

    @Programmatic
    public List<DocumentTemplate> documentTemplatesForCreateAndAttach(final Object domainObject) {
        final List<DocumentTemplate> templates = Lists.newArrayList();

        final String atPath = atPathFor(domainObject);
        if(atPath == null) {
            return templates;
        }

        final List<DocumentTemplate> templatesForPath =
                documentTemplateRepository.findByApplicableToAtPath(atPath);


        return Lists.newArrayList(
                templatesForPath.stream()
                        .filter(template -> {
                            final AttachmentAdvisor advisor = template.newAttachmentAdvisor(domainObject);
                            if (advisor == null) {
                                return false;
                            }

                            // at this stage we are asking the advisor if it would be able to attach a document
                            // if created.  We don't yet have that document, so it is null.
                            final Document document = null;
                            final List<AttachmentAdvisor.PaperclipSpec> paperclipSpecs =
                                    advisor.advise(template, domainObject, document);
                            return canCreate(template, paperclipSpecs);
                        })
                        .collect(Collectors.toList()));
    }

    private String atPathFor(final Object domainObject) {
        return applicationTenancyServices.stream()
                .map(x -> x.atPathFor(domainObject))
                .filter(x -> x != null)
                .findFirst()
                .orElse(null);
    }

    private boolean canPreview(final DocumentTemplate template) {
        return template.getContentRenderingStrategyApi().isPreviewsToUrl();
    }

    private boolean canCreate(final DocumentTemplate template, final List<AttachmentAdvisor.PaperclipSpec> paperclipSpecs) {
        return !template.isPreviewOnly() &&
                paperclipSpecs.stream()
                              .filter(paperclipSpec -> paperclipRepository.canAttach(paperclipSpec.getAttachTo()))
                              .findFirst()
                              .isPresent();
    }


    @Inject
    PaperclipRepository paperclipRepository;

    @Inject
    List<ApplicationTenancyService> applicationTenancyServices;

    @Inject
    DocumentTemplateRepository documentTemplateRepository;



}
