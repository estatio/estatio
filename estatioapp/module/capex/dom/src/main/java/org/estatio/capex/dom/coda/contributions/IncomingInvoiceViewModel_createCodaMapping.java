package org.estatio.capex.dom.coda.contributions;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.capex.dom.coda.CodaElement;
import org.estatio.capex.dom.coda.CodaMappingFilter;
import org.estatio.capex.dom.coda.CodaMappingRepository;
import org.estatio.capex.dom.coda.CodaTransactionType;
import org.estatio.capex.dom.coda.DocumentType;
import org.estatio.capex.dom.documents.categorisation.invoice.IncomingInvoiceViewModel;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.Property;
import org.estatio.dom.charge.Charge;

@Mixin
public class IncomingInvoiceViewModel_createCodaMapping {

    private final IncomingInvoiceViewModel item;

    public IncomingInvoiceViewModel_createCodaMapping(IncomingInvoiceViewModel item) {
        this.item = item;
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @MemberOrder(name = "codaMappings", sequence = "1")
    public void $$(
            Charge charge,
            CodaElement codaElement,
            CodaTransactionType codaTransactionType,
            CodaMappingFilter projectFilter,
            CodaMappingFilter propertyFilter,
            CodaMappingFilter budgetFilter,
            boolean propertyIsFullyOwned
    ) {
        repository.findOrCreate(
                "/FRA", //TODO:
                DocumentType.INVOICE_IN,
                codaTransactionType,
                charge,
                projectFilter,
                propertyFilter,
                budgetFilter,
                propertyIsFullyOwned,
                null,
                null,
                null,
                null,
                codaElement);
    }

    public Charge default0$$() {return item.getCharge();}
    //public CodaElement default1$$() {}
    //public CodaTransactionType default2$$() {}
    public CodaMappingFilter default3$$() {return item.getProject() == null ? CodaMappingFilter.NO : CodaMappingFilter.YES;}
    public CodaMappingFilter default4$$() {return item.getFixedAsset() == null ? CodaMappingFilter.NO : CodaMappingFilter.YES;}
    public CodaMappingFilter default5$$() {return item.getBudgetItem() == null ? CodaMappingFilter.NO : CodaMappingFilter.YES;}
    public boolean default6$$() {return isFullyOwned(item.getFixedAsset());}

    private boolean isFullyOwned(final FixedAsset<?> fixedAsset) {
        if (fixedAsset instanceof Property){
            Property property = (Property) fixedAsset;
            return property.getFullOwnership();
        }
        return true;
    }

    @Inject CodaMappingRepository repository;

}
