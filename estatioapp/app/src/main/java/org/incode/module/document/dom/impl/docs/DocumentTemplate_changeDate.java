package org.incode.module.document.dom.impl.docs;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.i18n.TranslatableString;

import org.incode.module.document.dom.DocumentModule;

@Mixin
public class DocumentTemplate_changeDate {

    //region > constructor
    private final DocumentTemplate documentTemplate;

    public DocumentTemplate_changeDate(final DocumentTemplate documentTemplate) {
        this.documentTemplate = documentTemplate;
    }
    //endregion


    public static class ActionDomainEvent extends DocumentModule.ActionDomainEvent<DocumentTemplate_changeDate>  { }

    @Action(
            semantics = SemanticsOf.IDEMPOTENT,
            domainEvent = ActionDomainEvent.class
    )
    public DocumentTemplate $$(
            @ParameterLayout(named = "New date")
            final LocalDate date) {
        documentTemplate.setDate(date);
        return documentTemplate;
    }

    public LocalDate default0$$() {
        return documentTemplate.getDate();
    }

    public TranslatableString validate0$$(LocalDate proposedDate) {
        final DocumentTemplate original = documentTemplate;
        final String proposedAtPath = documentTemplate.getAtPath();

        return documentTemplateRepository.validateApplicationTenancyAndDate(original.getType(), proposedAtPath, proposedDate, original);
    }


    @Inject
    private DocumentTemplateRepository documentTemplateRepository;


}
