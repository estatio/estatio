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

import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.tax.Tax;

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
public class IncomingInvoiceItem extends InvoiceItem<IncomingInvoiceItem> {


    @Column(allowsNull = "false")
    @Getter @Setter
    private String description;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal netAmount;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal vatAmount;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal grossAmount;

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private Tax tax;


}
