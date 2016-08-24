/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.incode.module.doctemplates.dom;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;
import org.apache.isis.applib.util.TitleBuffer;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "incodeDocTemplates",
        table = "DocTemplate"
)
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@Queries({
        @javax.jdo.annotations.Query(
                name = "findByReferenceAndAtPath", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.doctemplates.dom.DocTemplate "
                        + "WHERE reference == :reference "
                        + "   && atPath    == :atPath ")
})
@Uniques({
        @Unique(
                name = "DocTemplate_reference_atPath_IDX",
                members = { "reference", "atPath" }
        )
})
@DomainObject(
        objectType = "incodeDocTemplates.DocTemplate",
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        titleUiEvent = DocTemplate.TitleUiEvent.class,
        iconUiEvent = DocTemplate.IconUiEvent.class,
        cssClassUiEvent = DocTemplate.CssClassUiEvent.class
)
public class DocTemplate implements Comparable<DocTemplate> {

    //region > ui event classes
    public static class TitleUiEvent extends DocTemplatesModule.TitleUiEvent<DocTemplate>{}
    public static class IconUiEvent extends DocTemplatesModule.IconUiEvent<DocTemplate>{}
    public static class CssClassUiEvent extends DocTemplatesModule.CssClassUiEvent<DocTemplate>{}
    //endregion

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends DocTemplatesModule.PropertyDomainEvent<DocTemplate, T> { }
    public static abstract class CollectionDomainEvent<T> extends DocTemplatesModule.CollectionDomainEvent<DocTemplate, T> { }
    public static abstract class ActionDomainEvent extends DocTemplatesModule.ActionDomainEvent<DocTemplate> { }
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService
    public static class TitleSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(DocTemplate.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTitle(titleOf(ev.getSource()));
        }
        private String titleOf(final DocTemplate docTemplate) {
            final TitleBuffer buf = new TitleBuffer();
            buf.append(docTemplate.getAtPath());
            buf.append(",");
            buf.append(docTemplate.getReference());
            return buf.toString();
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
        public void on(DocTemplate.IconUiEvent ev) {
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
        public void on(DocTemplate.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion


    //region > constructor
    public DocTemplate(final String reference, final String atPath, final String templateText) {
        this.reference = reference;
        this.atPath = atPath;
        this.templateText = templateText;
    }
    //endregion

    //region > reference (property)
    public static class ReferenceDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = DocTemplatesModule.JdoColumnLength.DOC_TEMPLATE_REFERENCE)
    @Property(
            domainEvent = ReferenceDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String reference;
    //endregion



    //region > atPath (property)
    public static class AtPathDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "true", length = DocTemplatesModule.JdoColumnLength.AT_PATH)
    @Property(
            domainEvent = AtPathDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            named = "Application tenancy"
    )
    private String atPath;
    //endregion



    //region > templateText (property)
    public static class TemplateTextDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull="true", jdbcType="CLOB", sqlType="LONGVARCHAR")
    @Property(
            domainEvent = TemplateTextDomainEvent.class,
            editing = Editing.ENABLED // huh... ought to override the isis configuration, but does not seem to...  ISIS-1478
    )
    private String templateText;
    //endregion





    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "reference", "atPath");
    }

    @Override
    public int compareTo(final DocTemplate other) {
        return ObjectContracts.compare(this, other, "reference", "atPath");
    }

    //endregion

    //region > injected services
    @Inject
    DocTemplateRepository docTemplateRepository;
    //endregion

}
