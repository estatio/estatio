package org.incode.module.document.dom.impl.docs;

import java.util.List;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Uniques;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.tablecol.TableColumnOrderService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;
import org.apache.isis.schema.utils.jaxbadapters.PersistentEntityAdapter;

import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.types.DocumentType;
import org.incode.module.document.dom.mixins.T_documents;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeDocuments"
)
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
        @Query(
                name = "findByCreatedAtAfter", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.Document "
                        + "WHERE :startDateTime <= createdAt  "
                        + "ORDER BY createdAt DESC "),
        @Query(
                name = "findByCreatedAtBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.Document "
                        + "WHERE :startDateTime <= createdAt  "
                        + "   && createdAt      <= :endDateTime "
                        + "ORDER BY createdAt DESC "),
        @Query(
                name = "findByTypeAndAtPathAndName", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.Document "
                        + "WHERE type   == :type  "
                        + "   && atPath == :atPath "
                        + "   && name   == :name "
                        + "ORDER BY createdAt DESC "),
        @Query(
                /*
                returns a maximum of 10, for archiving to minio (as controlled by Camel).
                We don't want to return more than that in order to avoid using too much memory
                or network bandwidth in any one call.

                Testing suggests that can archive about 80 a minute, ie this will be called
                8 times a minute by Camel.
                 */
                name = "findOldestBySortAndCreatedAtBefore", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.Document "
                        + "WHERE sort      == :sort "
                        + "   && createdAt <= :createdAtBefore "
                        + "ORDER BY createdAt ASC "
                        + "RANGE 0,10"),
        @Query(
                /*
                returns a maximum of 100, designed to be called once a minute (from Quartz).
                This will keep up with the rate of archiving determined by the above query.

                As for the above query for finding docs to be archived, again we don't
                want to return too many.  However, we can cope with a larger number because
                for these documents they are not going to be transferred across the network.
                There does need to be some limit though, in order to minimize the size of the
                db transaction log entry).
                */
                name = "findOldestWithPurgeableBlobsAndCreatedAtBefore", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.Document "
                        + "WHERE sort      == 'EXTERNAL_BLOB' "
                        + "   && blobBytes != null "
                        + "   && createdAt <= :createdAtBefore "
                        + "ORDER BY createdAt ASC "
                        + "RANGE 0,100"),
        @Query(
                // uses NOT IN
                name = "findWithNoPaperclips", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.Document "
                        + "WHERE !(SELECT p.document "
                        +           "FROM org.incode.module.document.dom.impl.paperclips.Paperclip p"
                        +        ").contains(this) "),
        @Query(
                // this version should be equivalent to 'findWithNoPaperclips', but fails with syntax error.  Might only be supported in DN 5.0
                name = "findWithNoPaperclips_doesnt_work", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.document.dom.impl.docs.Document "
                        + "WHERE (SELECT "
                        +           "FROM org.incode.module.document.dom.impl.paperclips.Paperclip p "
                        +           "WHERE p.document == this "
                        +        ").isEmpty() "),
})
@Indices({
    // none yet
})
@Uniques({
    // none yet
})
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        iconUiEvent = DocumentLike.IconUiEvent.class,
        bookmarking = BookmarkPolicy.AS_ROOT
)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
public class Document extends DocumentAbstract<Document> implements DocumentLike {

    public TranslatableString title() {
        return titleOf(this);
    }
    private TranslatableString titleOf(final Document document) {
        return TranslatableString.tr("[{type}] {name} {createdAt}",
                "name", document.getName(),
                "type", document.getType().getReference(),
                "createdAt", document.getCreatedAt().toString("(dd-MM-YYYY hh:mm)"));
    }


    //region > constructor
    Document() {
        // for unit testing only
    }

    public Document(
            final DocumentType type,
            final String atPath,
            final String documentName,
            final String mimeType,
            final DateTime createdAt) {
        super(type, atPath);
        setName(documentName);
        setMimeType(mimeType);
        this.createdAt = createdAt;
        this.state = DocumentState.NOT_RENDERED;
    }
    //endregion


    //region > render (programmatic)
    @Action(hidden = Where.EVERYWHERE) // so can invoke via BackgroundService
    public void render(
            final DocumentTemplate documentTemplate,
            final Object domainObject) {

        final Object rendererModel = documentTemplate.newRendererModel(domainObject);
        documentTemplate.renderContent(this, rendererModel);
    }
    //endregion


    //region > setBlob, setClob, setTextData
    @Override
    public void modifyBlob(Blob blob) {
        super.modifyBlob(blob);
        setState(DocumentState.RENDERED);
        setRenderedAt(clockService.nowAsDateTime());
    }

    @Override
    public void modifyClob(Clob clob) {
        super.modifyClob(clob);
        setState(DocumentState.RENDERED);
        setRenderedAt(clockService.nowAsDateTime());
    }

    @Programmatic
    @Override
    public void setTextData(String name, String mimeType, String text) {
        super.setTextData(name, mimeType, text);
        setState(DocumentState.RENDERED);
        setRenderedAt(clockService.nowAsDateTime());
    }

    //endregion

    //region > createdAt (property)
    public static class CreatedAtDomainEvent extends PropertyDomainEvent<LocalDateTime> { }
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = CreatedAtDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DateTime createdAt;
    //endregion

    //region > renderedAt (property)
    public static class RenderedAtDomainEvent extends PropertyDomainEvent<LocalDateTime> { }
    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(
            domainEvent = RenderedAtDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DateTime renderedAt;
    //endregion

    //region > state (property)
    public static class StateDomainEvent extends PropertyDomainEvent<DocumentState> { }
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = StateDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DocumentState state;
    //endregion

    //region > externalUrl (property)
    public static class ExternalUrlDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "true", length = ExternalUrlType.Meta.MAX_LEN)
    @Property(
            domainEvent = ExternalUrlDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            named = "External URL"
    )
    private String externalUrl;

    public boolean hideExternalUrl() {
        return !getSort().isExternal();
    }
    //endregion



    //region > injected services
    @Inject
    ClockService clockService;
    //endregion


    //region > types

    public static class ExternalUrlType {

        private ExternalUrlType() {}

        public static class Meta {

            public static final int MAX_LEN = 2000;

            private Meta() {}

        }

    }
    //endregion

    //region > mixins

    @Mixin
    public static class Document_attachments extends T_documents<Document> {
        public Document_attachments(final Document document) {
            super(document);
        }

        @DomainService(
                nature = NatureOfService.DOMAIN,
                menuOrder = "98" // needs to be < implementations provided by document module.
        )
        public static class TableColumnOrderServiceForPaperclipsAttachedToDocument implements
                TableColumnOrderService {

            @Override
            public List<String> orderParented(
                    final Object domainObject,
                    final String collectionId,
                    final Class<?> collectionType,
                    final List<String> propertyIds) {
                if (!Paperclip.class.isAssignableFrom(collectionType)) {
                    return null;
                }

                if (!(domainObject instanceof Document)) {
                    return null;
                }

                if("attachments".equals(collectionId)) {
                    final List<String> trimmedPropertyIds = Lists.newArrayList(propertyIds);
                    trimmedPropertyIds.remove("attachedTo");
                    return trimmedPropertyIds;
                }

                return null;
            }

            @Override
            public List<String> orderStandalone(final Class<?> collectionType, final List<String> propertyIds) {
                return null;
            }
        }
    }

    //endregion

}
