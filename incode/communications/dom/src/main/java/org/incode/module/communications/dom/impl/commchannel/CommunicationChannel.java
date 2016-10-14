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
package org.incode.module.communications.dom.impl.commchannel;

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

import org.incode.module.base.dom.types.DescriptionType;
import org.incode.module.base.dom.types.ReferenceType;

import org.estatio.dom.UdoDomainObject2;
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
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        , schema = "incodeCommunications"
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator")
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReferenceAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.communications.dom.impl.commchannel.CommunicationChannel "
                        + "WHERE reference == :reference "
                        + "&& type == :type")
})
@DomainObject(editing = Editing.DISABLED)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public abstract class CommunicationChannel
        extends UdoDomainObject2<CommunicationChannel>
        implements WithNameGetter, WithReferenceGetter, WithApplicationTenancyCountry {

    public static class PhoneNumberType {

        private PhoneNumberType() {}

        public static final String REGEX = "[+]?[0-9 -]*";
        public static final String REGEX_DESC = "Only numbers and two symbols being \"-\" and \"+\" are allowed ";

        public final static int MAX_LEN = 20;

    }

    public static class EmailType {

        // as per http://emailregex.com/
        // better would probably be:
        // (?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])
        public static final String REGEX = "[^@ ]*@{1}[^@ ]*[.]+[^@ ]*";
        public static final String REGEX_DESC = "Only one \"@\" symbol is allowed, followed by a domain e.g. test@example.com";

        public final static int MAX_LEN = 254; //http://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address

        private EmailType(){}

    }

    // //////////////////////////////////////


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
    @javax.jdo.annotations.Column(allowsNull = "false", length = CommunicationChannelType.Type.MAX_LEN)
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private CommunicationChannelType type;

    // //////////////////////////////////////

    /**
     * For import purposes only
     */
    @javax.jdo.annotations.Column(allowsNull = "true", length = ReferenceType.Meta.MAX_LEN)
    @Property(hidden = Where.EVERYWHERE)
    @Getter @Setter
    private String reference;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(length = DescriptionType.Meta.MAX_LEN)
    @Property(optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    @PropertyLayout(multiLine = 3)
    @Getter @Setter
    private String description;

    // //////////////////////////////////////

    @MemberOrder(sequence = "3")
    @Getter @Setter
    private boolean legal;

    // //////////////////////////////////////

    @Column(allowsNull = "true", length = CommunicationChannelPurposeType.Meta.MAX_LEN)
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
