package org.incode.module.document.dom.impl.docs;

import javax.activation.DataSource;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
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
import org.apache.isis.applib.services.factory.FactoryService;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.spec.AbstractSpecification2;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.incode.module.document.DocumentModule;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.types.AtPathType;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeDocuments"
)
@Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Indices({
        @Index(
                name = "DocumentAbstract_type_atPath_IDX",
                members = { "type", "atPath" }
        ),
        @Index(
                name = "DocumentAbstract_atPath_type_IDX",
                members = { "atPath", "type" }
        ),
        @Index(
                name = "DocumentAbstract_type_atPath_name_IDX",
                members = { "type", "atPath", "name" }
        ),
})
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        bookmarking = BookmarkPolicy.AS_ROOT
)
public abstract class DocumentAbstract<T extends DocumentAbstract> implements Comparable<T>, HasAtPath {

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends DocumentModule.PropertyDomainEvent<DocumentAbstract, T> { }
    public static abstract class CollectionDomainEvent<T> extends DocumentModule.CollectionDomainEvent<DocumentAbstract, T> { }
    public static abstract class ActionDomainEvent extends DocumentModule.ActionDomainEvent<DocumentAbstract> { }
    //endregion

    //region > constructors
    DocumentAbstract() {
        // for testing only
    }

    public DocumentAbstract(
            final DocumentType type,
            final String atPath) {
        this.type = type;
        this.atPath = atPath;
        this.sort = DocumentSort.EMPTY;
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
    @Column(allowsNull = "false", length = AtPathType.Meta.MAX_LEN)
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
    @Column(allowsNull = "false", length = NameType.Meta.MAX_LEN)
    @Property(
            domainEvent = NameDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String name;
    //endregion

    //region > mimeType (property)
    public static class MimeTypeDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = MimeTypeType.Meta.MAX_LEN)
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

        // TODO: guard shouldn't be necessary, but otherwise get exception (same as getClob()).
        if (hideBlob()) {
            return null;
        }

        return new Blob(getName(), getMimeType(), asBytes());
    }
    @Programmatic
    public void modifyBlob(Blob blob) {
        setName(blob.getName());
        setMimeType(blob.getMimeType().toString());
        setBlobBytes(blob.getBytes());
        setSort(DocumentSort.BLOB);
    }
    public boolean hideBlob() {
        return !getSort().isBytes();
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
        /*
        java.lang.IllegalArgumentException
        Cannot convert to characters
        org.incode.module.document.dom.impl.docs.DocumentSort#asChars(DocumentSort.java:120)
        org.incode.module.document.dom.impl.docs.DocumentAbstract#asChars(DocumentAbstract.java:255)
        org.incode.module.document.dom.impl.docs.DocumentAbstract#getClob(DocumentAbstract.java:208)
        sun.reflect.NativeMethodAccessorImpl#invoke0(NativeMethodAccessorImpl.java:-2)
        sun.reflect.NativeMethodAccessorImpl#invoke(NativeMethodAccessorImpl.java:62)
        sun.reflect.DelegatingMethodAccessorImpl#invoke(DelegatingMethodAccessorImpl.java:43)
        java.lang.reflect.Method#invoke(Method.java:498)
        org.apache.isis.core.commons.lang.MethodExtensions#invoke(MethodExtensions.java:53)
        org.apache.isis.core.commons.lang.MethodExtensions#invoke(MethodExtensions.java:47)
        org.apache.isis.core.metamodel.adapter.ObjectAdapter$InvokeUtils#invoke(ObjectAdapter.java:373)
        org.apache.isis.core.metamodel.facets.properties.accessor.PropertyAccessorFacetViaAccessor#getProperty(PropertyAccessorFacetViaAccessor.java:76)
        org.apache.isis.core.metamodel.specloader.specimpl.OneToOneAssociationDefault#get(OneToOneAssociationDefault.java:146)
        org.apache.isis.viewer.wicket.model.models.EntityModel#resetPropertyModels(EntityModel.java:406)
        org.apache.isis.viewer.wicket.ui.panels.FormExecutorDefault#executeAndProcessResults(FormExecutorDefault.java:157)
        org.apache.isis.viewer.wicket.ui.panels.PromptFormAbstract#onOkSubmittedOf(PromptFormAbstract.java:229)
         */
        // TODO: guard shouldn't be necessary, but otherwise get above exception
        if (hideClob()) {
            return null;
        }

        return new Clob(getName(), getMimeType(), asChars());
    }
    @Programmatic
    public void modifyClob(Clob clob) {
        setName(clob.getName());
        setMimeType(clob.getMimeType().toString());
        setClobChars(clob.getChars().toString());
        setSort(DocumentSort.CLOB);
    }

    public boolean hideClob() {
        return !getSort().isCharacters();
    }
    //endregion

    //region > text (persisted property)
    public static class TextDomainEvent extends PropertyDomainEvent<Clob> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull = "true", length = TextType.Meta.MAX_LEN)
    @Property(
            notPersisted = true, // exclude from auditing
            domainEvent = TextDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String text;

    @Programmatic
    public void setTextData(String name, String mimeType, String text) {
        setName(name);
        setMimeType(mimeType);
        setText(text);
        setSort(DocumentSort.TEXT);
    }

    public boolean hideText() {
        return getSort() != DocumentSort.TEXT;
    }
    //endregion


    //region > asDataSource, asChars, asBytes (programmatic)
    @Programmatic
    public DataSource asDataSource() {
        return getSort().asDataSource(this);
    }
    @Programmatic
    public String asChars() {
        return getClobChars() != null ? getClobChars() : getSort().asChars(this);
    }
    @Programmatic
    public byte[] asBytes() {
        return getBlobBytes() != null ? getBlobBytes() : getSort().asBytes(this);
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
    @Inject
    FactoryService factoryService;
    //endregion

    //region > types
    public static class NameType {

        private NameType() {}

        public static class Meta {

            public static final int MAX_LEN = 255;

            private Meta() {}

        }
    }

    public static class MimeTypeType {

        private MimeTypeType() {}

        public static class Meta {

            public static final int MAX_LEN = 255;

            private Meta() {}

            public static class Specification extends AbstractSpecification2<String> {
                @Override
                public TranslatableString satisfiesTranslatableSafely(final String mimeType) {
                    try {
                        new MimeType(mimeType);
                    } catch (MimeTypeParseException e) {
                        return TranslatableString.tr("Invalid mime type");
                    }
                    return null;
                }
            }
        }

    }

    public static class TextType {

        private TextType() {}

        public static class Meta {

            public static final int MAX_LEN = 4000; // long varchar

            private Meta() {}

        }

    }

    //endregion

}
