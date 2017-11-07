package org.estatio.capex.dom.coda.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.coda.CodaElement;
import org.estatio.capex.dom.coda.CodaMappingRepository;
import org.estatio.capex.dom.coda.CodaTransactionType;
import org.estatio.capex.dom.coda.DocumentType;
import org.estatio.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.module.charge.dom.Charge;

/**
 * This cannot be inlined (needs to be a mixin) because IncomingInvoiceItem does not know about Coda.
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
