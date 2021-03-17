package org.incode.module.communications.dom.impl.comms;

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
import javax.jdo.annotations.Queries;
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
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.util.ObjectContracts;

import org.incode.module.communications.CommunicationsModule;

import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.types.DescriptionType;

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
        // none yet
})
@Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Indices({
        @Index(
                name = "CommChannelRole_comm_channel_type_IDX",
                members = { "communication", "channel", "type" }
        ),
        @Index(
                name = "Communication_channel_comm_type_IDX",
                members = { "channel", "communication", "type" }
        ),
        @Index(
                name = "CommChannelRole_comm_type_channel_IDX",
                members = { "communication", "type", "channel" }
        ),
        @Index(
                name = "Communication_channel_type_comm_IDX",
                members = { "channel", "type", "communication" }
        ),
})
@Uniques({
        // none yet
})
@DomainObject(
        editing = Editing.DISABLED
)
@DomainObjectLayout(
        titleUiEvent = CommChannelRole.TitleUiEvent.class,
        iconUiEvent = CommChannelRole.IconUiEvent.class,
        cssClassUiEvent = CommChannelRole.CssClassUiEvent.class,
        bookmarking = BookmarkPolicy.AS_ROOT
)

public class CommChannelRole implements Comparable<CommChannelRole> {

    //region > ui event classes
    public static class TitleUiEvent extends CommunicationsModule.TitleUiEvent<CommChannelRole> {}
    public static class IconUiEvent extends CommunicationsModule.IconUiEvent<CommChannelRole>{}
    public static class CssClassUiEvent extends CommunicationsModule.CssClassUiEvent<CommChannelRole>{}
    //endregion

    //region > domain event classes
    public static abstract class PropertyDomainEvent<T> extends CommunicationsModule.PropertyDomainEvent<CommChannelRole, T> { }
    public static abstract class CollectionDomainEvent<T> extends CommunicationsModule.CollectionDomainEvent<CommChannelRole, T> { }
    public static abstract class ActionDomainEvent extends CommunicationsModule.ActionDomainEvent<CommChannelRole> { }
    //endregion

    //region > title, icon, cssClass
    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class TitleSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(CommChannelRole.TitleUiEvent ev) {
            if(ev.getTitle() != null) {
                return;
            }
            ev.setTranslatableTitle(titleOf(ev.getSource()));
        }
        private TranslatableString titleOf(final CommChannelRole role) {
            return TranslatableString.tr(
                    "{type} {description}",
                    "type", role.getType().name(),
                    "description", role.getDescription()
            );
        }
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class IconSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(CommChannelRole.IconUiEvent ev) {
            if(ev.getIconName() != null) {
                return;
            }
            ev.setIconName("email");
        }
    }

    /**
     * Implemented as a subscriber so can be overridden by consuming application if required.
     */
    @DomainService(nature = NatureOfService.DOMAIN)
    public static class CssClassSubscriber extends AbstractSubscriber {
        @EventHandler
        @Subscribe
        public void on(CommChannelRole.CssClassUiEvent ev) {
            if(ev.getCssClass() != null) {
                return;
            }
            ev.setCssClass("");
        }
    }
    //endregion

    //region > constructors
    public CommChannelRole(
            final CommChannelRoleType type,
            final Communication communication,
            final CommunicationChannel channel,
            final String description) {
        this.type = type;
        this.communication = communication;
        this.channel = channel;
        this.description = description;
    }
    //endregion


    //region > type (property)
    public static class TypeDomainEvent extends PropertyDomainEvent<CommChannelRoleType> { }
    @Getter @Setter
    @Column(allowsNull = "false", name = "typeId")
    @Property(
            domainEvent = TypeDomainEvent.class,
            editing = Editing.DISABLED
    )
    private CommChannelRoleType type;
    //endregion


    //region > communication (property)
    public static class CommunicationDomainEvent extends PropertyDomainEvent<Communication> { }
    @Getter @Setter
    @Column(allowsNull = "false", name = "communicationId")
    @Property(
            domainEvent = CommunicationDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(
            hidden = Where.REFERENCES_PARENT
    )
    private Communication communication;
    //endregion

    //region > channel (property)
    public static class ChannelDomainEvent extends PropertyDomainEvent<CommunicationChannel> { }
    @Getter @Setter
    @Column(allowsNull = "true", name = "channelId")
    @Property(
            domainEvent = ChannelDomainEvent.class,
            editing = Editing.DISABLED
    )
    private CommunicationChannel channel;
    //endregion

    //region > description (property)
    public static class DescriptionDomainEvent extends PropertyDomainEvent<String> { }
    @Getter @Setter
    @Column(allowsNull = "true", length = DescriptionType.Meta.MAX_LEN)
    @Property(
            domainEvent = DescriptionDomainEvent.class,
            editing = Editing.DISABLED
    )
    private String description;
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
        return ObjectContracts.toString(this, "communication", "type", "channel", "id");
    }

    @Override
    public int compareTo(final CommChannelRole other) {
        return ObjectContracts.compare(this, other, "communication", "type", "channel", "id");
    }
    //endregion

    //region > injected services
    //endregion

}
