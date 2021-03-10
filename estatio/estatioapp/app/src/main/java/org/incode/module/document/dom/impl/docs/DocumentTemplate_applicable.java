package org.incode.module.document.dom.impl.docs;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.document.dom.impl.applicability.Applicability;
import org.incode.module.document.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisor;
import org.incode.module.document.dom.impl.applicability.AttachmentAdvisorAttachToNone;
import org.incode.module.document.dom.impl.applicability.RendererModelFactory;
import org.incode.module.document.dom.services.ClassNameViewModel;
import org.incode.module.document.dom.spi.AttachmentAdvisorClassNameService;
import org.incode.module.document.dom.spi.RendererModelFactoryClassNameService;
import org.incode.module.document.dom.types.FqcnType;

@Mixin
public class DocumentTemplate_applicable {
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_applicable(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }



    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @ActionLayout(cssClassFa = "fa-plus", contributed = Contributed.AS_ACTION)
    @MemberOrder(name = "appliesTo", sequence = "1")
    public DocumentTemplate $$(
            @Parameter(maxLength = FqcnType.Meta.MAX_LEN, mustSatisfy = FqcnType.Meta.Specification.class)
            @ParameterLayout(named = "Domain type")
            final String domainClassName,
            @Parameter(maxLength = FqcnType.Meta.MAX_LEN, mustSatisfy = FqcnType.Meta.Specification.class)
            @ParameterLayout(named = "Renderer Model Factory")
            final ClassNameViewModel rendererModelFactoryClassNameViewModel,
            @Parameter(maxLength = FqcnType.Meta.MAX_LEN, mustSatisfy = FqcnType.Meta.Specification.class)
            @ParameterLayout(named = "Attachment Advisor")
            final ClassNameViewModel attachmentAdvisorClassNameViewModel) {

        applicable(
                domainClassName, rendererModelFactoryClassNameViewModel.getFullyQualifiedClassName(), attachmentAdvisorClassNameViewModel.getFullyQualifiedClassName());
        return this.documentTemplate;
    }

    public TranslatableString disable$$() {
        if (rendererModelFactoryClassNameService == null) {
            return TranslatableString.tr(
                    "No RendererModelFactoryClassNameService registered to locate implementations of RendererModelFactory");
        }
        if (attachmentAdvisorClassNameService == null) {
            return TranslatableString.tr(
                    "No AttachmentAdvisorClassNameService registered to locate implementations of AttachmentAdvisor");
        }
        return null;
    }

    public List<ClassNameViewModel> choices1$$() {
        return rendererModelFactoryClassNameService.rendererModelFactoryClassNames();
    }

    public List<ClassNameViewModel> choices2$$() {
        return attachmentAdvisorClassNameService.attachmentAdvisorClassNames();
    }

    public TranslatableString validate0$$(final String domainTypeName) {

        return isApplicable(domainTypeName) ?
                TranslatableString.tr(
                        "Already applicable for '{domainTypeName}'",
                        "domainTypeName", domainTypeName)
                : null;
    }


    @Programmatic
    public Applicability applicable(
            final Class<?> domainClass,
            final Class<? extends RendererModelFactory> renderModelFactoryClass,
            final Class<? extends AttachmentAdvisor> attachmentAdvisorClass) {
        return applicable(
                domainClass.getName(),
                renderModelFactoryClass,
                attachmentAdvisorClass != null
                        ? attachmentAdvisorClass
                        : AttachmentAdvisorAttachToNone.class
        );
    }

    @Programmatic
    public Applicability applicable(
            final String domainClassName,
            final Class<? extends RendererModelFactory> renderModelFactoryClass,
            final Class<? extends AttachmentAdvisor> attachmentAdvisorClass) {
        return applicable(domainClassName, renderModelFactoryClass.getName(), attachmentAdvisorClass.getName() );
    }

    @Programmatic
    public Applicability applicable(
            final String domainClassName,
            final String renderModelFactoryClassName,
            final String attachmentAdvisorClassName) {
        Applicability applicability = existingApplicability(domainClassName);
        if(applicability == null) {
            applicability = applicabilityRepository.create(documentTemplate, domainClassName, renderModelFactoryClassName, attachmentAdvisorClassName);
        } else {
            applicability.setRendererModelFactoryClassName(renderModelFactoryClassName);
            applicability.setAttachmentAdvisorClassName(attachmentAdvisorClassName);
        }
        return applicability;
    }

    private boolean isApplicable(final String domainClassName) {
        return existingApplicability(domainClassName) != null;
    }
    private Applicability existingApplicability(final String domainClassName) {
        SortedSet<Applicability> applicabilities = documentTemplate.getAppliesTo();
        for (Applicability applicability : applicabilities) {
            if (applicability.getDomainClassName().equals(domainClassName)) {
                return applicability;
            }
        }
        return null;
    }


    @Inject
    RendererModelFactoryClassNameService rendererModelFactoryClassNameService;
    @Inject
    AttachmentAdvisorClassNameService attachmentAdvisorClassNameService;
    @Inject
    ApplicabilityRepository applicabilityRepository;

}
