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

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.JDOHelper;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;

import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.links.PaperclipRepository;
import org.incode.module.documents.dom.rendering.Renderer;
import org.incode.module.documents.dom.rendering.RendererFromBytesToBytes;
import org.incode.module.documents.dom.rendering.RendererFromBytesToChars;
import org.incode.module.documents.dom.rendering.RendererFromBytesToUrl;
import org.incode.module.documents.dom.rendering.RendererFromCharsToBytes;
import org.incode.module.documents.dom.rendering.RendererFromCharsToChars;
import org.incode.module.documents.dom.rendering.RendererFromCharsToUrl;
import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.services.ClassService;
import org.incode.module.documents.dom.types.DocumentType;

import lombok.Getter;
import lombok.Setter;
import static org.incode.module.documents.dom.docs.OutputType.TO_URL;

@PersistenceCapable(
        identityType= IdentityType.DATASTORE,
        schema = "incodeDocuments",
        table = "DocumentTemplate"
)
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
        @javax.jdo.annotations.Query(
                name = "findByTypeAndAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.docs.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "   && atPathCopy == :atPath "
                        + "ORDER BY date DESC"
        ),
        @javax.jdo.annotations.Query(
                name = "findByTypeAndApplicableToAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.docs.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "   && :atPath.startsWith(atPathCopy) "
                        + "ORDER BY atPathCopy DESC, date DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findByTypeAndApplicableToAtPathAndCurrent", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.docs.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "   && :atPath.startsWith(atPathCopy) "
                        + "   && (date == null || date <= :now) "
                        + "ORDER BY atPathCopy DESC, date DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findByType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.docs.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "ORDER BY atPathCopy DESC, date DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findByApplicableToAtPathAndCurrent", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.docs.DocumentTemplate "
                        + "   && :atPath.startsWith(atPathCopy) "
                        + "   && (date == null || date <= :now) "
                        + "ORDER BY atPathCopy DESC, typeCopy, date DESC "
        )
})
@Uniques({
        @Unique(
                name = "DocumentTemplate_type_atPath_date_IDX",
                members = { "typeCopy", "atPathCopy", "date" }
        ),
})
@Indices({
        @Index(
                name = "DocumentTemplate_atPath_date_IDX",
                members = { "atPathCopy", "date" }
        ),
        @Index(
                name = "DocumentTemplate_type_date_IDX",
                members = { "typeCopy", "date" }
        ),
})
@DomainObject(
        objectType = "incodeDocuments.DocumentTemplate",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        titleUiEvent = DocumentTemplate.TitleUiEvent.class,
        iconUiEvent = DocumentTemplate.IconUiEvent.class,
        cssClassUiEvent = DocumentTemplate.CssClassUiEvent.class,
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class DocumentTemplate extends DocumentAbstract<DocumentTemplate> {

    //region > ui event classes
    public static class TitleUiEvent extends DocumentsModule.TitleUiEvent<DocumentTemplate>{}
    public static class IconUiEvent extends DocumentsModule.IconUiEvent<DocumentTemplate>{}
    public static class CssClassUiEvent extends DocumentsModule.CssClassUiEvent<DocumentTemplate>{}
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService
    public static class TitleSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(DocumentTemplate.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTranslatableTitle(titleOf(ev.getSource()));
        }
        private TranslatableString titleOf(final DocumentTemplate template) {
            if(template.getDate() != null) {
                return TranslatableString.tr("[{type}] {name}, (from {date})",
                        "name", template.getName(),
                        "type", template.getType().getReference(),
                        "date", template.getDate());
            } else {
                return TranslatableString.tr("[{type}] {name}",
                        "name", template.getName(),
                        "type", template.getType().getReference());
            }
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
        public void on(DocumentTemplate.IconUiEvent ev) {
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
        public void on(DocumentTemplate.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion


    //region > constructor
    public DocumentTemplate(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final Blob blob,
            final RenderingStrategy renderingStrategy,
            final String dataModelClassName) {
        super(type, atPath, blob);
        init(type, date, atPath, renderingStrategy, dataModelClassName);
    }

    public DocumentTemplate(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final String name,
            final String mimeType,
            final String text,
            final RenderingStrategy renderingStrategy,
            final String dataModelClassName) {
        super(type, atPath, name, mimeType, text);
        init(type, date, atPath, renderingStrategy, dataModelClassName);
    }

    public DocumentTemplate(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final Clob clob,
            final RenderingStrategy renderingStrategy,
            final String dataModelClassName) {
        super(type, atPath, clob);
        init(type, date, atPath, renderingStrategy, dataModelClassName);
    }

    private void init(
            final DocumentType type,
            final LocalDate date,
            final String atPath,
            final RenderingStrategy renderingStrategy,
            final String dataModelClassName) {
        this.typeCopy = type;
        this.atPathCopy = atPath;
        this.date = date;
        this.renderingStrategy = renderingStrategy;
        this.dataModelClassName = dataModelClassName;
    }
    //endregion


    //region > typeCopy (derived property, persisted)
    /**
     * Copy of {@link #getType()}, for query purposes only.
     */
    @Getter @Setter
    @Column(allowsNull = "false", name = "typeId")
    @Property(
            notPersisted = true, // ignore for auditing
            hidden = Where.EVERYWHERE
    )
    private DocumentType typeCopy;
    //endregion

    //region > atPathCopy (derived property, persisted)
    /**
     * Copy of {@link #getAtPath()}, for query purposes only.
     */
    @Getter @Setter
    @Column(allowsNull = "false", length = DocumentsModule.JdoColumnLength.AT_PATH)
    @Property(
            notPersisted = true, // ignore for auditing
            hidden = Where.EVERYWHERE
    )
    private String atPathCopy;
    //endregion

    //region > date (property)
    public static class DateDomainEvent extends DocumentTemplate.PropertyDomainEvent<LocalDate> { }
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = DateDomainEvent.class,
            editing = Editing.DISABLED
    )
    private LocalDate date;
    //endregion


    //region > dataModelClassName (property)
    public static class DataModelClassNameDomainEvent extends DocumentTemplate.PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = DocumentsModule.JdoColumnLength.FQCN)
    @Property(
            domainEvent = DataModelClassNameDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String dataModelClassName;
    //endregion

    //region > renderStrategy (property)
    public static class RenderingStrategyDomainEvent extends PropertyDomainEvent<RenderingStrategy> { }
    @Getter @Setter
    @Column(allowsNull = "false", name = "renderStrategyId")
    @Property(
            domainEvent = RenderingStrategyDomainEvent.class,
            editing = Editing.DISABLED
    )
    private RenderingStrategy renderingStrategy;
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

    //region > instantiateDataModel (programmatic)

    @Programmatic
    public Object instantiateDataModel() {
        final String dataModelClassName = getDataModelClassName();
        final Object dataModel = classService.instantiate(dataModelClassName);
        serviceRegistry2.injectServicesInto(dataModel);
        return dataModel;
    }

    //endregion


    //region > preview (programmatic)



    @Programmatic
    public List<OutputType> getOutputTypes() {
        final List<OutputType> outputTypes = Lists.newArrayList();
        if(getRenderingStrategy().isRendersToBytes()) {
            outputTypes.add(OutputType.TO_BYTES);
        }
        if(getRenderingStrategy().isRendersToBytes()) {
            outputTypes.add(OutputType.TO_CHARS);
        }
        if(getRenderingStrategy().isRendersToUrl()) {
            outputTypes.add(TO_URL);
        }
        return outputTypes;
    }

    /**
     * @param outputType - as returned from {@link #getOutputTypes()}.
     */
    @Programmatic
    public Object preview(final Object dataModel, final String documentName, final OutputType outputType)
            throws IOException {
        final List<OutputType> outputTypes = getOutputTypes();
        if(!outputTypes.contains(outputType)) {
            throw new IllegalArgumentException("output type '" + outputType + "' not supported by rendering strategy");
        }
        final Renderer renderer = getRenderingStrategy().instantiateRenderer();
        final DocumentNature inputNature = getRenderingStrategy().getInputNature();
        switch (outputType) {
        case TO_BYTES:
            switch (inputNature){
            case BYTES:
                return ((RendererFromBytesToBytes) renderer).renderBytesToBytes(
                        getType(), getAtPath(), getVersion(),
                        asBytes(), dataModel, documentName);
            case CHARACTERS:
                return ((RendererFromCharsToBytes) renderer).renderCharsToBytes(
                        getType(), getAtPath(), getVersion(),
                        asChars(), dataModel, documentName);
            default:
                // shouldn't happen, above switch statement is complete
                throw new IllegalArgumentException("Unknown documentNature '" + inputNature + "'");
            }
        case TO_CHARS:
            switch (inputNature){
            case BYTES:
                return ((RendererFromBytesToChars) renderer).renderBytesToChars(
                        getType(), getAtPath(), getVersion(),
                        asBytes(), dataModel, documentName);
            case CHARACTERS:
                return ((RendererFromCharsToChars) renderer).renderCharsToChars(
                        getType(), getAtPath(), getVersion(),
                        asChars(), dataModel, documentName);
            default:
                // shouldn't happen, above switch statement is complete
                throw new IllegalArgumentException("Unknown documentNature '" + inputNature + "'");
            }
        case TO_URL:
            switch (inputNature){
            case BYTES:
                return ((RendererFromBytesToUrl) renderer).renderBytesToUrl(
                        getType(), getAtPath(), getVersion(),
                        asBytes(), dataModel, documentName);
            case CHARACTERS:
                return ((RendererFromCharsToUrl) renderer).renderCharsToUrl(
                        getType(), getAtPath(), getVersion(),
                        asChars(), dataModel, documentName);
            default:
                // shouldn't happen, above switch statement is complete
                throw new IllegalArgumentException("Unknown documentNature '" + inputNature + "'");
            }
        default:
            // shouldn't happen, above switch statement is complete
            throw new IllegalArgumentException("Unknown outputType '" + outputType + "'");
        }
    }

    //endregion

    //region > render (programmatic)

    @Programmatic
    public DocumentAbstract render(final Object dataModel, final String documentName) {
        final RenderingStrategy renderingStrategy = getRenderingStrategy();

        final DocumentNature outputNature = renderingStrategy.getOutputNature();

        try {
            final DateTime createdAt = clockService.nowAsDateTime();

            // reuse the preview logic
            final DocumentType documentType = this.getType();
            final Object rendered = preview(dataModel, documentName, outputNature.getOutputType());

            switch(outputNature) {
            case BYTES:
                final byte[] renderedBytes = (byte[]) rendered;
                final Blob blob = new Blob (documentName, getMimeType(), renderedBytes);
                return documentRepository.createBlob(documentType, getAtPath(), blob, createdAt);
            case CHARACTERS:
                String renderedChars = (String) rendered;
                if(renderedChars.length() <= DocumentsModule.JdoColumnLength.TEXT) {
                    return documentRepository.createText(
                            documentType, getAtPath(), documentName, getMimeType(), renderedChars, createdAt);
                } else {
                    final Clob clob = new Clob (documentName, getMimeType(), renderedChars);
                    return documentRepository.createClob(documentType, getAtPath(), clob, createdAt);
                }
            default:
                // shouldn't happen, the above switch statement is complete
                throw new IllegalStateException(String.format("DocumentNature '%s' not recognized", outputNature));
            }
        } catch (IOException e) {
            throw new ApplicationException("Unable to render document template", e);
        }
    }

    //endregion



    //region > getVersion (programmatic)
    @Programmatic
    private long getVersion() {
        return (Long)JDOHelper.getVersion(this);
    }

    //endregion



    //region > injected services
    @Inject
    PaperclipRepository paperclipRepository;
    @Inject
    DocumentTemplateRepository documentTemplateRepository;
    @Inject
    QueryResultsCache queryResultsCache;
    @Inject
    ClassService classService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
    @Inject
    private ClockService clockService;
    //endregion

}
