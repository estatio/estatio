package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;

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
//@Unique(name = "IncomingInvoiceItem_number_UNQ", members = { "number" })
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
