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

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Objects;
import com.google.common.eventbus.Subscribe;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.joda.time.DateTime;

import org.apache.isis.applib.AbstractSubscriber;
import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.background.BackgroundService;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.applib.services.email.EmailService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import org.incode.module.communications.dom.CommunicationsModule;
import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.documents.dom.impl.docs.Document;
import org.incode.module.documents.dom.impl.docs.DocumentAbstract;
import org.incode.module.documents.dom.impl.paperclips.Paperclip;
import org.incode.module.documents.dom.impl.paperclips.PaperclipRepository;

import org.estatio.dom.communicationchannel.CommunicationChannel;
import org.estatio.dom.communicationchannel.CommunicationChannelType;
import org.estatio.dom.communicationchannel.EmailAddress;

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
    @DomainService(nature = NatureOfService.DOMAIN)
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
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class IconSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(Communication.IconUiEvent ev) {
            if(ev.getIconName() != null) {
                return;
            }
            switch (ev.getSource().getType()) {
            case POSTAL_ADDRESS:
                ev.setIconName("postal");
                break;
            case EMAIL_ADDRESS:
                ev.setIconName("email");
                break;
            case PHONE_NUMBER:
                break;
            case FAX_NUMBER:
                break;
            }
        }
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
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
    public static Communication newEmail(
            final String atPath,
            final String subject,
            final DateTime queuedAt) {
        return new Communication(CommunicationChannelType.EMAIL_ADDRESS, atPath, subject, queuedAt);
    }
    public static Communication newPostal(
            final String atPath,
            final String subject) {
        return new Communication(CommunicationChannelType.POSTAL_ADDRESS, atPath, subject, null);
    }
    private Communication(
            final CommunicationChannelType type,
            final String atPath,
            final String subjectIfAny,
            final DateTime queuedAt) {
        this.type = type;
        this.atPath = atPath;
        this.subject = subjectIfAny;
        this.queuedAt = queuedAt;
        this.state = CommunicationState.QUEUED;
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

    /**
     * Populated for {@link #getType() type} of {@link CommunicationChannelType#EMAIL_ADDRESS email}, but not for
     * {@link CommunicationChannelType#POSTAL_ADDRESS}.
     */
    @Getter @Setter
    @Column(allowsNull = "true", length = CommunicationsModule.JdoColumnLength.SUBJECT)
    @Property(
            domainEvent = SubjectDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String subject;
    //endregion

    //region > queuedAt (property)
    public static class QueuedAtDomainEvent extends PropertyDomainEvent<DateTime> { }
    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(
            domainEvent = QueuedAtDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DateTime queuedAt;
    //endregion

    //region > sentAt (property)
    public static class SentAtDomainEvent extends PropertyDomainEvent<DateTime> { }
    @Getter @Setter
    @Column(allowsNull = "true")
    @Property(
            domainEvent = SentAtDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DateTime sentAt;
    //endregion

    //region > sent (programmatic)
    @Programmatic
    public void sent(DateTime dateTime) {
        setSentAt(dateTime);
        setState(CommunicationState.SENT);
    }
    //endregion

    //region > correspondents
    @Persistent(mappedBy = "communication", dependentElement = "true")
    @Collection()
    @Getter @Setter
    private SortedSet<CommChannelRole> correspondents = new TreeSet<CommChannelRole>();
    //endregion

    //region > addCorrespondentIfAny (programmatic)

    @Programmatic
    public void addCorrespondent(
            final CommChannelRoleType roleType,
            final CommunicationChannel communicationChannel) {
        final CommChannelRole role = new CommChannelRole(roleType, this, communicationChannel, titleService.titleOf(communicationChannel));
        getCorrespondents().add(role);
    }

    @Programmatic
    public void addCorrespondentIfAny(
            final CommChannelRoleType roleType,
            final String description) {
        if(description == null) {
            // just ignore
            return;
        }
        final CommChannelRole role = new CommChannelRole(roleType, this, null, description);
        getCorrespondents().add(role);
    }
    //endregion

    //region > state (property)
    public static class StateDomainEvent extends PropertyDomainEvent<CommunicationState> { }
    @Getter @Setter
    @Column(allowsNull = "false", name = "stateId")
    @Property(
            domainEvent = StateDomainEvent.class,
            editing = Editing.DISABLED
    )
    private CommunicationState state;
    //endregion

    @Programmatic
    public void scheduleSend(final String subject) {
        backgroundService.execute(this).send(subject);
    }

    @Action(hidden = Where.EVERYWHERE) // so can invoke via BackgroundService
    public Communication send(final String subject) {

        Document attachment = findDocument(DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT);
        Document coverNoteDoc = findDocument(DocumentConstants.PAPERCLIP_ROLE_COVER);

        List<String> toList = findCorrespondents(CommChannelRoleType.TO);
        List<String> ccList = findCorrespondents(CommChannelRoleType.CC);
        List<String> bccList = findCorrespondents(CommChannelRoleType.BCC);

        final String emailBody = coverNoteDoc.asChars();

        final boolean send = emailService.send(
                toList, ccList, bccList,
                subject, emailBody,
                attachment.asDataSource());

        if(!send) {
            throw new ApplicationException("Failed to send email; see system logs for details.");
        }

        sent(clockService.nowAsDateTime());

        return this;
    }

    public List<String> findCorrespondents(final CommChannelRoleType roleType) {
        SortedSet<CommChannelRole> correspondents = getCorrespondents();
        return correspondents.stream()
                             .filter(x -> x.getType() == roleType)
                             .map(x -> {
                                 CommunicationChannel channel = x.getChannel();
                                 if(channel != null) {
                                     if (channel.getType() == CommunicationChannelType.EMAIL_ADDRESS) {
                                         EmailAddress emailAddress = (EmailAddress) channel;
                                         return emailAddress.getEmailAddress();
                                     } else {
                                         return null;
                                     }
                                 } else {
                                     return x.getDescription();
                                 }
                             })
                             .filter(x -> x != null)
                             .collect(Collectors.toList());
    }

    @Programmatic
    public Document findDocument(final String roleName) {
        DocumentAbstract documentAbstract = findDocumentIfAny(roleName);
        if(documentAbstract == null) {
            throw new ApplicationException(
                    String.format("Could not find document via paperclip, role '%s'", roleName));
        }
        if(documentAbstract instanceof Document) {
            return (Document) documentAbstract;
        }
        throw new ApplicationException(
                String.format("Found document via paperclip, role '%s', but was DocumentTemplate (not an instance of Document)", roleName));
    }

    private DocumentAbstract findDocumentIfAny(final String roleName) {
        List<Paperclip> paperclips = paperclipRepository.findByAttachedTo(this);
        Optional<Paperclip> paperclipForAttachment = paperclips.stream()
                .filter(x -> Objects.equal(x.getRoleName(), roleName)).findFirst();
        return paperclipForAttachment.isPresent() ?
                paperclipForAttachment.get().getDocument() : null;
    }

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
        return ObjectContracts.toString(this, "type", "queuedAt", "sentAt", "state", "subject", "atPath", "id");
    }

    @Override
    public int compareTo(final Communication other) {
        return ObjectContracts.compare(this, other, "type", "queuedAt", "sentAt", "state", "subject", "atPath", "id");
    }
    //endregion

    //region > injected services
    @Inject
    TitleService titleService;

    @Inject
    BackgroundService backgroundService;

    @Inject
    EmailService emailService;

    @Inject
    ClockService clockService;

    @Inject
    PaperclipRepository paperclipRepository;
    //endregion

}
