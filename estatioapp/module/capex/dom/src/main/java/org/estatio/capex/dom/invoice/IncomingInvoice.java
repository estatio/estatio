package org.estatio.capex.dom.invoice;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.party.Party;

import lombok.Builder;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "capex",
        table = "IncomingInvoice"
)
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.SUBCLASS_TABLE)
@javax.jdo.annotations.Discriminator(
        "capex.IncomingInvoice"
)
@Queries({
        @Query(
                name = "findByInvoiceNumber", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.invoice.IncomingInvoice "
                        + "WHERE invoiceNumber == :invoiceNumber ")
})
@Unique(name = "IncomingInvoice_number_UNQ", members = { "number" })
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "capex.IncomingInvoice"
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class IncomingInvoice extends Invoice<IncomingInvoice> {

    public IncomingInvoice() {
        super("invoiceNumber");
    }



    @Builder
    public IncomingInvoice(
            final String invoiceNumber,
            final String atPath,
            final Party buyer,
            final Party seller,
            final LocalDate invoiceDate,
            final LocalDate dueDate
            ){
        super("invoiceNumber");
        setInvoiceNumber(invoiceNumber);
        setApplicationTenancyPath(atPath);
        setBuyer(buyer);
        setSeller(seller);
        setInvoiceDate(invoiceDate);
        setDueDate(dueDate);
    }

    //region > compareTo, toString
    @Override
    public int compareTo(final IncomingInvoice other) {
        return org.apache.isis.applib.util.ObjectContracts.compare(this, other, "number");
    }

    @Override
    public String toString() {
        return org.apache.isis.applib.util.ObjectContracts.toString(this, "number");
    }
    //endregion

}
