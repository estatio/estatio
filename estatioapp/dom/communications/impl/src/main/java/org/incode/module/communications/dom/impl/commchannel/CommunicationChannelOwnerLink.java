package org.incode.module.communications.dom.impl.commchannel;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import com.google.common.base.Function;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.poly.dom.PolymorphicAssociationLink;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE
        ,schema = "IncodeCommunications"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByCommunicationChannel", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLink "
                        + "WHERE communicationChannel == :communicationChannel"),
        @javax.jdo.annotations.Query(
                name = "findByOwner", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLink "
                        + "WHERE ownerObjectType == :ownerObjectType "
                        + "   && ownerIdentifier == :ownerIdentifier "),
        @javax.jdo.annotations.Query(
                name = "findByOwnerAndCommunicationChannelType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwnerLink "
                        + "WHERE ownerObjectType == :ownerObjectType "
                        + "   && ownerIdentifier == :ownerIdentifier "
                        + "   && communicationChannelType == :communicationChannelType ")
})
@javax.jdo.annotations.Indices({
        @javax.jdo.annotations.Index(
                name = "CommunicationChannelOwnerLink_main_idx",
                members = { "ownerObjectType", "ownerIdentifier", "communicationChannelType", "communicationChannel" })
})
@javax.jdo.annotations.Unique(name="CommunicationChannelOwnerLink_commchannel_owner_UNQ", members = {"communicationChannel","ownerObjectType","ownerIdentifier"})
@DomainObject(
        objectType = "comms.CommunicationChannelOwnerLink"  // TODO: reconcile with schema
)
public abstract class CommunicationChannelOwnerLink extends PolymorphicAssociationLink<CommunicationChannel, CommunicationChannelOwner, CommunicationChannelOwnerLink> {


    public static class InstantiateEvent
            extends PolymorphicAssociationLink.InstantiateEvent<CommunicationChannel, CommunicationChannelOwner, CommunicationChannelOwnerLink> {

        public InstantiateEvent(final Object source, final CommunicationChannel subject, final CommunicationChannelOwner owner) {
            super(CommunicationChannelOwnerLink.class, source, subject, owner);
        }
    }

    //region > constructor
    public CommunicationChannelOwnerLink() {
        super("{polymorphicReference} owns {subject}");
    }
    //endregion

    //region > SubjectPolymorphicReferenceLink API
    @Override
    @Programmatic
    public CommunicationChannel getSubject() {
        return getCommunicationChannel();
    }

    @Override
    @Programmatic
    public void setSubject(final CommunicationChannel subject) {
        setCommunicationChannel(subject);
    }

    @Override
    @Programmatic
    public String getPolymorphicObjectType() {
        return getOwnerObjectType();
    }

    @Override
    @Programmatic
    public void setPolymorphicObjectType(final String polymorphicObjectType) {
        setOwnerObjectType(polymorphicObjectType);
    }

    @Override
    @Programmatic
    public String getPolymorphicIdentifier() {
        return getOwnerIdentifier();
    }

    @Override
    @Programmatic
    public void setPolymorphicIdentifier(final String polymorphicIdentifier) {
        setOwnerIdentifier(polymorphicIdentifier);
    }

    // //////////////////////////////////////

    @Column(
            allowsNull = "false",
            name = "communicationChannelId"
    )
    @Getter @Setter
    private CommunicationChannel communicationChannel;

    // //////////////////////////////////////

    @Column(allowsNull = "false", length = 255)
    @Getter @Setter
    private String ownerObjectType;

    // //////////////////////////////////////

    @Column(allowsNull = "false", length = 255)
    @Getter @Setter
    private String ownerIdentifier;

    // //////////////////////////////////////

    /**
     * copy of the {@link #getCommunicationChannel()}'s {@link CommunicationChannel#getType() type}.
     *
     * <p>
     *     To support querying.  This is an immutable property of {@link CommunicationChannel} so
     *     it is safe to copy.
     * </p>
     */
    @MemberOrder(sequence = "1")
    @javax.jdo.annotations.Column(allowsNull = "false", length = CommunicationChannelType.Type.MAX_LEN)
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private CommunicationChannelType communicationChannelType;

    // //////////////////////////////////////

    public static class Functions {
        public static Function<CommunicationChannelOwnerLink, CommunicationChannel> communicationChannel() {
            return communicationChannel(CommunicationChannel.class);
        }
        public static <T extends CommunicationChannel> Function<CommunicationChannelOwnerLink, T> communicationChannel(Class<T> cls) {
            return new Function<CommunicationChannelOwnerLink, T>() {
                @Override
                public T apply(final CommunicationChannelOwnerLink input) {
                    return (T)input.getCommunicationChannel();
                }
            };
        }
        public static Function<CommunicationChannelOwnerLink, CommunicationChannelOwner> owner() {
            return owner(CommunicationChannelOwner.class);
        }

        public static <T extends CommunicationChannelOwner> Function<CommunicationChannelOwnerLink, T> owner(final Class<T> cls) {
            return new Function<CommunicationChannelOwnerLink, T>() {
                @Override
                public T apply(final CommunicationChannelOwnerLink input) {
                    return (T)input.getPolymorphicReference();
                }
            };
        }
    }
}
