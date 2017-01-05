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
package org.estatio.dom.agreement;

import java.util.List;
import java.util.SortedSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.collect.Lists;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.with.WithIntervalContiguous;
import org.incode.module.base.dom.utils.TitleBuilder;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannel;
import org.incode.module.communications.dom.impl.commchannel.CommunicationChannelOwner_newChannelContributions;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo"    // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE, column = "id")
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER, column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(name = "findByRoleAndTypeAndStartDate", language = "JDOQL", value = "SELECT "
                + "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannel "
                + "WHERE role == :agreementRole "
                + "&& type == :type "
                + "&& startDate == :startDate"),
        @javax.jdo.annotations.Query(name = "findByRoleAndTypeAndEndDate", language = "JDOQL", value = "SELECT "
                + "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannel "
                + "WHERE role == :agreementRole "
                + "&& type == :type "
                + "&& endDate == :endDate"),
        @javax.jdo.annotations.Query(name = "findByRoleAndType", language = "JDOQL", value = "SELECT "
                + "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannel "
                + "WHERE role == :role "
                + "&& type == :type "),
        @javax.jdo.annotations.Query(name = "findByRoleAndTypeAndContainsDate", language = "JDOQL", value = "SELECT "
                + "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannel "
                + "WHERE role == :role "
                + "&& type == :type "
                + "&& (startDate == null || startDate <= :date) "
                + "&& (endDate == null || endDate > :date) "),
        @javax.jdo.annotations.Query(name = "findByCommunicationChannel", language = "JDOQL", value = "SELECT "
                + "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannel "
                + "WHERE communicationChannel == :communicationChannel "),
        @javax.jdo.annotations.Query(
                name = "findByAgreement", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.agreement.AgreementRoleCommunicationChannel "
                        + "WHERE agreement == :agreement ") })
@Unique(name = "AgreementRoleCommunicationChannel_role_startDate_type_communicationChannel_UNQ", members = {
        "role", "startDate", "type", "communicationChannel" })
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "org.estatio.dom.agreement.AgreementRoleCommunicationChannel"
)
public class AgreementRoleCommunicationChannel
        extends UdoDomainObject2<AgreementRoleCommunicationChannel>
        implements WithIntervalContiguous<AgreementRoleCommunicationChannel>, WithApplicationTenancyProperty {

    private WithIntervalContiguous.Helper<AgreementRoleCommunicationChannel> helper =
            new WithIntervalContiguous.Helper<>(this);

    // //////////////////////////////////////

    public AgreementRoleCommunicationChannel() {
        super("role, startDate desc nullsLast, type, communicationChannel");
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getType())
                .withTupleElement(getRole())
                .withTupleElement(getCommunicationChannel())
                .toString();
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getRole().getApplicationTenancy();
    }

    // //////////////////////////////////////
    @javax.jdo.annotations.Column(name = "agreementRoleId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private AgreementRole role;

    public void modifyRole(final AgreementRole role) {
        AgreementRole currentRole = getRole();
        if (role == null || role.equals(currentRole)) {
            return;
        }
        setRole(role);
    }

    public void clearRole() {
        AgreementRole currentRole = getRole();
        if (currentRole == null) {
            return;
        }
        setRole(null);
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(defaultFetchGroup = "true")
    @javax.jdo.annotations.Column(name = "typeId", allowsNull = "false")
    @Getter @Setter
    private AgreementRoleCommunicationChannelType type;

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent(defaultFetchGroup = "true")
    @javax.jdo.annotations.Column(name = "communicationChannelId", allowsNull = "false")
    @PropertyLayout(hidden = Where.REFERENCES_PARENT)
    @Getter @Setter
    private CommunicationChannel communicationChannel;

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate startDate;

    @javax.jdo.annotations.Persistent
    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate endDate;

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public void remove() {
        AgreementRole agreementRole = this.getRole();
        getContainer().remove(this);

        return;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @Override
    public AgreementRoleCommunicationChannel changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        helper.changeDates(startDate, endDate);
        return this;
    }

    public String disableChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return null;
    }

    @Override
    public LocalDate default0ChangeDates() {
        return getStartDate();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return getEndDate();
    }

    @Override
    public String validateChangeDates(
            final LocalDate startDate,
            final LocalDate endDate) {
        return helper.validateChangeDates(startDate, endDate);
    }

    // //////////////////////////////////////

    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Override
    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return getInterval().overlap(getRole().getEffectiveInterval());
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate localDate) {
        return getEffectiveInterval().contains(localDate);
    }

    // //////////////////////////////////////

    @Property(optionality = Optionality.OPTIONAL, hidden = Where.ALL_TABLES)
    @Override
    public AgreementRoleCommunicationChannel getPredecessor() {
        return helper.getPredecessor(getRole().getCommunicationChannels(),
                getType().matchingCommunicationChannel());
    }

    @Property(hidden = Where.ALL_TABLES, optionality = Optionality.OPTIONAL)
    @Override
    public AgreementRoleCommunicationChannel getSuccessor() {
        return helper.getSuccessor(getRole().getCommunicationChannels(),
                getType().matchingCommunicationChannel());
    }

    @CollectionLayout(render = RenderType.EAGERLY)
    @Override
    public SortedSet<AgreementRoleCommunicationChannel> getTimeline() {
        return helper.getTimeline(getRole().getCommunicationChannels(),
                getType().matchingCommunicationChannel());
    }

    // //////////////////////////////////////

    static final class SiblingFactory implements
            WithIntervalContiguous.Factory<AgreementRoleCommunicationChannel> {
        private final AgreementRoleCommunicationChannel arcc;
        private final CommunicationChannel cc;

        public SiblingFactory(
                final AgreementRoleCommunicationChannel arcc,
                final CommunicationChannel cc) {
            this.arcc = arcc;
            this.cc = cc;
        }

        @Override
        public AgreementRoleCommunicationChannel newRole(
                final LocalDate startDate, final LocalDate endDate) {
            return arcc.getRole().createAgreementRoleCommunicationChannel(
                    arcc.getType(), cc, startDate, endDate);
        }
    }

    public AgreementRoleCommunicationChannel succeededBy(
            final CommunicationChannel communicationChannel,
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return helper.succeededBy(startDate, endDate, new SiblingFactory(this,
                communicationChannel));
    }

    public List<CommunicationChannel> choices0SucceededBy() {
        return Lists.newArrayList(communicationChannelsForRolesParty());
    }

    public CommunicationChannel default0SucceededBy() {
        final SortedSet<CommunicationChannel> partyChannels = communicationChannelsForRolesParty();
        return !partyChannels.isEmpty() ? partyChannels.first() : null;
    }

    public LocalDate default1SucceededBy() {
        return helper.default1SucceededBy();
    }

    public String validateSucceededBy(
            final CommunicationChannel communicationChannel,
            final LocalDate startDate, final LocalDate endDate) {
        String invalidReasonIfAny = helper.validateSucceededBy(startDate,
                endDate);
        if (invalidReasonIfAny != null) {
            return invalidReasonIfAny;
        }

        if (communicationChannel == getCommunicationChannel()) {
            return "Successor's communication channel cannot be the same as this object's communication channel";
        }
        final AgreementRoleCommunicationChannel successor = getSuccessor();
        if (successor != null
                && communicationChannel == successor.getCommunicationChannel()) {
            return "Successor's communication channel cannot be the same as that of existing successor";
        }
        final SortedSet<CommunicationChannel> partyChannels = communicationChannelsForRolesParty();
        if (!partyChannels.contains(communicationChannel)) {
            return "Successor's communication channel must be one of those of the parent role's party";
        }

        return null;
    }

    public AgreementRoleCommunicationChannel precededBy(
            final CommunicationChannel communicationChannel,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final LocalDate endDate) {

        return helper.precededBy(startDate, endDate, new SiblingFactory(this,
                communicationChannel));
    }

    public List<CommunicationChannel> choices0PrecededBy() {
        return Lists.newArrayList(communicationChannelsForRolesParty());
    }

    public CommunicationChannel default0PrecededBy() {
        final SortedSet<CommunicationChannel> partyChannels = communicationChannelsForRolesParty();
        return !partyChannels.isEmpty() ? partyChannels.first() : null;
    }

    public LocalDate default2PrecededBy() {
        return helper.default2PrecededBy();
    }

    public String validatePrecededBy(
            final CommunicationChannel communicationChannel,
            final LocalDate startDate, final LocalDate endDate) {
        final String invalidReasonIfAny = helper.validatePrecededBy(startDate,
                endDate);
        if (invalidReasonIfAny != null) {
            return invalidReasonIfAny;
        }

        if (communicationChannel == getCommunicationChannel()) {
            return "Successor's communication channel cannot be the same as this object's communication channel";
        }
        final AgreementRoleCommunicationChannel predecessor = getPredecessor();
        if (predecessor != null
                && communicationChannel == predecessor
                .getCommunicationChannel()) {
            return "Predecessor's communication channel cannot be the same as that of existing predecessor";
        }
        final SortedSet<CommunicationChannel> partyChannels = communicationChannelsForRolesParty();
        if (!partyChannels.contains(communicationChannel)) {
            return "Predecessor's communication channel must be one of those of the parent role's party";
        }
        return null;
    }

    // //////////////////////////////////////

    private SortedSet<CommunicationChannel> communicationChannelsForRolesParty() {
        return communicationChannelContributions
                .communicationChannels(getRole().getParty());
    }

    // //////////////////////////////////////

    @ActionLayout(describedAs = "Change Communication Channel Type")
    public AgreementRoleCommunicationChannel changeType(
            final @Parameter(optionality = Optionality.OPTIONAL) AgreementRoleCommunicationChannelType type) {
        setType(type);
        return this;
    }

    public AgreementRoleCommunicationChannelType default0ChangeType() {
        return getType();
    }

    // //////////////////////////////////////

    @javax.inject.Inject
    private CommunicationChannelOwner_newChannelContributions communicationChannelContributions;

}
