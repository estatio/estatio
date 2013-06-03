package org.estatio.dom.tag;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.bookmark.Bookmark;

import org.estatio.dom.EstatioRefDataObject;


/**
 * Represents a general purpose mechanism for tagging any entity with a named (string) value.
 * 
 * <p>
 * For example, we wish to tag <tt>LeaseUnit</tt>s with a <i>brand</i>.  This is modelled
 * by <tt>LeaseUnit</tt> having a reference of type <tt>Tag</tt>.  The tag's {@link #objectType} is
 * set to the fully qualified class name of <tt>LeaseUnit</tt>, and the tag's {@link #getName()} is set to &quot;brand&quot;.
 */
@javax.jdo.annotations.PersistenceCapable
@javax.jdo.annotations.Unique(name="tag_bookmark_and_name", members={"objectType","name","objectIdentifier"})
@Immutable
public class Tag extends EstatioRefDataObject implements Comparable<Tag> {

    public static final Function<Tag, String> GET_VALUE = new Function<Tag, String>() {
        public String apply(Tag tag) {
            return tag.getValue();
        }
    };
    
    
    // {{ objectType (property)
    private String objectType;

    /**
     * The {@link Bookmark#getObjectType() object type} (either the class name or a unique alias of it) 
     * of the object to which this {@link Tag} belongs. 
     */
    @MemberOrder(sequence = "1")
    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }
    // }}

    
    // {{ Name (property)
    private String name;

    /**
     * The name of this tag, for example <tt>Brand</tt> for a <tt>LeaseUnit</tt>.
     * 
     * <p>
     * The combination of ({@link #getObjectType() objectType}, {@link #getName() name})
     * is unique.
     */
    @Disabled
    @MemberOrder(sequence = "2")
    public String getName() {
        return name;
    }

    public void setName(final String tagName) {
        this.name = tagName;
    }
    // }}


    // {{ ObjectIdentifier (property)
    private String objectIdentifier;

    /**
     * The {@link Bookmark#getIdentifier() identifier} to the object that has this
     * tag.
     * 
     * <p>
     * The ({@link #getObjectType() objectType}, {@link #getObjectIdentifier() identifier})
     * can be used to recreate a {@link Bookmark}, if required.
     */
    @MemberOrder(sequence = "3")
    public String getObjectIdentifier() {
        return objectIdentifier;
    }

    public void setObjectIdentifier(final String bookmark) {
        this.objectIdentifier = bookmark;
    }
    // }}



    
    // {{ Value (property)
    private String value;

    @Title
    @MemberOrder(sequence = "1")
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }
    // }}

    
    @Programmatic
    public Bookmark asBookmark() {
        return new Bookmark(getObjectType(), getObjectIdentifier());
    }


    // {{ Comparable impl
    @Override
    public int compareTo(Tag other) {
        return ORDERING_BY_OBJECT_TYPE.compound(ORDERING_BY_NAME).compare(this, other);
    }
    // }}

    public static Ordering<Tag> ORDERING_BY_OBJECT_TYPE = new Ordering<Tag>() {
        public int compare(Tag p, Tag q) {
            return Ordering.natural().nullsFirst().compare(p.getObjectType(), q.getObjectType());
        }
    };
    public static Ordering<Tag> ORDERING_BY_NAME = new Ordering<Tag>() {
        public int compare(Tag p, Tag q) {
            return Ordering.natural().nullsFirst().compare(p.getName(), q.getName());
        }
    };


    
    
}
