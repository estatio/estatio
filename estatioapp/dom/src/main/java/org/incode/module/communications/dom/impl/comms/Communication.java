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
package org.incode.module.communications.dom.impl.comms;

import java.util.SortedSet;
import java.util.TreeSet;

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
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.DateTime;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.util.ObjectContracts;

import org.incode.module.communications.dom.CommunicationsModule;

import org.estatio.dom.communicationchannel.CommunicationChannelType;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType=IdentityType.DATASTORE
        //        ,
        //        schema = "estatioCommunications"  // DN doesn't seem to allow this to be in a different schema...
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        // none yet
})
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Indices({
        @Index(
                name = "Communication_type_atPath_IDX",
                members = { "type", "atPath" }
        ),
        @Index(
                name = "Communication_atPath_type_IDX",
                members = { "atPath", "type" }
        ),
})
@Uniques({
        // none yet
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "estatioCommunications.Communication"
)
@DomainObjectLayout(
        titleUiEvent = Communication.TitleUiEvent.class,
        iconUiEvent = Communication.IconUiEvent.class,
        cssClassUiEvent = Communication.CssClassUiEvent.class,
        bookmarking = BookmarkPolicy.AS_ROOT
)

public class Communication implements Comparable<Communication> {

    //region > ui event classes
    public static class TitleUiEvent extends CommunicationsModule.TitleUiEvent<Communication> {}
    public static class IconUiEvent extends CommunicationsModule.IconUiEvent<Communication>{}
    public static class CssClassUiEvent extends CommunicationsModule.CssClassUiEvent<Communication>{}
    //endregion

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends CommunicationsModule.PropertyDomainEvent<Communication, T> { }
    public static abstract class CollectionDomainEvent<T> extends CommunicationsModule.CollectionDomainEvent<Communication, T> { }
    public static abstract class ActionDomainEvent extends CommunicationsModule.ActionDomainEvent<Communication> { }
    //endregion


    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService
    public static class TitleSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Communication.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTitle(titleOf(ev.getSource()));
        }
        private String titleOf(final Communication communication) {
            return communication.getSubject();
        }
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService
    public static class IconSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Communication.IconUiEvent ev) {
            if(ev.getIconName() != null) {
                return;
            }
            ev.setIconName("email");
        }
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService
    public static class CssClassSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Communication.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion

    //region > constructors
    public Communication(
            final CommunicationChannelType type,
            final String atPath,
            final String subject,
            final DateTime sent) {
        this.type = type;
        this.atPath = atPath;
        this.subject = subject;
        this.sent = sent;
    }
    //endregion


    //region > type (property)
    public static class TypeDomainEvent extends PropertyDomainEvent<CommunicationChannelType> { }
    @Getter @Setter
    @Column(allowsNull = "false", name = "typeId")
    @Property(
            domainEvent = TypeDomainEvent.class,
            editing = Editing.DISABLED
    )
    private CommunicationChannelType type;
    //endregion

    //region > atPath (property)
    public static class AtPathDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = CommunicationsModule.JdoColumnLength.AT_PATH)
    @Property(
            domainEvent = AtPathDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            named = "Application tenancy"
    )
    private String atPath;
    //endregion


    //region > subject (property)
    public static class SubjectDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = CommunicationsModule.JdoColumnLength.SUBJECT)
    @Property(
            domainEvent = SubjectDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String subject;
    //endregion

    //region > sent (property)
    public static class SentDomainEvent extends PropertyDomainEvent<DateTime> { }
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = SentDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DateTime sent;
    //endregion

    //region > correspondents
    @Persistent(mappedBy = "communication", dependentElement = "true")
    @Collection()
    @Getter @Setter
    private SortedSet<CommChannelRole> correspondents = new TreeSet<CommChannelRole>();
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
        return ObjectContracts.toString(this, "type", "sent", "subject", "atPath", "id");
    }

    @Override
    public int compareTo(final Communication other) {
        return ObjectContracts.compare(this, other, "type", "sent", "subject", "atPath", "id");
    }
    //endregion

    //region > injected services
    //endregion

}
