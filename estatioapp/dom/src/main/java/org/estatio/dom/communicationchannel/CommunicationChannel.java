/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
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
package org.estatio.dom.communicationchannel;

import java.util.SortedSet;

import javax.inject.Inject;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.IsisApplibModule.ActionDomainEvent;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceGetter;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a mechanism for communicating with its
 * {@link CommunicationChannelOwner owner}.
 * 
 * <p>
 * This is an abstract entity; concrete subclasses are {@link PostalAddress
 * postal}, {@link PhoneOrFaxNumber phone/fax} and {@link EmailAddress email}.
 */
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReferenceAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.communicationchannel.CommunicationChannel "
                        + "WHERE reference == :reference "
                        + "&& type == :type")
})
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public abstract class CommunicationChannel
        extends EstatioDomainObject<CommunicationChannel>
        implements WithNameGetter, WithReferenceGetter, WithApplicationTenancyCountry {

    public CommunicationChannel() {
        super("type, legal, id");
    }

    // //////////////////////////////////////

    public String iconName() {
        return getType().title().replace(" ", "");
    }

    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getOwner().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "2")
    @Property(hidden = Where.OBJECT_FORMS)
    public String getName() {
        return getContainer().titleOf(this);
    }

    // //////////////////////////////////////

    @Property(
            notPersisted = true,
            editing = Editing.DISABLED
    )
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    public CommunicationChannelOwner getOwner() {
        final CommunicationChannelOwnerLink link = getOwnerLink();
        return link != null? link.getPolymorphicReference(): null;
    }

    @Programmatic
    public void setOwner(final CommunicationChannelOwner owner) {
        removeOwnerLink();
        final CommunicationChannelOwnerLink link = communicationChannelOwnerLinkRepository.createLink(this, owner);
    }

    private void removeOwnerLink() {
        final CommunicationChannelOwnerLink ownerLink = getOwnerLink();
        if(ownerLink != null) {
            getContainer().remove(ownerLink);
        }
    }

    private CommunicationChannelOwnerLink getOwnerLink() {
        return communicationChannelOwnerLinkRepository.findByCommunicationChannel(this);
    }

    // //////////////////////////////////////

    @MemberOrder(sequence = "1")
    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TYPE_ENUM)
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private CommunicationChannelType type;

    // //////////////////////////////////////

    /**
     * For import purposes only
     */
    @javax.jdo.annotations.Column(allowsNull = "true", length = JdoColumnLength.REFERENCE)
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String reference;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(length = JdoColumnLength.DESCRIPTION)
    @Property(optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    @PropertyLayout(multiLine = 3)
    @Getter @Setter
    private String description;

    // //////////////////////////////////////

    @MemberOrder(sequence = "3")
    @Getter @Setter
    private boolean legal;

    // //////////////////////////////////////

    @Column(allowsNull = "true", length = JdoColumnLength.TYPE_ENUM)
    @Getter @Setter
    private CommunicationChannelPurposeType purpose;

    // //////////////////////////////////////

    public CommunicationChannel change(
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(multiLine = 3)
            final String description,
            @Parameter(optionality = Optionality.OPTIONAL)
            final boolean legal,
            @Parameter(optionality = Optionality.OPTIONAL)
            final CommunicationChannelPurposeType purpose) {
        setLegal(legal);
        setPurpose(purpose);
        setDescription(description);
        return this;
    }

    public String default0Change() {
        return getDescription();
    }

    public boolean default1Change() {
        return isLegal();
    }

    public CommunicationChannelPurposeType default2Change() {
        return getPurpose();
    }

    public static class RemoveEvent extends ActionDomainEvent<CommunicationChannel> {
        private static final long serialVersionUID = 1L;
        public CommunicationChannel getReplacement() {
            return (CommunicationChannel) (this.getArguments().isEmpty() ? null : getArguments().get(0));
        }
    }

    @Action(domainEvent = CommunicationChannel.RemoveEvent.class)
    public void remove(
            @Parameter(optionality = Optionality.OPTIONAL)
            final CommunicationChannel replaceWith) {
        removeOwnerLink();
        getContainer().remove(this);
    }

    public SortedSet<CommunicationChannel> choices0Remove() {
        return communicationChannelRepository.findOtherByOwnerAndType(getOwner(), getType(), this);
    }

    @Inject
    CommunicationChannelRepository communicationChannelRepository;
    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelOwnerLinkRepository;

}
