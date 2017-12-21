package org.incode.platform.dom.communications.integtests.demo.dom.invoice;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.platform.dom.communications.integtests.demo.dom.demowithnotes.DemoObjectWithNotes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema="exampleDemoCommunications"
)
@javax.jdo.annotations.DatastoreIdentity(strategy= IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column = "version")
@DomainObject
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT )
@AllArgsConstructor
@Builder
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class DemoInvoice implements Comparable<DemoInvoice> {

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence="1", prepend = "Invoice #")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private String num;


    @javax.jdo.annotations.Column(allowsNull = "false", name = "customerId")
    @Title(sequence="2", prepend = " for ")
    @Property
    @Getter @Setter
    private DemoObjectWithNotes customer;




    @Override
    public String toString() {
        return ObjectContracts.toString(this, "num", "customer");
    }

    @Override
    public int compareTo(final DemoInvoice other) {
        return ObjectContracts.compare(this, other, "num", "customer");
    }


}
