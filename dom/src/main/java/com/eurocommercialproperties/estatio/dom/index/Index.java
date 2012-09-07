package com.eurocommercialproperties.estatio.dom.index;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.isis.applib.AbstractDomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Title;

@PersistenceCapable
public class Index extends AbstractDomainObject {

    // {{ Reference (property)
    private String reference;

    @Title(sequence="1", prepend="[", append="] ")
    @MemberOrder(sequence = "1")
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // }}

    // {{ Name (property)
    private String name;

    @Title(sequence="2")
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    // }}

    // {{ Values (Collection)
    @Persistent(mappedBy = "index")
    private Set<IndexValue> values = new LinkedHashSet<IndexValue>();

    @MemberOrder(sequence = "3")
    public Set<IndexValue> getValues() {
        return values;
    }

    public void setValues(final Set<IndexValue> values) {
        this.values = values;
    }
    // }}

}
