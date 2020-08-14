package org.estatio.module.coda.dom.elements;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;

import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;

import lombok.Getter;
import lombok.Setter;

@DomainObject(
        objectType = "elements.InvoiceItemCodaElementsLink",
        autoCompleteRepository = CodaElementRepository.class)
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE, schema = "dbo")
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Queries({
        @Query(
                name="findUnique",
                value = "SELECT FROM org.estatio.module.coda.dom.elements.CodaElement "
                        + "WHERE incomingInvoiceItem == :incomingInvoiceItem ")
})
@javax.jdo.annotations.Uniques({
        @javax.jdo.annotations.Unique(
                name = "IncomingInvoiceItem_UNQ", members = { "incomingInvoiceItem" }),
})
public class InvoiceItemCodaElementsLink {

    public InvoiceItemCodaElementsLink(){}

    public InvoiceItemCodaElementsLink(
            final IncomingInvoiceItem incomingInvoiceItem,
            final CodaElement codaElement4,
            final CodaElement codaElement5){
        this.incomingInvoiceItem = incomingInvoiceItem;
        this.codaElement4 = codaElement4;
        this.codaElement5 = codaElement5;
    }

    @Getter @Setter @Column(allowsNull = "false", name = "incomingInvoiceItemId")
    private IncomingInvoiceItem incomingInvoiceItem;

    @Getter @Setter @Column(allowsNull = "true", name = "codaElement4Id")
    private CodaElement codaElement4;

    @Getter @Setter @Column(allowsNull = "true", name = "codaElement5Id")
    private CodaElement codaElement5;

}
