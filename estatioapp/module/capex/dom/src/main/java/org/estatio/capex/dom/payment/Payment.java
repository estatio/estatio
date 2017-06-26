package org.estatio.capex.dom.payment;

import java.math.BigDecimal;

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
import org.apache.isis.applib.annotation.Editing;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.capex.dom.invoice.IncomingInvoice;
import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.invoice.PaymentMethod;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType = IdentityType.DATASTORE,
        schema = "dbo",
        table = "Payment"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.IDENTITY,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByInvoice", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.capex.dom.payment.Payment "
                        + "WHERE invoice == :invoice ")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "payment.Payment"
)
public class Payment extends UdoDomainObject2<Payment> {

    public Payment() {
        super("amount, invoice");
    }

    public Payment(final BigDecimal amount, final IncomingInvoice invoice, final PaymentMethod paymentMethod){
        this();
        this.amount = amount;
        this.invoice = invoice;
        this.paymentMethod = paymentMethod;
    }

    @Column(allowsNull = "false", scale = 2)
    @Getter @Setter
    private BigDecimal amount;

    @Column(allowsNull = "false", name = "invoiceId")
    @Getter @Setter
    private IncomingInvoice invoice;

    @Column(allowsNull = "false")
    @Getter @Setter
    private PaymentMethod paymentMethod;

    @Override
    public ApplicationTenancy getApplicationTenancy() {
        return invoice.getApplicationTenancy();
    }

}
