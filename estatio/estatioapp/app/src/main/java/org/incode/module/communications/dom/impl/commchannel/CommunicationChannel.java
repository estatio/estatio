package org.incode.module.communications.dom.impl.commchannel;

import java.util.List;
import java.util.SortedSet;

import javax.inject.Inject;
import javax.jdo.JDOHelper;
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
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import org.isisaddons.module.security.dom.tenancy.HasAtPath;

import org.incode.module.base.dom.managed.HasManagedInAndExternalReference;
import org.incode.module.base.dom.managed.ManagedIn;
import org.incode.module.base.dom.types.DescriptionType;
import org.incode.module.base.dom.types.ReferenceType;
import org.incode.module.base.dom.with.WithCodeGetter;
import org.incode.module.base.dom.with.WithDescriptionGetter;
import org.incode.module.base.dom.with.WithNameGetter;
import org.incode.module.base.dom.with.WithReferenceGetter;
import org.incode.module.base.dom.with.WithTitleGetter;

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
        , schema = "IncodeCommunications"   // Isis' ObjectSpecId inferred from @Discriminator
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
        implements Comparable<CommunicationChannel>,
        HasAtPath, HasManagedInAndExternalReference,
        WithNameGetter, WithReferenceGetter {

    // //////////////////////////////////////

    public static class PhoneNumberType {
        private PhoneNumberType() {}

        /**
         * @deprecated use {@link Meta#REGEX} instead
         */
        @Deprecated
        public static final String REGEX = Meta.REGEX;
        /**
         * @deprecated use {@link Meta#REGEX} instead
         */
        @Deprecated
        public static final String REGEX_DESC = Meta.REGEX_DESC;
        /**
         * @deprecated use {@link Meta#MAX_LEN} instead
         */
        @Deprecated
        public final static int MAX_LEN = Meta.MAX_LEN;

        public static class Meta {
            private Meta(){}
            public static final String REGEX = "[+]?[0-9 -]*";
            public static final String REGEX_DESC = "Only numbers and two symbols being \"-\" and \"+\" are allowed ";
            public final static int MAX_LEN = 20;
        }
    }

    public static class EmailType {
        private EmailType(){}

        /**
         * @deprecated use {@link Meta#REGEX} instead
         */
        @Deprecated
        public static final String REGEX = Meta.REGEX;
        /**
         * @deprecated use {@link Meta#REGEX_DESC} instead
         */
        @Deprecated
        public static final String REGEX_DESC = Meta.REGEX_DESC;
        /**
         * @deprecated use {@link Meta#MAX_LEN} instead
         */
        @Deprecated
        public final static int MAX_LEN = Meta.MAX_LEN;

        public static class Meta {
            private Meta(){}

            //
            // as per http://emailregex.com/
            //
            // better would probably be:
            // (?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])
            //
            public static final String REGEX = "[^@ ]*@{1}[^@ ]*[.]+[^@ ]*";

            public static final String REGEX_DESC = "Only one \"@\" symbol is allowed, followed by a domain e.g. test@example.com";

            //
            //http://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address
            //
            public final static int MAX_LEN = 254;

        }

    }

    // //////////////////////////////////////

    protected static ObjectContracts UDO_OBJECT_CONTRACTS =
            new ObjectContracts()
                    .with(WithReferenceGetter.ToString.evaluator())
                    .with(WithCodeGetter.ToString.evaluator())
                    .with(WithNameGetter.ToString.evaluator())
                    .with(WithTitleGetter.ToString.evaluator())
                    .with(WithDescriptionGetter.ToString.evaluator());

    // //////////////////////////////////////

    private final static String KEY_PROPERTIES = "type, legal, id";

    public CommunicationChannel() {
    }

    // //////////////////////////////////////

    public String iconName() {
        return getType().title().replace(" ", "");
    }

    // //////////////////////////////////////

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


    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level Path",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    @Override
    public String getAtPath() {
        final CommunicationChannelOwner owner = getOwner();
        return owner != null ? owner.getAtPath() : null;
    }


    /**
     * Inferred from {@link #getPurpose()}.
     */
    @Override
    public ManagedIn getManagedIn() {
        return getPurpose() != null ? getPurpose().getManagedIn() : null;
    }

    /**
     * Inferred from {@link #getExternalReference()}, but only if {@link #getManagedIn()} is also known.
     */
    @Override
    public String getManagedInExternalReference() {
        return getManagedIn() != null ? getExternalReference() : null;
    }

    // //////////////////////////////////////

    @Property(hidden = Where.OBJECT_FORMS)
    public String getName() {
        return titleService.titleOf(this);
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
        communicationChannelOwnerLinkRepository.createLink(this, owner);
    }

    private void removeOwnerLink() {
        final CommunicationChannelOwnerLink ownerLink = getOwnerLink();
        if(ownerLink != null) {
            repositoryService.remove(ownerLink);
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

    /**
     * To keep track of CommunicationChannels that are managed by external systems.
     *
     * <p>
     * The meaning of this id is opaque to Estatio.
     * In the case of CODA-managed communication channels, this field holds the "tag",
     * and is unique only within a ({@link #getOwner() owner}, {@link #getType() type}) pair.
     * </p>
     */
    @javax.jdo.annotations.Column(allowsNull = "true", length = 18)
    @Property
    @Getter @Setter
    private String externalReference;


    // //////////////////////////////////////

    @javax.jdo.annotations.Column(length = DescriptionType.Meta.MAX_LEN)
    @Property(optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    @PropertyLayout(multiLine = 3)
    @Getter @Setter
    private String description;

    // //////////////////////////////////////

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
    public List<CommunicationChannelPurposeType> choices2Change() {
        return CommunicationChannelPurposeType.managedIn(ManagedIn.ESTATIO);
    }


    // //////////////////////////////////////

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
        repositoryService.remove(this);
    }

    public SortedSet<CommunicationChannel> choices0Remove() {
        return communicationChannelRepository.findOtherByOwnerAndType(getOwner(), getType(), this);
    }


    // //////////////////////////////////////


    @Override
    public String toString() {
        return UDO_OBJECT_CONTRACTS.toStringOf(this, KEY_PROPERTIES);
    }

    @Override
    public int compareTo(final CommunicationChannel other) {
        return ObjectContracts.compare(this, other, KEY_PROPERTIES);
    }

    // //////////////////////////////////////

    @Inject
    CommunicationChannelRepository communicationChannelRepository;
    @Inject
    CommunicationChannelOwnerLinkRepository communicationChannelOwnerLinkRepository;
    @Inject
    RepositoryService repositoryService;
    @Inject
    TitleService titleService;



}
