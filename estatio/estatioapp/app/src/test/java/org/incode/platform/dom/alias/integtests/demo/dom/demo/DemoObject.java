package org.incode.platform.dom.alias.integtests.demo.dom.demo;

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
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema="exampleDemoAlias"
)
@javax.jdo.annotations.DatastoreIdentity(strategy= IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Version(strategy=VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.platform.dom.alias.integtests.demo.dom.demo.DemoObject "
                        + "WHERE name.indexOf(:name) >= 0 ")
})

@DomainObject(editing = Editing.DISABLED )
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_ROOT )
@AllArgsConstructor
@Builder
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class DemoObject implements Comparable<DemoObject> {


    @javax.jdo.annotations.Column(allowsNull="false")
    @Title(sequence="1")
    @Getter @Setter
    private String name;


    @Override
    public String toString() {
        return ObjectContracts.toString(this, "name");
    }

    @Override
    public int compareTo(final DemoObject other) {
        return Ordering.natural().onResultOf(DemoObject::getName).compare(this, other);
    }


    // required by DemoObjectData#findUsing
    @Override
    public boolean equals(final Object o) {
        return ObjectContracts.equals(this,o, "name");
    }

    @Override
    public int hashCode() {
        return ObjectContracts.hashCode(this, "name");
    }
}
