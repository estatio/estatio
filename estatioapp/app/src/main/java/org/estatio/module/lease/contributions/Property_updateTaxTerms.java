package org.estatio.module.lease.contributions;

import javax.inject.Inject;

import org.assertj.core.util.Lists;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.services.message.MessageService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTermForTax;

@Mixin()
public class Property_updateTaxTerms {

    private final Property property;

    public Property_updateTaxTerms(final Property property) {
        this.property = property;
    }

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(contributed = Contributed.AS_ACTION)
    public Property updateTaxTerms(final LocalDate paymentDate) {
        leaseRepository.findByAssetAndActiveOnDate(property, paymentDate).forEach(lease->{
            Lists.newArrayList(lease.getItems()).stream().filter(item->item.getType().equals(LeaseItemType.TAX)).forEach(item->{
                item.verifyUntil(paymentDate.plusDays(1));
                Lists.newArrayList(item.getTerms()).stream()
                    .filter(term->term.getClass().isAssignableFrom(LeaseTermForTax.class))
                    .filter(term->term.isActiveOn(paymentDate))
                    .forEach(term->{
                        LeaseTermForTax castedTerm = (LeaseTermForTax) term;
                        if (castedTerm.getPaymentDate()==null) {
                            castedTerm.setPaymentDate(paymentDate);
                        } else {
                            String warning = String.format("Payment date was already set on %s for tax on lease %s", castedTerm.getPaymentDate().toString("dd-MM-yyyy"), castedTerm.getLeaseItem().getLease().getReference());
                            messageService.warnUser(warning);
                        }
                    });
                });
            });
        return property;
    }

    @Inject
    LeaseRepository leaseRepository;

    @Inject
    MessageService messageService;

}
