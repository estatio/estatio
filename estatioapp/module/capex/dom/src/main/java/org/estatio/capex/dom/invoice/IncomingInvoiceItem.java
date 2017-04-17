package org.estatio.capex.dom.invoice;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.estatio.capex.dom.items.FinancialItem;
import org.estatio.capex.dom.items.FinancialItemType;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.capex.dom.project.Project;
import org.estatio.dom.tax.Tax;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE

        // unused since rolled-up to superclass:
        //,schema = "dbo"
        //,table = "IncomingInvoiceItem"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Queries({
        @Query(
                name = "findByInvoiceAndCharge", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoiceItem "
                        + "WHERE invoice == :invoice "
                        + "   && charge == :charge ")
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

    public IncomingInvoiceItem(
            final BigInteger sequence,
            final IncomingInvoice invoice,
            final Charge charge,
            final String description,
            final BigDecimal netAmount,
            final BigDecimal vatAmount,
            final BigDecimal grossAmount,
            final Tax tax,
            final LocalDate dueDate,
            final LocalDate startDate,
            final LocalDate endDate,
            final org.estatio.dom.asset.Property property,
            final Project project
    ){
        setSequence(sequence);

        setInvoice(invoice);
        setCharge(charge);
        setDescription(description);

        setNetAmount(netAmount);
        setVatAmount(vatAmount);
        setGrossAmount(grossAmount);

        setDueDate(dueDate);
        setStartDate(startDate);
        setEndDate(endDate);

        setTax(tax);
        setFixedAsset(property);
        setProject(project);
    }

    @Override
    @Programmatic
    public BigDecimal value() {
        return getNetAmount();
    }

    @Override
    public FinancialItemType getType() {
        return FinancialItemType.INVOICED;
    }

    @javax.jdo.annotations.Column(name = "fixedAssetId", allowsNull = "true")
    @Property(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private FixedAsset fixedAsset;

    @Getter @Setter
    @Column(allowsNull = "true", name="projectId")
    @Property(hidden = Where.REFERENCES_PARENT)
    private Project project;

}
