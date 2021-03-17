package org.incode.module.document.dom.impl.docs;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.command.dom.CommandJdo;
import org.isisaddons.module.command.dom.T_backgroundCommands;

import org.incode.module.document.dom.spi.SupportingDocumentsEvaluator;

@Mixin
public class Document_backgroundCommands extends T_backgroundCommands<Document> {

    private final Document document;

    public Document_backgroundCommands(final Document document) {
        super(document);
        this.document = document;
    }

    public static class ActionDomainEvent extends T_backgroundCommands.ActionDomainEvent {}

    @Action(
            semantics = SemanticsOf.SAFE,
            domainEvent = ActionDomainEvent.class
    )
    public List<CommandJdo> $$() {
        return super.$$();
    }

    public boolean hide$$() {
        // hide for supporting documents
        for (SupportingDocumentsEvaluator supportingDocumentsEvaluator : supportingDocumentsEvaluators) {
            final SupportingDocumentsEvaluator.Evaluation evaluation =
                    supportingDocumentsEvaluator.evaluate(document);
            if(evaluation == SupportingDocumentsEvaluator.Evaluation.SUPPORTING) {
                return true;
            }
        }
        return false;
    }

    @Inject
    List<SupportingDocumentsEvaluator> supportingDocumentsEvaluators;

}
