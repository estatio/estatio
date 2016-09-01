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
package org.incode.module.documents.dom.templates;

import java.util.SortedSet;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Uniques;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.docs.Document;
import org.incode.module.documents.dom.rendering.Renderer;
import org.incode.module.documents.dom.rendering.RenderingStrategy;
import org.incode.module.documents.dom.services.ClassService;
import org.incode.module.documents.dom.types.DocumentType;

import org.estatio.dom.WithIntervalContiguous;
import org.estatio.dom.valuetypes.LocalDateInterval;

import lombok.Getter;
import lombok.Setter;

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
                        + "FROM org.incode.module.documents.dom.templates.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "   && :atPath.startsWith(atPathCopy) "
                        + "ORDER BY atPathCopy DESC, startDate DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findCurrentByTypeAndAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.templates.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "   && :atPath.startsWith(atPathCopy) "
                        + "   && (startDate == null || startDate <= :now) "
                        + "   && (endDate == null   || endDate   > :now) "
                        + "ORDER BY atPathCopy DESC, startDate DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findCurrentByType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.templates.DocumentTemplate "
                        + "WHERE typeCopy   == :type "
                        + "   && (startDate == null || startDate <= :now) "
                        + "   && (endDate == null   || endDate   > :now) "
                        + "ORDER BY atPathCopy DESC, startDate DESC "
        ),
        @javax.jdo.annotations.Query(
                name = "findCurrentByAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.templates.DocumentTemplate "
                        + "   && :atPath.startsWith(atPathCopy) "
                        + "   && (startDate == null || startDate <= :now) "
                        + "   && (endDate == null   || endDate > :now) "
                        + "ORDER BY atPathCopy DESC, typeCopy, startDate DESC "
        )
})
@Uniques({
    // none yet...
})
@Indices({
        @Index(
                name = "DocumentTemplate_type_atPath_dates_IDX",
                members = { "typeCopy", "atPathCopy", "startDate", "endDate" }
        ),
        @Index(
                name = "DocumentTemplate_atPath_dates_IDX",
                members = { "atPathCopy", "startDate", "endDate" }
        ),
        @Index(
                name = "DocumentTemplate_type_dates_IDX",
                members = { "typeCopy", "startDate", "endDate" }
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
public class DocumentTemplate extends Document<DocumentTemplate> implements WithIntervalContiguous<DocumentTemplate> {

    //region > ui event classes
    public static class TitleUiEvent extends DocumentsModule.TitleUiEvent<DocumentTemplate>{}
    public static class IconUiEvent extends DocumentsModule.IconUiEvent<DocumentTemplate>{}
    public static class CssClassUiEvent extends DocumentsModule.CssClassUiEvent<DocumentTemplate>{}
    //endregion

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends DocumentsModule.PropertyDomainEvent<DocumentTemplate, T> { }
    public static abstract class CollectionDomainEvent<T> extends DocumentsModule.CollectionDomainEvent<DocumentTemplate, T> { }
    public static abstract class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<DocumentTemplate> { }
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
            if(template.getStartDate() != null) {
                if(template.getEndDate() != null) {
                    return TranslatableString.tr("{name} ({type}, {startDate -> {endDate})",
                            "name", template.getName(),
                            "type", template.getType().getReference(),
                            "startDate", template.getStartDate(),
                            "endDate", template.getEndDate());
                } else {
                    return TranslatableString.tr("{name} ({type}, {startDate} to date)",
                            "name", template.getName(),
                            "type", template.getType().getReference(),
                            "startDate", template.getEndDate());
                }
            } else {
                if(template.getEndDate() != null) {
                    return TranslatableString.tr("{name} ({type}, to {endDate})",
                            "name", template.getName(),
                            "type", template.getType().getReference(),
                            "endDate", template.getEndDate());
                } else {
                    return TranslatableString.tr("{name} ({type})",
                            "name", template.getName(),
                            "type", template.getType().getReference());
                }
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
            final String atPath,
            final Blob blob,
            final RenderingStrategy renderingStrategy,
            final String dataModelClassName) {
        super(type, atPath, blob);
        init(type, atPath, renderingStrategy, dataModelClassName);
    }

    public DocumentTemplate(
            final DocumentType type,
            final String atPath,
            final String name,
            final String mimeType,
            final String text,
            final RenderingStrategy renderingStrategy,
            final String dataModelClassName) {
        super(type, atPath, name, mimeType, text);
        init(type, atPath, renderingStrategy, dataModelClassName);
    }

    public DocumentTemplate(
            final DocumentType type,
            final String atPath,
            final Clob clob,
            final RenderingStrategy renderingStrategy,
            final String dataModelClassName) {
        super(type, atPath, clob);
        init(type, atPath, renderingStrategy, dataModelClassName);
    }

    private void init(
            final DocumentType type,
            final String atPath,
            final RenderingStrategy renderingStrategy,
            final String dataModelClassName) {
        this.typeCopy = type;
        this.atPathCopy = atPath;
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

    //region > render (programmatic)

    @Programmatic
    public Document render(final Object dataModel, final String documentName) {
        final Renderer renderer = getRenderingStrategy().instantiateRenderer();
        return renderer.render(this, dataModel, documentName);
    }

    //endregion



    //region > startDate (property)
    public static class StartDateDomainEvent extends DocumentTemplate.PropertyDomainEvent<LocalDate> { }
    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(
            domainEvent = StartDateDomainEvent.class,
            editing = Editing.DISABLED
    )
    private LocalDate startDate;
    //endregion

    //region > endDate (property)
    public static class EndDateDomainEvent extends DocumentTemplate.PropertyDomainEvent<LocalDate> { }
    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(
            domainEvent = EndDateDomainEvent.class,
            editing = Editing.DISABLED
    )
    private LocalDate endDate;
    //endregion

    //region > WithIntervalContiguous impl

    // for some reason (perhaps because this class has no no-arg constructor?) this field is not populated when
    // object is rehydrated.  Therefore just recreate in #getHelper() each time...
    // private final WithIntervalContiguous.Helper<DocumentTemplate> intervalHelper = new WithIntervalContiguous.Helper<>(this);

    WithIntervalContiguous.Helper<DocumentTemplate> getHelper() {
        return new WithIntervalContiguous.Helper<>(this);
    }

    @Override
    public LocalDateInterval getInterval() {
        return new LocalDateInterval(getStartDate(), getEndDate());
    }

    @Override
    public LocalDateInterval getEffectiveInterval() {
        return getInterval();
    }

    @Override
    public boolean isCurrent() {
        return false;
    }

    @Override
    public DocumentTemplate changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return getHelper().changeDates(startDate, endDate);
    }

    @Override
    public LocalDate default0ChangeDates() {
        return getHelper().default0ChangeDates();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return getHelper().default1ChangeDates();
    }

    @Override
    public String validateChangeDates(LocalDate startDate, LocalDate endDate) {
        return getHelper().validateChangeDates(startDate, endDate);
    }


    @Property(hidden = Where.ALL_TABLES, editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @Override
    public DocumentTemplate getPredecessor() {
        return getHelper().getPredecessor(getTimeline(), com.google.common.base.Predicates.alwaysTrue());
    }

    @Property(hidden = Where.ALL_TABLES, editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @Override
    public DocumentTemplate getSuccessor() {
        return getHelper().getSuccessor(getTimeline(), com.google.common.base.Predicates.alwaysTrue());
    }

    @CollectionLayout(defaultView = "table")
    @Override
    public SortedSet<DocumentTemplate> getTimeline() {
        final DocumentType type = getType();
        final String atPath = getAtPath();
        return queryResultsCache.execute(
                (Callable<SortedSet<DocumentTemplate>>) () ->
                        Sets.newTreeSet(documentTemplateRepository.findByTypeAndAtPath(type, atPath)),
                DocumentTemplate.class, "getTimeline", type, atPath);
    }
    //endregion


    //region > injected services
    @Inject
    DocumentTemplateRepository documentTemplateRepository;
    @Inject
    QueryResultsCache queryResultsCache;
    @Inject
    ClassService classService;
    @Inject
    ServiceRegistry2 serviceRegistry2;

    //endregion

}
