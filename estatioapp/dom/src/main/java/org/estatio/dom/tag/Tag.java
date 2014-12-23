/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.tag;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;

import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Mandatory;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithNameGetter;

/**
 * Represents a general purpose mechanism for tagging any entity with a named
 * (string) value.
 * 
 * <p>
 * For example, we wish to tag <tt>LeaseUnit</tt>s with a <i>brand</i>. This is
 * modelled by <tt>LeaseUnit</tt> having a reference of type <tt>Tag</tt>. The
 * tag's {@link #getObjectType() object type} is set to the fully qualified
 * class name of <tt>LeaseUnit</tt>, and the tag's {@link #getName()} is set to
 * &quot;brand&quot;.
 */
@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER, 
        column = "version")
@javax.jdo.annotations.Uniques({
    @javax.jdo.annotations.Unique(
            name = "Tag_taggable_name_UNQ", members = { "taggable", "name" })
})
@Immutable
public class Tag 
        extends EstatioMutableObject<Tag> 
        implements WithNameGetter {

    public Tag() {
        super("taggable, name");
    }

    // //////////////////////////////////////

    public static final Function<Tag, String> GET_VALUE = new Function<Tag, String>() {
        public String apply(final Tag tag) {
            return tag != null ? tag.getValue() : null;
        }
    };

    // //////////////////////////////////////

    /**
     * Polymorphic association to (any implementation of) {@link Taggable}.
     */
    @javax.jdo.annotations.Persistent(
            extensions = {
                    @Extension(vendorName = "datanucleus",
                            key = "mapping-strategy",
                            value = "per-implementation") })
    private Taggable taggable;
    
    @Mandatory
    public Taggable getTaggable() {
        return taggable;
    }

    public void setTaggable(final Taggable taggable) {
        this.taggable = taggable;
    }

    // //////////////////////////////////////

    private String objectType;
    
    @javax.jdo.annotations.Column(allowsNull = "false", length=JdoColumnLength.FQCN)
    @Disabled
    public String getObjectType() {
        return objectType;
    }
    
    public void setObjectType(final String objectType) {
        this.objectType = objectType;
    }

    // //////////////////////////////////////

    private String name;

    /**
     * The name of this tag, for example <tt>Brand</tt> for a <tt>LeaseUnit</tt>
     * .
     * 
     * <p>
     * The combination of ({@link #getObjectType() objectType},
     * {@link #getName() name}) is unique.
     */
    @javax.jdo.annotations.Column(allowsNull = "false", length=JdoColumnLength.NAME)
    @Disabled
    public String getName() {
        return name;
    }

    public void setName(final String tagName) {
        this.name = tagName;
    }

    // //////////////////////////////////////


    private String value;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Title
    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }



}
