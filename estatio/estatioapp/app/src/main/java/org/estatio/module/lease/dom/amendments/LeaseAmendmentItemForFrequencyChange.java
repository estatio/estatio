package org.estatio.module.lease.dom.amendments;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.InheritanceStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.lease.dom.InvoicingFrequency;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "dbo" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@javax.jdo.annotations.Discriminator("amendments.LeaseAmendmentItemForFrequencyChange")
public class LeaseAmendmentItemForFrequencyChange extends LeaseAmendmentItem {

    @Column(allowsNull = "false")
    @Getter @Setter
    private InvoicingFrequency invoicingFrequencyOnLease;

    @Column(allowsNull = "true")
    @Getter @Setter
    private InvoicingFrequency amendedInvoicingFrequency;

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public LeaseAmendmentItemForFrequencyChange changeFrequencies(final InvoicingFrequency invoicingFrequencyOnLease, final InvoicingFrequency amendedInvoicingFrequency){
        setInvoicingFrequencyOnLease(invoicingFrequencyOnLease);
        setAmendedInvoicingFrequency(amendedInvoicingFrequency);
        return this;
    }

    public InvoicingFrequency default0ChangeFrequencies(){
        return getInvoicingFrequencyOnLease();
    }

    public InvoicingFrequency default1ChangeFrequencies(){
        return getAmendedInvoicingFrequency();
    }

    public String disableChangeFrequencies(){
        final String warning = String.format("Amendment in state of %s cannot be changed", getLeaseAmendment().getState());
        return !getLeaseAmendment().getState().isMutable ? warning : null;
    }

    @Action(semantics = SemanticsOf.SAFE)
    @Override
    public LeaseAmendmentItemType getType(){
        return LeaseAmendmentItemType.INVOICING_FREQUENCY_CHANGE;
    }

}
