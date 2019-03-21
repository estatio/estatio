package org.estatio.module.capex.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.document.dom.impl.docs.DocumentTemplate;
import org.incode.module.document.dom.impl.docs.DocumentTemplateRepository;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.impl.types.DocumentTypeRepository;

import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.invoice.dom.DocumentTypeData;

@Mixin(method = "prop")
public class Order_confirmationTemplate {

    public Order_confirmationTemplate(final Order order) {
        super();
    }

    @Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public DocumentTemplate prop() {
        final DocumentType documentType =
                DocumentTypeData.ORDER_CONFIRM.findUsing(documentTypeRepository);
        return documentTemplateRepository.findFirstByTypeAndApplicableToAtPath(documentType, "/ITA");
    }

    @Inject
    DocumentTemplateRepository documentTemplateRepository;
    @Inject
    DocumentTypeRepository documentTypeRepository;
}
