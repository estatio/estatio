package org.estatio.dom.invoice;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainObject2;

import lombok.Getter;
import lombok.Setter;
import static org.incode.module.base.dom.types.DescriptionType.Meta.MAX_LEN;

@PersistenceCapable(identityType = IdentityType.DATASTORE ,schema = "dbo")
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version( strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@Queries({
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.invoice.InvoiceAttribute "
                        + "WHERE invoice == :invoice"),
        @Query(
                name = "findByInvoiceAndName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.invoice.InvoiceAttribute "
                        + "WHERE invoice == :invoice && "
                        + "name == :name")
})
@DomainObject()
public class InvoiceAttribute extends UdoDomainObject2<InvoiceAttribute> {

    public InvoiceAttribute() {
        super("invoice,name");
    }

    @Override public ApplicationTenancy getApplicationTenancy() {
        return invoice.getApplicationTenancy();
    }

    @Column(name = "invoiceId", allowsNull = "false")
    @Getter @Setter
    @Property(hidden = Where.PARENTED_TABLES)
    private Invoice invoice;

    @Column(allowsNull = "false")
    @Getter @Setter
    private InvoiceAttributeName name;

    @Column(allowsNull = "true", length = MAX_LEN)
    @Getter @Setter
    private String value;

    @Getter @Setter
    private boolean overridden;


}
