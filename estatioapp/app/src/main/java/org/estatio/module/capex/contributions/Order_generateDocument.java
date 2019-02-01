package org.estatio.module.capex.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.applicability.ApplicabilityRepository;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.invoice.dom.DocumentTypeData;

@Mixin(method = "act")
public class Order_generateDocument {

    private final Order order;

    public Order_generateDocument(final Order order) {
        this.order = order;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Document act(final DocumentTemplate documentTemplate) {
        return documentTemplate.create(order);
    }

    public List<DocumentTemplate> choices0Act(){
        final DocumentType documentType = DocumentTypeData.ORDER_TEMPLATE.findUsing(documentTypeRepository);
        return repository.findByType(documentType);
//        return repository.findByTypeAndApplicableToAtPath(documentType, order.getAtPath());
    }

    @Inject
    DocumentTypeRepository documentTypeRepository;

    @Inject
    DocumentTemplateRepository repository;

    @Inject
    ApplicabilityRepository applicabilityRepository;

}
