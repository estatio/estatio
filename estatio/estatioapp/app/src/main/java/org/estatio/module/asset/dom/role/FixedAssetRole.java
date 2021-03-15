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
package org.estatio.module.asset.dom.role;

import java.math.BigDecimal;
import java.util.SortedSet;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
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
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.TitleBuffer;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithIntervalContiguous;

import org.estatio.module.base.dom.UdoDomainObject2;
import org.estatio.module.base.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.base.dom.EstatioRole;

import lombok.Getter;
import lombok.Setter;

/**
 * Identifies the {@link #getParty() party} that plays a particular
 * {@link #getType() type} of role with respect to a {@link #getAsset() fixed
 * asset}, for a particular {@link #getInterval() interval of time}.
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.DATASTORE
        ,schema = "dbo" // Isis' ObjectSpecId inferred @DomainObject#objectType
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.role.FixedAssetRole "
                        + "WHERE type == :type"),
        @javax.jdo.annotations.Query(
                name = "findByAssetAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.role.FixedAssetRole "
                        + "WHERE asset == :asset "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(
                name = "findByAssetAndPartyAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.role.FixedAssetRole "
                        + "WHERE asset == :asset "
                        + "&& party == :party "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(
                name = "findAllForProperty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.role.FixedAssetRole "
                        + "WHERE asset == :asset"),
        @javax.jdo.annotations.Query(
                name = "findByPartyAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.role.FixedAssetRole "
                        + "WHERE party == :party && type == :type"),
        @javax.jdo.annotations.Query(
                name = "findByParty", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.module.asset.dom.role.FixedAssetRole "
                        + "WHERE party == :party ")
})
@DomainObject(
        objectType = "org.estatio.dom.asset.FixedAssetRole"  // backward compatibility
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public class FixedAssetRole
        extends UdoDomainObject2<FixedAssetRole>
        implements WithIntervalContiguous<FixedAssetRole>, WithApplicationTenancyProperty {

    private WithIntervalContiguous.Helper<FixedAssetRole> helper =
            new WithIntervalContiguous.Helper<>(this);

    public String title() {
        final TitleBuffer buf = new TitleBuffer()
                .append(titleService.titleOf(getParty()))
                .append(titleService.titleOf(getType()))
                .append("for")
                .append(titleService.titleOf(getAsset()));
        return buf.toString();
    }



    public FixedAssetRole() {
        super("asset, startDate desc nullsLast, type, party");
    }

    public FixedAssetRole(FixedAsset asset, Party party, FixedAssetRoleTypeEnum type) {
        this();
        this.asset = asset;
        this.party = party;
        this.type = type;
    }



    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getAsset().getApplicationTenancy();
    }


    @javax.jdo.annotations.Column(name = "assetId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    @Getter @Setter
    private FixedAsset asset;

    @javax.jdo.annotations.Column(name = "partyId", allowsNull = "false")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    @Getter @Setter
    private Party party;

    @javax.jdo.annotations.Column(allowsNull = "false", length = IPartyRoleType.Meta.MAX_LEN)
    @Property(editing = Editing.DISABLED)
    @Getter @Setter
    private FixedAssetRoleTypeEnum type;

    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private LocalDate startDate;

    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @javax.jdo.annotations.Persistent
    @Getter @Setter
    private LocalDate endDate;

    @Property(optionality = Optionality.OPTIONAL)
    @Getter @Setter
    private BigDecimal ownershipShare;




    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @Override
    public FixedAssetRole changeDates(
            @Nullable final LocalDate startDate,
            @Nullable final LocalDate endDate) {
        helper.changeDates(startDate, endDate);
        return this;
    }
    public String disableChangeDates() {
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
    public String validateChangeDates(final LocalDate startDate, final LocalDate endDate) {
        return helper.validateChangeDates(startDate, endDate);
    }


    @Override
    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Override
    @Programmatic
    public LocalDateInterval getEffectiveInterval() {
        return getInterval();
    }




    @Property()
    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate localDate) {
        return getInterval().contains(localDate);
    }



    @Property(hidden = Where.ALL_TABLES, editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @Override
    public FixedAssetRole getPredecessor() {
        return helper.getPredecessor(getAsset().getRoles(), getType().matchingRole());
    }

    @Property(hidden = Where.ALL_TABLES, editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @Override
    public FixedAssetRole getSuccessor() {
        return helper.getSuccessor(getAsset().getRoles(), getType().matchingRole());
    }

    @CollectionLayout(defaultView = "table")
    @Override
    public SortedSet<FixedAssetRole> getTimeline() {
        return helper.getTimeline(getAsset().getRoles(), getType().matchingRole());
    }






    static final class SiblingFactory implements WithIntervalContiguous.Factory<FixedAssetRole> {
        private final FixedAssetRole far;
        private final Party party;

        public SiblingFactory(final FixedAssetRole far, final Party party) {
            this.far = far;
            this.party = party;
        }

        @Override
        public FixedAssetRole newRole(final LocalDate startDate, final LocalDate endDate) {
            return far.getAsset().createRole(far.getType(), party, startDate, endDate);
        }
    }

    public FixedAssetRole succeededBy(
            final Party party,
            final LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        return helper.succeededBy(startDate, endDate, new SiblingFactory(this, party));
    }

    public LocalDate default1SucceededBy() {
        return helper.default1SucceededBy();
    }

    public String validateSucceededBy(
            final Party party,
            final LocalDate startDate,
            final LocalDate endDate) {
        String invalidReasonIfAny = helper.validateSucceededBy(startDate, endDate);
        if (invalidReasonIfAny != null) {
            return invalidReasonIfAny;
        }

        if (party == getParty()) {
            return "Successor's party cannot be the same as this object's party";
        }
        final FixedAssetRole successor = getSuccessor();
        if (successor != null && party == successor.getParty()) {
            return "Successor's party cannot be the same as that of existing successor";
        }
        return null;
    }

    public FixedAssetRole precededBy(
            final Party party,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final LocalDate endDate) {

        return helper.precededBy(startDate, endDate, new SiblingFactory(this, party));
    }

    public LocalDate default2PrecededBy() {
        return helper.default2PrecededBy();
    }

    public String validatePrecededBy(
            final Party party,
            final LocalDate startDate,
            final LocalDate endDate) {
        final String invalidReasonIfAny = helper.validatePrecededBy(startDate, endDate);
        if (invalidReasonIfAny != null) {
            return invalidReasonIfAny;
        }

        if (party == getParty()) {
            return "Predecessor's party cannot be the same as this object's party";
        }
        final FixedAssetRole predecessor = getPredecessor();
        if (predecessor != null && party == predecessor.getParty()) {
            return "Predecessor's party cannot be the same as that of existing predecessor";
        }
        return null;
    }



    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public FixedAsset delete() {
        FixedAsset fa = getAsset();
        remove(this);
        return fa;
    }

    public boolean hideDelete() {
        return !EstatioRole.ADMINISTRATOR.isApplicableFor(getUser());
    }



    @Inject
    TitleService titleService;
}
