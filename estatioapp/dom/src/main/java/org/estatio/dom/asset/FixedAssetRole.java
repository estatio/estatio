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
package org.estatio.dom.asset;

import java.util.SortedSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import com.google.common.base.Function;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.RenderType;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithIntervalContiguous;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;

/**
 * Identifies the {@link #getParty() party} that plays a particular
 * {@link #getType() type} of role with respect to a {@link #getAsset() fixed
 * asset}, for a particular {@link #getInterval() interval of time}.
 */
@javax.jdo.annotations.PersistenceCapable(identityType = IdentityType.DATASTORE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByAssetAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.asset.FixedAssetRole "
                        + "WHERE asset == :asset "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(
                name = "findByAssetAndPartyAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.asset.FixedAssetRole "
                        + "WHERE asset == :asset "
                        + "&& party == :party "
                        + "&& type == :type")
})
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public class FixedAssetRole
        extends EstatioDomainObject<FixedAssetRole>
        implements WithIntervalContiguous<FixedAssetRole> {

    private WithIntervalContiguous.Helper<FixedAssetRole> helper =
            new WithIntervalContiguous.Helper<FixedAssetRole>(this);

    // //////////////////////////////////////

    public FixedAssetRole() {
        super("asset, startDate desc nullsLast, type, party");
    }

    // //////////////////////////////////////

    private FixedAsset asset;

    @javax.jdo.annotations.Column(name = "assetId", allowsNull = "false")
    @Title(sequence = "3", prepend = ":")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    public FixedAsset getAsset() {
        return asset;
    }

    public void setAsset(final FixedAsset asset) {
        this.asset = asset;
    }

    // //////////////////////////////////////

    private Party party;

    @javax.jdo.annotations.Column(name = "partyId", allowsNull = "false")
    @Title(sequence = "2", prepend = ":")
    @Property(hidden = Where.REFERENCES_PARENT, editing = Editing.DISABLED)
    public Party getParty() {
        return party;
    }

    public void setParty(final Party party) {
        this.party = party;
    }

    // //////////////////////////////////////

    private FixedAssetRoleType type;

    @javax.jdo.annotations.Column(allowsNull = "false", length = JdoColumnLength.TYPE_ENUM)
    @Property(editing = Editing.DISABLED)
    @Title(sequence = "1")
    public FixedAssetRoleType getType() {
        return type;
    }

    public void setType(final FixedAssetRoleType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private LocalDate startDate;

    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(final LocalDate startDate) {
        this.startDate = startDate;
    }

    @javax.jdo.annotations.Persistent
    private LocalDate endDate;

    @Property(editing = Editing.DISABLED, optionality = Optionality.OPTIONAL)
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDate endDate) {
        this.endDate = endDate;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.IDEMPOTENT)
    @Override
    public FixedAssetRole changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(named = "Start Date") LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) @ParameterLayout(named = "End Date") LocalDate endDate) {
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
        return getInterval();
    }

    // //////////////////////////////////////

    public boolean isCurrent() {
        return isActiveOn(getClockService().now());
    }

    private boolean isActiveOn(final LocalDate localDate) {
        return getInterval().contains(localDate);
    }

    // //////////////////////////////////////

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

    @CollectionLayout(render = RenderType.EAGERLY)
    @Override
    public SortedSet<FixedAssetRole> getTimeline() {
        return helper.getTimeline(getAsset().getRoles(), getType().matchingRole());
    }

    // //////////////////////////////////////

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
            final @ParameterLayout(named = "Start date") LocalDate startDate,
            final @ParameterLayout(named = "End date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
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
            final @ParameterLayout(named = "Start date") @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @ParameterLayout(named = "End date") LocalDate endDate) {

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

    // //////////////////////////////////////

    public final static class Functions {

        private Functions() {
        }

        /**
         * A {@link Function} that obtains the role's
         * {@link FixedAssetRole#getParty() party} attribute.
         */
        public static <T extends Party> Function<FixedAssetRole, T> partyOf() {
            return new Function<FixedAssetRole, T>() {
                @SuppressWarnings("unchecked")
                public T apply(final FixedAssetRole fixedAssetRole) {
                    return (T) (fixedAssetRole != null ? fixedAssetRole.getParty() : null);
                }
            };
        }

        /**
         * A {@link Function} that obtains the role's
         * {@link FixedAssetRole#getAsset() asset} attribute.
         */
        public static <T extends FixedAsset> Function<FixedAssetRole, T> assetOf() {
            return new Function<FixedAssetRole, T>() {
                @SuppressWarnings("unchecked")
                public T apply(final FixedAssetRole fixedAssetRole) {
                    return (T) (fixedAssetRole != null ? fixedAssetRole.getAsset() : null);
                }
            };
        }
    }

}
