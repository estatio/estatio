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
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.title.TitleService;
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
        table = "Document"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
/*
        @javax.jdo.annotations.Query(
                name = "findByXxx", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.Document "
                        + "WHERE id == :id "
)
*/
})
@Indices({
        @Index(
                name = "Document_type_atPath_IDX",
                members = { "type", "atPath" }
        ),
        @Index(
                name = "Document_atPath_type_IDX",
                members = { "atPath", "type" }
        ),
})
@DomainObject(
        objectType = "incodeDocuments.Document",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        titleUiEvent = Document.TitleUiEvent.class,
        iconUiEvent = Document.IconUiEvent.class,
        cssClassUiEvent = Document.CssClassUiEvent.class,
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class Document<T extends Document> implements Comparable<T> {

    //region > ui event classes
    public static class TitleUiEvent extends DocumentsModule.TitleUiEvent<Document> {}
    public static class IconUiEvent extends DocumentsModule.IconUiEvent<Document>{}
    public static class CssClassUiEvent extends DocumentsModule.CssClassUiEvent<Document>{}
    //endregion

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends DocumentsModule.PropertyDomainEvent<Document, T> { }
    public static abstract class CollectionDomainEvent<T> extends DocumentsModule.CollectionDomainEvent<Document, T> { }
    public static abstract class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<Document> { }
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService
    public static class TitleSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Document.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTranslatableTitle(titleOf(ev.getSource()));
        }
        private TranslatableString titleOf(final Document document) {
            return TranslatableString.tr("{name} ({type})",
                    "name", document.getName(),
                    "type", document.getType().getReference());
        }
        @Inject
        TitleService titleService;
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService
    public static class IconSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Document.IconUiEvent ev) {
            if(ev.getIconName() != null) {
                return;
            }
            ev.setIconName("");
        }
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService
    public static class CssClassSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Document.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion


    //region > constructors
    public Document(final DocumentType type, final String atPath, final Blob blob) {
        this(type, atPath, blob.getName(), blob.getMimeType().toString(), DocumentSort.BLOB);
        this.blobBytes = blob.getBytes();
    }

    public Document(
            final DocumentType type,
            final String atPath,
            final String name,
            final String mimeType,
            final String text) {
        this(type, atPath, name, mimeType, DocumentSort.TEXT);
        this.text = text;
    }
    public Document(final DocumentType type, final String atPath, final Clob clob) {
        this(type, atPath, clob.getName(), clob.getMimeType().toString(), DocumentSort.CLOB);
        this.clobChars = clob.getChars().toString();
    }

    private Document(
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


    //region > externalUrl (property)
    public static class ExternalUrlDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "true", length = DocumentsModule.JdoColumnLength.EXTERNAL_URL)
    @Property(
            domainEvent = ExternalUrlDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            named = "External URL"
    )
    private String externalUrl;

    public boolean hideExternalUrl() {
        return getSort() != DocumentSort.EXTERNAL_BLOB;
    }
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


    //region > text (persisted property)
    public static class TextDomainEvent extends PropertyDomainEvent<Clob> { }
    @Getter @Setter
    @javax.jdo.annotations.Persistent(defaultFetchGroup="false")
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


    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "type", "name", "atPath", "sort", "externalUrl");
    }

    @Override
    public int compareTo(final Document other) {
        return ObjectContracts.compare(this, other, "type", "name", "atPath", "sort", "externalUrl");
    }
    //endregion

    //region > injected services
    @Inject
    DocumentRepository documentRepository;
    //endregion

}
