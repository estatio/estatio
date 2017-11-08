package org.estatio.module.capex.dom.coda.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.capex.dom.coda.CodaElement;
import org.estatio.module.capex.dom.coda.CodaMappingRepository;
import org.estatio.module.capex.dom.coda.CodaTransactionType;
import org.estatio.module.capex.dom.coda.DocumentType;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.Property;
import org.estatio.module.charge.dom.Charge;

/**
 * TODO: this could be inlined, however we'll probably split out coda as a separate module, in which will be a regular contribution again
 */
@Mixin
public class IncomingInvoiceItem_createCodaMapping {

    private final IncomingInvoiceItem incomingInvoiceItem;

    public IncomingInvoiceItem_createCodaMapping(IncomingInvoiceItem incomingInvoiceItem) {
        this.incomingInvoiceItem = incomingInvoiceItem;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(name = "codaMappings", sequence = "1")
    public void $$(
            Charge charge,
            CodaElement codaElement,
            CodaTransactionType codaTransactionType,
            boolean propertyIsFullyOwned
    ) {
        repository.findOrCreate(
                "/FRA", //TODO:
                DocumentType.INVOICE_IN,
                incomingInvoiceItem.getIncomingInvoiceType(),
                codaTransactionType,
                charge,
                propertyIsFullyOwned,
                null,
                null,
                null,
                null, codaElement);
    }

    public Charge default0$$() {return incomingInvoiceItem.getCharge();}
    //public CodaElement default1$$() {}
    //public CodaTransactionType default2$$() {}
    public boolean default3$$() {return isFullyOwned(incomingInvoiceItem.getFixedAsset());}

    private boolean isFullyOwned(final FixedAsset<?> fixedAsset) {
        if (fixedAsset instanceof Property){
            Property property = (Property) fixedAsset;
            return property.getFullOwnership();
        }
        return true;
    }

    @Inject CodaMappingRepository repository;

}
