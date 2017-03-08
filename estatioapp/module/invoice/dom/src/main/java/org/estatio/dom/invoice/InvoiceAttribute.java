package org.estatio.dom.invoice;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.base.FragmentRenderService;

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
    private boolean derived;

    @Mixin(method="act")
    public static class _overrideDescription {
        private final Invoice invoice;
        public _overrideDescription(final Invoice invoice) {
            this.invoice = invoice;
        }
        @Action(semantics = SemanticsOf.IDEMPOTENT)
        @ActionLayout(contributed=Contributed.AS_ACTION)
        public Invoice act(
                @ParameterLayout(multiLine = 3, named = "PL description")
                final String preliminaryLetterDescription) {
            invoice.setPreliminaryLetterDescription(preliminaryLetterDescription);
            invoice.setPreliminaryLetterDescriptionOverridden(true);
            return invoice;
        }
        public boolean hideAct() {
            return invoice.isPreliminaryLetterDescriptionOverridden();
        }
        public String disableAct() {
            if (invoice.isImmutable()) {
                return "Invoice can't be changed";
            }
            return null;
        }
        public String default0Act() {
            return invoice.getPreliminaryLetterDescription();
        }
    }

    @Mixin(method="act")
    public static class _unoverrideDescription {
        private final Invoice invoice;
        public _unoverrideDescription(final Invoice invoice) {
            this.invoice = invoice;
        }
        @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
        @ActionLayout(contributed= Contributed.AS_ACTION)
        public Invoice act() {
            final String preliminaryLetterDescription = fragmentRenderService
                    .render(invoice, "preliminaryLetterDescription");
            invoice.setPreliminaryLetterDescription(preliminaryLetterDescription);
            invoice.setPreliminaryLetterDescriptionOverridden(false);
            return invoice;
        }
        public boolean hideAct() {
            return !invoice.isPreliminaryLetterDescriptionOverridden();
        }
        public String disableAct() {
            if (invoice.isImmutable()) {
                return "Invoice can't be changed";
            }
            return null;
        }

        @Inject
        FragmentRenderService fragmentRenderService;
    }

}
