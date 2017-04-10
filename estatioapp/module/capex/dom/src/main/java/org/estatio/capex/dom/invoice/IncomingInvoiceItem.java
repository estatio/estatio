package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.estatio.capex.dom.charge.IncomingCharge;
import org.estatio.capex.dom.items.FinancialItem;
import org.estatio.capex.dom.items.FinancialItemType;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.project.Project;
import org.estatio.dom.tax.Tax;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "capex",
        table = "IncomingInvoiceItem"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUBCLASS_TABLE)
@Queries({
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "capex.IncomingInvoiceItem"
)
@javax.jdo.annotations.Discriminator(
        "capex.IncomingInvoiceItem"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class IncomingInvoiceItem extends InvoiceItem<IncomingInvoiceItem> implements FinancialItem {

    @Builder
    public IncomingInvoiceItem(
            final IncomingInvoice invoice,
            final IncomingCharge incomingCharge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate startDate,
            final LocalDate endDate,
            final org.estatio.dom.asset.Property property,
            final Project project
            ){
        setInvoice(invoice);
        setIncomingCharge(incomingCharge);
        setStartDate(startDate);
        setEndDate(endDate);
        setDescription(description);
        setNetAmount(netAmount);
        setVatAmount(vatAmount);
        setGrossAmount(grossAmount);
        setTax(tax);
        setFixedAsset(property);
        setProject(project);
    }

    @Override
    public BigDecimal value() {
        return getNetAmount();
    }

    @Override
    public FinancialItemType getType() {
        return FinancialItemType.INVOICED;
    }

    @Column(allowsNull = "false")
    @Getter @Setter
    private IncomingCharge incomingCharge;

    @javax.jdo.annotations.Column(name = "fixedAssetId", allowsNull = "true")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private FixedAsset fixedAsset;

    @Getter @Setter
    @Column(allowsNull = "true")
    private Project project;

}
