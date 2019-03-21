package org.incode.module.document.dom.impl.applicability;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.services.ClassNameViewModel;
import org.incode.module.document.dom.services.ClassService;
import org.incode.module.document.dom.spi.RendererModelFactoryClassNameService;

/**
 * TODO: remove this once move to DocumentTypeData, DocumentTemplateData and RenderingStrategyData
 */
@Mixin
public class Applicability_changeRendererModelFactory {


    //region > constructor
    private final Applicability applicability;

    public Applicability_changeRendererModelFactory(final Applicability applicability) {
        this.applicability = applicability;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<Applicability_changeRendererModelFactory>  { }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    @MemberOrder(name = "rendererModelFactoryClassName",sequence = "1")
    public Applicability_changeRendererModelFactory $$(final ClassNameViewModel classNameViewModel) {
        applicability.setRendererModelFactoryClassName(classNameViewModel.getFullyQualifiedClassName());
        return this;
    }

    public TranslatableString disable$$() {
        return classNameService == null
                ? TranslatableString.tr(
                "No RendererModelFactoryClassNameService registered to locate implementations of RendererModelFactory")
                : null;
    }

    public ClassNameViewModel default0$$() {
        return new ClassNameViewModel(classService.load(applicability.getRendererModelFactoryClassName()));
    }

    public List<ClassNameViewModel> choices0$$() {
        return classNameService.rendererModelFactoryClassNames();
    }


    //region > injected services
    @Inject
    RendererModelFactoryClassNameService classNameService;
    @Inject
    ClassService classService;
    //endregion
}
