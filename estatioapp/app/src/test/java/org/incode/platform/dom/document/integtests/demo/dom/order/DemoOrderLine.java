package org.incode.platform.dom.document.integtests.demo.dom.order;

import java.math.BigDecimal;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "exampleDemoDocument"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy=VersionStrategy.VERSION_NUMBER,
        column="version")
@DomainObject()
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_CHILD
)
@AllArgsConstructor
@Builder
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class DemoOrderLine implements Comparable<DemoOrderLine> {


    @javax.jdo.annotations.Column(allowsNull="false")
    @Property(hidden = Where.REFERENCES_PARENT)
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private DemoOrder order;


    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence="1")
    @MemberOrder(sequence="2")
    @Getter @Setter
    private String description;


    @Getter @Setter
    @MemberOrder(sequence = "3")
    private int quantity;


    @javax.jdo.annotations.Column(allowsNull="true", scale=2)
    @javax.validation.constraints.Digits(integer=10, fraction=2)
    @MemberOrder(sequence = "4")
    @Getter @Setter
    private BigDecimal cost;



    @Override
    public int compareTo(DemoOrderLine other) {
        return Ordering.natural().onResultOf(DemoOrderLine::getDescription).compare(this, other);
    }


}
