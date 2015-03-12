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
package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import com.google.common.base.Function;
import org.isisaddons.module.poly.dom.PolymorphicAssociationLink;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.Where;
import org.estatio.dom.JdoColumnLength;

@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.IDENTITY, column = "id")
@javax.jdo.annotations.Inheritance(
        strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByCommunicationChannel", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.communicationchannel.CommunicationChannelOwnerLink "
                        + "WHERE communicationChannel == :communicationChannel"),
        @javax.jdo.annotations.Query(
                name = "findByOwner", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.communicationchannel.CommunicationChannelOwnerLink "
                        + "WHERE ownerObjectType == :ownerObjectType "
                        + "   && ownerIdentifier == :ownerIdentifier "),
        @javax.jdo.annotations.Query(
                name = "findByOwnerAndCommunicationChannelType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.communicationchannel.CommunicationChannelOwnerLink "
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
        objectType = "comms.CommunicationChannelOwnerLink"
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
    //endregion

    //region > communicationChannel (property)
    private CommunicationChannel communicationChannel;
    @Column(
            allowsNull = "false",
            name = "communicationChannelId"
    )
    public CommunicationChannel getCommunicationChannel() {
        return communicationChannel;
    }

    public void setCommunicationChannel(final CommunicationChannel communicationChannel) {
        this.communicationChannel = communicationChannel;
    }
    //endregion

    //region > ownerObjectType (property)
    private String ownerObjectType;

    @Column(allowsNull = "false", length = 255)
    public String getOwnerObjectType() {
        return ownerObjectType;
    }

    public void setOwnerObjectType(final String ownerObjectType) {
        this.ownerObjectType = ownerObjectType;
    }
    //endregion

    //region > ownerIdentifier (property)
    private String ownerIdentifier;

    @Column(allowsNull = "false", length = 255)
    public String getOwnerIdentifier() {
        return ownerIdentifier;
    }

    public void setOwnerIdentifier(final String ownerIdentifier) {
        this.ownerIdentifier = ownerIdentifier;
    }
    //endregion

    //region > communicationChannelType (property)

    private CommunicationChannelType communicationChannelType;

    /**
     * copy of the {@link #getCommunicationChannel()}'s {@link CommunicationChannel#getType() type}.
     *
     * <p>
     *     To support querying.  This is an immutable property of {@link org.estatio.dom.communicationchannel.CommunicationChannel} so
     *     it is safe to copy.
     * </p>
     */
    @MemberOrder(sequence = "1")
    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TYPE_ENUM)
    @Property(hidden = Where.EVERYWHERE)
    public CommunicationChannelType getCommunicationChannelType() {
        return communicationChannelType;
    }

    public void setCommunicationChannelType(final CommunicationChannelType communicationChannelType) {
        this.communicationChannelType = communicationChannelType;
    }
    //endregion

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
