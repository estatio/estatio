package org.estatio.module.invoicegroup.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.invoicegroup.dom.InvoiceGroup;
import org.estatio.module.invoicegroup.dom.InvoiceGroupRepository;

@Mixin(method = "prop")
public class Property_invoiceGroup {

    private final Property property;

    public Property_invoiceGroup(final Property property) {
        this.property = property;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    public InvoiceGroup prop() {
        return invoiceGroupRepository.findContainingProperty(property).orElse(null);
    }

    @Inject
    InvoiceGroupRepository invoiceGroupRepository;

}
