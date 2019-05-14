package org.incode.module.document.dom.impl.docs;

import java.util.SortedSet;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.document.dom.impl.applicability.Applicability;
import org.incode.module.document.dom.impl.applicability.ApplicabilityRepository;

@Mixin
public class DocumentTemplate_notApplicable {

    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_notApplicable(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }

    public static class NotApplicableDomainEvent extends DocumentTemplate.ActionDomainEvent {
    }

    @Action(
            domainEvent = NotApplicableDomainEvent.class,
            semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE
    )
    @ActionLayout(
            cssClassFa = "fa-minus"
    )
    @MemberOrder(name = "appliesTo", sequence = "2")
    public DocumentTemplate $$(final Applicability applicability) {
        applicabilityRepository.delete(applicability);
        return this.documentTemplate;
    }

    public TranslatableString disable$$() {
        final TranslatableString tr = factoryService.mixin(DocumentTemplate_applicable.class, documentTemplate).disable$$();
        if(tr != null) {
            return tr;
        }
        return choices0$$().isEmpty() ? TranslatableString.tr("No applicabilities to remove") : null;
    }

    public SortedSet<Applicability> choices0$$() {
        return documentTemplate.getAppliesTo();
    }

    @Inject
    ApplicabilityRepository applicabilityRepository;
    @Inject
    FactoryService factoryService;
}
