package org.estatio.capex.dom.invoice;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;

import org.estatio.dom.invoice.InvoiceItem;

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


}
