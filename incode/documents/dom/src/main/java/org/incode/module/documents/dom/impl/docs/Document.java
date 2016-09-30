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
package org.incode.module.documents.dom.impl.docs;

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

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.impl.applicability.Binder;
import org.incode.module.documents.dom.impl.types.DocumentType;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeDocuments",
        table = "Document"
)
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
        @Query(
                name = "findByCreatedAtAfter", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.impl.docs.Document "
                        + "WHERE :startDateTime <= createdAt  "
                        + "ORDER BY createdAt DESC "),
        @Query(
                name = "findByCreatedAtBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.impl.docs.Document "
                        + "WHERE :startDateTime <= createdAt  "
                        + "   && createdAt      <= :endDateTime "
                        + "ORDER BY createdAt DESC "),
})
@Indices({
    // none yet
})
@Uniques({
    // none yet
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
public class Document extends DocumentAbstract<Document> {

    //region > ui event classes
    public static class TitleUiEvent extends DocumentsModule.TitleUiEvent<Document> {}
    public static class IconUiEvent extends DocumentsModule.IconUiEvent<Document>{}
    public static class CssClassUiEvent extends DocumentsModule.CssClassUiEvent<Document>{}
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TitleSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.Document$TitleSubscriber";
        }

        @EventHandler
        @Subscribe
        public void on(Document.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTranslatableTitle(titleOf(ev.getSource()));
        }
        private TranslatableString titleOf(final DocumentAbstract document) {
            return TranslatableString.tr("[{type}] {name}",
                    "name", document.getName(),
                    "type", document.getType().getReference());
        }
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class IconSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.Document$IconSubscriber";
        }

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
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class CssClassSubscriber extends AbstractSubscriber {

        public String getId() {
            return "incodeDocuments.Document$CssClassSubscriber";
        }

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


    //region > constructor
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
            final Object domainObject,
            final String additionalTextIfAny) {

        final Binder.Binding binding = documentTemplate.newBinding(domainObject, additionalTextIfAny);
        final Object contentDataModel = binding.getDataModel();

        documentTemplate.renderContent(this, contentDataModel);
    }
    //endregion


    //region > setBlob, setClob, setTextData
    @Override
    void setBlob(Blob blob) {
        super.setBlob(blob);
        setState(DocumentState.RENDERED);
        setRenderedAt(clockService.nowAsDateTime());
    }

    @Override
    void setClob(Clob clob) {
        super.setClob(clob);
        setState(DocumentState.RENDERED);
        setRenderedAt(clockService.nowAsDateTime());
    }

    @Override
    void setTextData(String name, String mimeType, String text) {
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
            domainEvent = CreatedAtDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DateTime renderedAt;
    //endregion

    //region > state (property)
    public static class StateDomainEvent extends PropertyDomainEvent<DocumentState> { }
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = Document.StateDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DocumentState state;
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
        return !getSort().isExternal();
    }
    //endregion


    //region > asChars, asBytes (programmatic)
    @Programmatic
    public String asChars() {
        return getSort().asChars(this);
    }
    @Programmatic
    public byte[] asBytes() {
        return getSort().asBytes(this);
    }
    //endregion



    @Inject
    ClockService clockService;
}
