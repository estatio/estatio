package org.estatio.module.lease.app;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntitiesAdapter;

import org.estatio.module.lease.dom.Lease;

@XmlRootElement(name = "list")
@XmlType(
        propOrder = {
                "title",
                "leases"
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@DomainObject(
        objectType = "lease.ListOfLeases",
        editing = Editing.DISABLED,
        nature = Nature.VIEW_MODEL
)
public class ListOfLeases {

    //region > constructors
    public ListOfLeases() {
    }
    public ListOfLeases(
            final String title,
            final List<Lease> leases) {
        this.title = title;
        this.leases.addAll(leases);
    }
    //endregion

    //region > title
    private String title;
    public String title() {
        return title;
    }
    //endregion

    @XmlJavaTypeAdapter(PersistentEntitiesAdapter.class)
    private List<Lease> leases = Lists.newArrayList();

    @Collection()
    @CollectionLayout(defaultView = "table")
    public List<Lease> getLeases() {
        return leases;
    }
    public void setLeases(final List<Lease> leases) {
        this.leases = leases;
    }
}
