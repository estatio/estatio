package org.estatio.module.asset.dom;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.invoicing.NumeratorForOutgoingInvoicesRepository;
import org.estatio.module.numerator.dom.Numerator;

@Mixin(method = "act")
public class Property_linkToNumeratorProperty {

    private final Property property;

    public Property_linkToNumeratorProperty(final Property property) {
        this.property = property;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT, associateWith = "numeratorProperty")
    public Property act(
            @Nullable // allow the numerator property to be set back to null.
            final Property numeratorProperty) {
        property.setNumeratorProperty(numeratorProperty);
        return property;
    }

    public List<Property> choices0Act() {
        return propertyRepository.allProperties().stream()
                .filter(x -> x.getCountry() == property.getCountry())
                .filter(x -> x.getNumeratorProperty() == null)
                .filter(x -> x != property)
                .filter(this::hasInvoiceNumberNumerator)
                .sorted(Comparator.comparing(FixedAsset::getReference))
                .collect(Collectors.toList());
    }

    public String disableAct() {
        return hasInvoiceNumberNumerator(property) ? "This property has its own numerator" : null;
    }

    private boolean hasInvoiceNumberNumerator(final Property property) {
        final Numerator numeratorIfAny =
                numeratorForOutgoingInvoicesRepository.findInvoiceNumberNumeratorExact(property, null);
        return numeratorIfAny != null;
    }

    @Inject
    PropertyRepository propertyRepository;

    @Inject
    NumeratorForOutgoingInvoicesRepository numeratorForOutgoingInvoicesRepository;

}
