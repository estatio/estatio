package org.incode.platform.dom.docfragment.integtests.demo.dom.invoicewithatpath;

import java.io.IOException;

import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Ordering;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.module.docfragment.dom.api.DocFragmentService;
import org.incode.module.docfragment.dom.types.AtPathType;

import freemarker.template.TemplateException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "exampleDemo"
)
@javax.jdo.annotations.DatastoreIdentity(strategy= IdGeneratorStrategy.IDENTITY, column ="id")
@javax.jdo.annotations.Version(strategy= VersionStrategy.VERSION_NUMBER, column ="version")
@DomainObject
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT )
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class DemoInvoiceWithAtPath implements Comparable<DemoInvoiceWithAtPath> {

    @Builder
    public DemoInvoiceWithAtPath(final int num, final LocalDate dueBy, final int numDays, final String atPath) {
        this.num = num;
        this.dueBy = dueBy;
        this.numDays = numDays;
        this.atPath = atPath;
    }

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    @Title(sequence = "1", prepend = "Invoice #")
    private int num;


    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    private LocalDate dueBy;


    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    private int numDays;


    @javax.jdo.annotations.Column(allowsNull = "false", length = AtPathType.Meta.MAX_LEN)
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private String atPath;


    @Property(editing = Editing.DISABLED)
    @javax.jdo.annotations.Column(allowsNull = "true")
    @Getter @Setter
    private String rendered;


    @Action(semantics = SemanticsOf.IDEMPOTENT)
    public DemoInvoiceWithAtPath render(
            @ParameterLayout(named = "Fragment name")
            final String fragmentName) {
        final String rendered = doRender(fragmentName);
        setRendered(rendered);
        return this;
    }

    public String default0Render() {
        return "due";
    }
    

    private String doRender(final String name) {
        try {
            return docFragmentService.render(this, name);
        } catch (IOException | TemplateException e) {
            return "failed to render";
        }
    }




    @Override
    public String toString() {
        return ""+getNum();
    }

    @Override
    public int compareTo(final DemoInvoiceWithAtPath other) {
        return Ordering.natural().onResultOf(DemoInvoiceWithAtPath::getNum).compare(this, other);
    }

    /**
     *  required by {@link DemoInvoiceWithAtPathData#findUsing(ServiceRegistry2)}.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final DemoInvoiceWithAtPath that = (DemoInvoiceWithAtPath) o;

        return num == that.num;
    }

    @Override public int hashCode() {
        return num;
    }

    @Inject
    DocFragmentService docFragmentService;


}