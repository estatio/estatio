package org.incode.module.communications.dom.impl.comms;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.activation.DataSource;
import javax.annotation.Nullable;
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
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
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
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import org.incode.module.communications.CommunicationsModule;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelType;
import org.incode.module.communications.dom.impl.commchannel.EmailAddress;
import org.incode.module.communications.dom.mixins.DocumentConstants;
import org.incode.module.communications.dom.types.AtPathType;
import org.incode.module.communications.dom.types.SubjectType;
import org.incode.module.document.dom.impl.docs.Document;
import org.incode.module.document.dom.impl.docs.DocumentAbstract;
import org.incode.module.document.dom.impl.paperclips.Paperclip;
import org.incode.module.document.dom.impl.paperclips.PaperclipRepository;

import lombok.Getter;
import lombok.Setter;

@PersistenceCapable(
        identityType=IdentityType.DATASTORE
        , schema = "IncodeCommunications"
)
@DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@Queries({
        @Query(
                name = "findByCommunicationChannel", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.communications.dom.impl.comms.Communication "
                        + "WHERE this.correspondents.contains(correspondent) "
                        + "   && correspondent.channel == :communicationChannel  "
                        + " VARIABLES org.incode.module.communications.dom.impl.comms.CommChannelRole correspondent "),
        @Query(
                name = "findByCommunicationChannelAndPendingOrCreatedAtBetween", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.communications.dom.impl.comms.Communication "
                        + "WHERE this.correspondents.contains(correspondent) "
                        + "   && correspondent.channel == :communicationChannel  "
                        + "   && (    ( state == 'PENDING' )  "
                        + "        || ( :from <= createdAt && createdAt <= :to ) ) "
                        + " VARIABLES org.incode.module.communications.dom.impl.comms.CommChannelRole correspondent "),
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
        editing = Editing.DISABLED
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
            final DateTime createdAt) {
        return new Communication(CommunicationChannelType.EMAIL_ADDRESS, atPath, subject, createdAt);
    }
    public static Communication newPostal(
            final String atPath,
            final String subject,
            final DateTime createdAt) {
        return new Communication(CommunicationChannelType.POSTAL_ADDRESS, atPath, subject, createdAt);
    }
    private Communication(
            final CommunicationChannelType type,
            final String atPath,
            final String subjectIfAny,
            final DateTime createdAt) {
        this.type = type;
        this.atPath = atPath;
        this.subject = subjectIfAny;
        this.createdAt = createdAt;
        this.state = CommunicationState.PENDING;
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


    //region > subject (property)
    public static class SubjectDomainEvent extends PropertyDomainEvent<String> { }

    /**
     * Populated for {@link #getType() type} of {@link CommunicationChannelType#EMAIL_ADDRESS email}, but not for
     * {@link CommunicationChannelType#POSTAL_ADDRESS}.
     */
    @Getter @Setter
    @Column(allowsNull = "true", length = SubjectType.Meta.MAX_LEN)
    @Property(
            domainEvent = SubjectDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String subject;
    //endregion

    //region > createdAt (property)
    public static class CreatedAtDomainEvent extends PropertyDomainEvent<DateTime> { }
    @Getter @Setter
    @Column(allowsNull = "false")
    @Property(
            domainEvent = CreatedAtDomainEvent.class,
            editing = Editing.DISABLED
    )
    private DateTime createdAt;

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
    public void sent() {
        if(CommunicationState.SENT == getState()) {
            return;
        }
        setSentAt(clockService.nowAsDateTime());
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
        repositoryService.persistAndFlush(role); // so that id is populated

        getCorrespondents().add(role);
    }

    @Inject
    RepositoryService repositoryService;
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

    //region > primaryDocument (property)

    @Property
    public Document getPrimaryDocument() {
        return primaryDocumentProvider.findFor(this);
    }

    /**
     * Factored out so can be injected elsewhere also.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class PrimaryDocumentProvider {

        @Programmatic
        public Document findFor(final Communication communication) {
            return queryResultsCache.execute(
                    () -> findForNoCache(communication),
                    PrimaryDocumentProvider.class, "findFor",
                    communication);
        }

        private Document findForNoCache(final Communication communication) {
            final List<Paperclip> paperclipsToPrimaryDocuments = paperclipRepository
                    .findByAttachedToAndRoleName(communication, DocumentConstants.PAPERCLIP_ROLE_PRIMARY);

            final int numPrimaryDocs = paperclipsToPrimaryDocuments.size();
            switch (numPrimaryDocs) {
            case 1:
                // expected case, there should be only one...
                final DocumentAbstract documentAbs = paperclipsToPrimaryDocuments.get(0).getDocument();
                return documentAbs instanceof Document
                        ? (Document) documentAbs
                        : null;
            default:
                // shouldn't happen, defensive coding...
                return null;
            }
        }

        @Inject
        PaperclipRepository paperclipRepository;

        @Inject
        QueryResultsCache queryResultsCache;
    }

    //endregion

    //region > scheduleSend (programmatic), send (action)
    @Programmatic
    public void scheduleSend() {
        backgroundService.execute(this).sendByEmail();
    }

    @Action(hidden = Where.EVERYWHERE) // so can invoke via BackgroundService
    public Communication sendByEmail() {

        // body...
        final Document coverNoteDoc = findDocument(DocumentConstants.PAPERCLIP_ROLE_COVER);
        final String emailBody = coverNoteDoc.asChars();

        // (email) attachments..
        // this corresponds to the primary document and any attachments
        final List<DataSource> attachments = Lists.newArrayList();

        final Document primaryDocument = getPrimaryDocument();
        if(primaryDocument != null) {
            // should be the case
            attachments.add(primaryDocument.asDataSource());
        }
        attachments.addAll(
                findDocumentsInRoleAsStream(DocumentConstants.PAPERCLIP_ROLE_ATTACHMENT)
                        .map(DocumentAbstract::asDataSource)
                        .collect(Collectors.toList()));


        // cc..
        final List<String> toList = findCorrespondents(CommChannelRoleType.TO);
        final List<String> ccList = findCorrespondents(CommChannelRoleType.CC);
        final List<String> bccList = findCorrespondents(CommChannelRoleType.BCC);

        // subject ...
        final String subject = getSubject();

        // finally, we send
        final boolean send = emailService.send(
                toList, ccList, bccList,
                subject, emailBody,
                attachments.toArray(new DataSource[]{}));

        if(!send) {
            throw new ApplicationException("Failed to send email; see system logs for details.");
        }

        // mark this comm as having been sent.
        sent();

        return this;
    }

    List<Document> findDocuments(final String roleName, final String mimeType) {
        return Lists.newArrayList(
                findDocumentsInRoleAsStream(roleName)
                        .filter(x -> mimeType.equals(x.getMimeType()))
                        .collect(Collectors.toList()));
    }

    private Stream<Document> findDocumentsInRoleAsStream(final String roleName) {
        final List<Paperclip> paperclips = findPaperclipsInRole(roleName);
        return paperclips.stream().map(Paperclip::getDocument)
                .filter(Document.class::isInstance).map(Document.class::cast);
    }

    private List<Paperclip> findPaperclipsInRole(final String roleName) {
        return paperclipRepository.findByAttachedToAndRoleName(this, roleName);
    }

    //endregion

    //region > findCorrespondents, findDocument (programmatic)

    @Programmatic
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
        final Stream<Document> documents = findDocumentsInRoleAsStream(roleName);
        final Optional<Document> documentIfAny = documents.findFirst();
        return documentIfAny.orElseThrow(() -> (RuntimeException)new ApplicationException(String.format(
                "Could not find document (via paperclip with role of '%s')",
                roleName)));
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
        return ObjectContracts.toString(this, "type", "createdAt", "sentAt", "state", "subject", "atPath", "id");
    }

    @Override
    public int compareTo(final Communication other) {
        return ObjectContracts.compare(this, other, "type", "createdAt", "sentAt", "state", "subject", "atPath", "id");
    }
    //endregion

    public static class Functions {
        private Functions(){}

        public static Function<Communication, DateTime> createdAt() {
            return new Function<Communication, DateTime>() {
                @Nullable @Override
                public DateTime apply(@Nullable final Communication comm) {
                    return comm != null ? comm.getCreatedAt() : null;
                }
            };
        }
    }

    public static class Orderings {
        private Orderings(){}

        public final static Ordering<Communication> createdAtDescending =
                Ordering.natural()
                        .onResultOf(Functions.createdAt())
                        .reverse();

    }

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

    @Inject
    PrimaryDocumentProvider primaryDocumentProvider;

    //endregion

}
