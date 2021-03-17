package org.incode.platform.dom.document.integtests.demo.dom.demowithurl;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema="exampleDemoDocument"
)
@javax.jdo.annotations.DatastoreIdentity(strategy= IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column = "version")
@DomainObject
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT)
@AllArgsConstructor
@Builder
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class DemoObjectWithUrl implements Comparable<DemoObjectWithUrl> {


    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence="1")
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private String name;


    @javax.jdo.annotations.Column(allowsNull="true")
    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    private String url;



    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(final DemoObjectWithUrl other) {
        return Ordering.natural().onResultOf(DemoObjectWithUrl::getName).compare(this, other);
    }


}
