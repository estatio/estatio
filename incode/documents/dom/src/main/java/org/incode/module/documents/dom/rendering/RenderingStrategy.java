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
package org.incode.module.documents.dom.rendering;

import java.net.URL;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
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
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.documents.dom.DocumentsModule;
import org.incode.module.documents.dom.docs.DocumentNature;
import org.incode.module.documents.dom.services.ClassService;
import org.incode.module.documents.dom.docs.DocumentTemplateRepository;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeDocuments",
        table = "RenderingStrategy"
)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
        @Query(
                name = "findByReference", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.documents.dom.rendering.RenderingStrategy "
                        + "WHERE reference == :reference ")
})
@Uniques({
        @Unique(
                name = "RenderingStrategy_reference_IDX",
                members = { "reference" }
        ),
        @Unique(
                name = "RenderingStrategy_name_IDX",
                members = { "name" }
        )
})
@DomainObject(
        objectType = "incodeDocuments.RenderingStrategy",
        editing = Editing.DISABLED,
        bounded = true
)
@DomainObjectLayout(
        titleUiEvent = RenderingStrategy.TitleUiEvent.class,
        iconUiEvent = RenderingStrategy.IconUiEvent.class,
        cssClassUiEvent = RenderingStrategy.CssClassUiEvent.class,
        bookmarking = BookmarkPolicy.AS_ROOT
)
public class RenderingStrategy implements Comparable<RenderingStrategy> {


    //region > ui event classes
    public static class TitleUiEvent extends DocumentsModule.TitleUiEvent<RenderingStrategy>{}
    public static class IconUiEvent extends DocumentsModule.IconUiEvent<RenderingStrategy>{}
    public static class CssClassUiEvent extends DocumentsModule.CssClassUiEvent<RenderingStrategy>{}
    //endregion

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends DocumentsModule.PropertyDomainEvent<RenderingStrategy, T> { }
    public static abstract class CollectionDomainEvent<T> extends DocumentsModule.CollectionDomainEvent<RenderingStrategy, T> { }
    public static abstract class ActionDomainEvent extends DocumentsModule.ActionDomainEvent<RenderingStrategy> { }
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService
    public static class TitleSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(RenderingStrategy.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTranslatableTitle(titleOf(ev.getSource()));
        }
        private TranslatableString titleOf(final RenderingStrategy renderingStrategy) {
            return TranslatableString.tr("[{reference}] {name}",
                    "reference", renderingStrategy.getReference(),
                    "name", renderingStrategy.getName());
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
        public void on(RenderingStrategy.IconUiEvent ev) {
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
        public void on(RenderingStrategy.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion


    //region > constructor
    public RenderingStrategy(
            final String reference,
            final String name,
            final DocumentNature documentNature,
            final Class<? extends Renderer> rendererClass) {
        this.reference = reference;
        this.name = name;
        this.documentNature = documentNature;
        this.rendererClassName = rendererClass.getName();
        this.previewingAsBlob = RendererWithPreviewAsBlob.class.isAssignableFrom(rendererClass);
        this.previewingAsClob = RendererWithPreviewAsClob.class.isAssignableFrom(rendererClass);
        this.previewingAsUrl = RendererWithPreviewAsUrl.class.isAssignableFrom(rendererClass);
    }
    //endregion


    //region > reference (property)
    public static class ReferenceDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = DocumentsModule.JdoColumnLength.REFERENCE)
    @Property(
            domainEvent = ReferenceDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String reference;
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

    //region > nature (property)
    public static class DocumentNatureDomainEvent extends PropertyDomainEvent<DocumentNature> { }

    /**
     * Whether this rendering strategy acts upon {@link DocumentNature#BYTES bytes} (produces {@link Blob}s) or upon
     * {@link DocumentNature#CHARACTERS characters} (produces {@link Clob}s).
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = DocumentNatureDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DocumentNature documentNature;
    //endregion

    //region > previewingAsBlob (property)
    public static class PreviewingAsBlobDomainEvent extends PropertyDomainEvent<Boolean> { }

    /**
     * Whether this rendering strategy supports previewing as a {@link Blob}s.
     */
    @Getter() @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = PreviewingAsBlobDomainEvent.class,
            editing = Editing.DISABLED
    )
    private boolean previewingAsBlob;
    //endregion

    //region > previewingAsClob (property)
    public static class PreviewingAsClobDomainEvent extends PropertyDomainEvent<Boolean> { }

    /**
     * Whether this rendering strategy supports previewing as a {@link Clob}.
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = PreviewingAsClobDomainEvent.class,
            editing = Editing.DISABLED
    )
    private boolean previewingAsClob;
    //endregion

    //region > previewingAsUrl (property)
    public static class PreviewingAsUrlDomainEvent extends PropertyDomainEvent<Boolean> { }

    /**
     * Whether this rendering strategy supports previewing as a {@link URL}.
     */
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = PreviewingAsUrlDomainEvent.class,
            editing = Editing.DISABLED
    )
    private boolean previewingAsUrl;
    //endregion

    //region > rendererClassName (property)
    public static class RendererClassNameDomainEvent extends PropertyDomainEvent<String> { }

    @Getter @Setter
    @Column(allowsNull = "false", length = DocumentsModule.JdoColumnLength.FQCN)
    @Property(
            domainEvent = RendererClassNameDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String rendererClassName;
    //endregion

    //region > instantiateRenderer (programmatic)
    @Programmatic
    public Renderer instantiateRenderer() {
        final Renderer renderer = (Renderer) classService.instantiate(getRendererClassName());
        serviceRegistry2.injectServicesInto(renderer);
        return renderer;
    }
    //endregion

    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "reference", "name", "rendererClassName");
    }

    @Override
    public int compareTo(final RenderingStrategy other) {
        return ObjectContracts.compare(this, other, "reference");
    }

    //endregion

    //region > injected services
    @Inject
    DocumentTemplateRepository documentTemplateRepository;
    @Inject
    ClassService classService;
    @Inject
    ServiceRegistry2 serviceRegistry2;
    //endregion

}
