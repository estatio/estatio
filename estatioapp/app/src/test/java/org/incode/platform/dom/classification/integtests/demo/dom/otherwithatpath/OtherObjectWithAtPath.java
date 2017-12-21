package org.incode.platform.dom.classification.integtests.demo.dom.otherwithatpath;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema="exampleDemoClassification"
)
@javax.jdo.annotations.DatastoreIdentity(strategy= IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column = "version")
@DomainObject(editing = Editing.DISABLED )
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT )
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class OtherObjectWithAtPath implements Comparable<OtherObjectWithAtPath> {

    @Builder
    public OtherObjectWithAtPath(final String name, final String atPath) {
        this.name = name;
        this.atPath = atPath;
    }

    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence="1")
    @Getter @Setter
    private String name;


    @javax.jdo.annotations.Column(allowsNull="false")
    @Getter @Setter
    private String atPath;

    public String getAtPath() {
        return atPath;
    }



    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(final OtherObjectWithAtPath other) {
        return Ordering.natural().onResultOf(OtherObjectWithAtPath::getName).compare(this, other);
    }


}
