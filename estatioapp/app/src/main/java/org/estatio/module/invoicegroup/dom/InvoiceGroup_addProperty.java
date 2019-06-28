package org.estatio.module.invoicegroup.dom;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.asset.dom.Property;

@Mixin(method = "act")
public class InvoiceGroup_addProperty {

    private final InvoiceGroup invoiceGroup;

    public InvoiceGroup_addProperty(InvoiceGroup invoiceGroup) {
        this.invoiceGroup = invoiceGroup;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public InvoiceGroup act(final Property property) {

        invoiceGroupRepository.findContainingProperty(property)
                .ifPresent(currentOwner -> currentOwner.getProperties().remove(property));

        invoiceGroup.getProperties().add(property);

        return invoiceGroup;
    }

    @Inject
    InvoiceGroupRepository invoiceGroupRepository;

}
