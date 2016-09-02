/*
 *  Copyright 2016 Dan Haywood
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
package org.incode.module.documents.dom.docs;

import javax.inject.Inject;
import javax.jdo.JDOHelper;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.types.DocumentType;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeDocuments",
        table = "DocumentAbstract"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@Indices({
        @Index(
                name = "DocumentAbstract_type_atPath_IDX",
                members = { "type", "atPath" }
        ),
        @Index(
                name = "DocumentAbstract_atPath_type_IDX",
                members = { "atPath", "type" }
        ),
})
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public abstract class DocumentAbstract<T extends DocumentAbstract> implements Comparable<T> {

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends DocumentsModule.PropertyDomainEvent<DocumentAbstract, T> { }
    public static abstract class CollectionDomainEvent<T> extends DocumentsModule.CollectionDomainEvent<DocumentAbstract, T> { }
    public static abstract class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<DocumentAbstract> { }
    //endregion

    //region > constructors
    public DocumentAbstract(
            final DocumentType type,
            final String atPath,
            final Blob blob) {
        this(type, atPath, blob.getName(), blob.getMimeType().toString(), DocumentSort.BLOB);
        this.blobBytes = blob.getBytes();
    }

    public DocumentAbstract(
            final DocumentType type,
            final String atPath,
            final String name,
            final String mimeType,
            final String text) {
        this(type, atPath, name, mimeType, DocumentSort.TEXT);
        this.text = text;
    }

    public DocumentAbstract(
            final DocumentType type,
            final String atPath,
            final Clob clob) {
        this(type, atPath, clob.getName(), clob.getMimeType().toString(), DocumentSort.CLOB);
        this.clobChars = clob.getChars().toString();
    }

    private DocumentAbstract(
            final DocumentType type,
            final String atPath,
            final String name,
            final String mimeType,
            final DocumentSort sort) {
        this.type = type;
        this.atPath = atPath;
        this.name = name;
        this.mimeType = mimeType;
        this.sort = sort;
    }
    //endregion


    //region > type (property)
    public static class TypeDomainEvent extends PropertyDomainEvent<DocumentType> { }
    @Getter @Setter
    @Column(allowsNull = "false", name = "typeId")
    @Property(
            domainEvent = TypeDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DocumentType type;
    //endregion

    //region > atPath (property)
    public static class AtPathDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = DocumentsModule.JdoColumnLength.AT_PATH)
    @Property(
            domainEvent = AtPathDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            named = "Application tenancy"
    )
    private String atPath;
    //endregion


    //region > name (property)
    public static class NameDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = DocumentsModule.JdoColumnLength.NAME)
    @Property(
            domainEvent = NameDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String name;
    //endregion

    //region > mimeType (property)
    public static class MimeTypeDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = DocumentsModule.JdoColumnLength.MIME_TYPE)
    @Property(
            domainEvent = MimeTypeDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String mimeType;
    //endregion


    //region > sort (property)
    public static class SortDomainEvent extends PropertyDomainEvent<DocumentSort> { }
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = SortDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DocumentSort sort;
    //endregion


    //region > blobBytes (persisted property, hidden)
    @Getter @Setter
    @javax.jdo.annotations.Persistent(defaultFetchGroup="false")
    @javax.jdo.annotations.Column(allowsNull = "true", name = "blob_bytes", jdbcType = "BLOB", sqlType = "BLOB")
    @Property(
            notPersisted = true, // exclude from auditing
            hidden = Where.EVERYWHERE
    )
    private byte[] blobBytes;
    //endregion

    //region > blob (derived property)
    public static class BlobDomainEvent extends PropertyDomainEvent<Blob> { }
    @javax.jdo.annotations.NotPersistent
    @Property(
            notPersisted = true,
            domainEvent = BlobDomainEvent.class,
            editing = Editing.DISABLED
    )
    public Blob getBlob() {
        return new Blob(getName(), getMimeType(), getBlobBytes());

    }
    public boolean hideBlob() {
        return getSort() != DocumentSort.BLOB;
    }
    //endregion

    //region > clobChars (persisted property, hidden)
    @Getter @Setter
    @javax.jdo.annotations.Persistent(defaultFetchGroup="false")
    @javax.jdo.annotations.Column(allowsNull = "true", name = "clob_chars", jdbcType = "CLOB", sqlType = "CLOB")
    @Property(
            notPersisted = true, // exclude from auditing
            hidden = Where.EVERYWHERE
    )
    private String clobChars;
    //endregion

    //region > clob (derived property)
    public static class ClobDomainEvent extends PropertyDomainEvent<Clob> { }
    @javax.jdo.annotations.NotPersistent
    @Property(
            notPersisted = true, // exclude from auditing
            domainEvent = ClobDomainEvent.class,
            editing = Editing.DISABLED
    )
    public Clob getClob() {
        return new Clob(getName(), getMimeType(), getClobChars());

    }
    public boolean hideClob() {
        return getSort() != DocumentSort.CLOB;
    }
    //endregion

    //region > text (persisted property)
    public static class TextDomainEvent extends PropertyDomainEvent<Clob> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "true", length = DocumentsModule.JdoColumnLength.TEXT)
    @Property(
            notPersisted = true, // exclude from auditing
            domainEvent = TextDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String text;

    public boolean hideText() {
        return getSort() != DocumentSort.TEXT;
    }
    //endregion


    //region > id (programmatic, for comparison)
    @Programmatic
    public String getId() {
        Object objectId = JDOHelper.getObjectId(this);
        if (objectId == null) {
            return "";
        }
        String objectIdStr = objectId.toString();
        final String id = objectIdStr.split("\\[OID\\]")[0];
        return id;
    }
    //endregion


    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "type", "name", "atPath", "sort");
    }

    @Override
    public int compareTo(final DocumentAbstract other) {
        return ObjectContracts.compare(this, other, "type", "name", "atPath", "sort", "id");
    }
    //endregion

    //region > injected services
    @Inject
    DocumentRepository documentRepository;
    //endregion

}
