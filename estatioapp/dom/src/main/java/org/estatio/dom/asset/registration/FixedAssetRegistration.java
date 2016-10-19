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
package org.estatio.dom.asset.registration;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.VersionStrategy;

import org.apache.commons.lang3.ObjectUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.Chained;
import org.incode.module.base.dom.WithIntervalMutable;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import org.estatio.dom.UdoDomainObject2;
import org.estatio.dom.apptenancy.WithApplicationTenancyProperty;
import org.estatio.dom.asset.FixedAsset;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        schema = "EstatioAssetRegistration" // Isis' ObjectSpecId inferred from @Discriminator
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.dom.asset.registration.FixedAssetRegistration"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findBySubject", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.asset.registration.FixedAssetRegistration "
                        + "WHERE subject == :subject")
})
@DomainObject()
public abstract class FixedAssetRegistration
        extends UdoDomainObject2<FixedAssetRegistration>
        implements WithIntervalMutable<FixedAssetRegistration>, Chained<FixedAssetRegistration>, WithApplicationTenancyProperty {

    public FixedAssetRegistration() {
        super("subject,type");
    }

    // //////////////////////////////////////

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getSubject().getApplicationTenancy();
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "subjectId", allowsNull = "false")
    @MemberOrder(sequence = "1")
    @Getter @Setter
    private FixedAsset subject;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(name = "typeId", allowsNull = "false")
    @Getter @Setter
    private FixedAssetRegistrationType type;

    // //////////////////////////////////////

    public String getName() {
        return null;
    }

    // //////////////////////////////////////

    @Column(allowsNull = "true")
    @MemberOrder(sequence = "3")
    @Getter @Setter
    private LocalDate startDate;

    // //////////////////////////////////////

    @Column(allowsNull = "true")
    @MemberOrder(sequence = "4")
    @Getter @Setter
    private LocalDate endDate;

    // //////////////////////////////////////

    private WithIntervalMutable.Helper<FixedAssetRegistration> changeDates = new WithIntervalMutable.Helper<>(this);

    WithIntervalMutable.Helper<FixedAssetRegistration> getChangeDates() {
        return changeDates;
    }

    public FixedAssetRegistration changeDates(
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate startDate,
            final @Parameter(optionality = Optionality.OPTIONAL) LocalDate endDate) {
        if (getPrevious() != null) {
            getPrevious().getChangeDates().changeDates(getPrevious().getStartDate(), startDate.minusDays(1));
        }

        return getChangeDates().changeDates(startDate, endDate);
    }

    public String validateChangeDates(final LocalDate startDate, final LocalDate endDate) {
        return ObjectUtils.firstNonNull(
                getChangeDates().validateChangeDates(startDate, endDate),
                getPrevious() == null ? null : getPrevious().getChangeDates().validateChangeDates(getPrevious().getStartDate(), startDate),
                getNext() == null ? null : getNext().getChangeDates().validateChangeDates(startDate, getNext().getEndDate())
                );
    }

    @Override
    public LocalDate default0ChangeDates() {
        return getStartDate();
    }

    @Override
    public LocalDate default1ChangeDates() {
        return getEndDate();
    }

    @Programmatic
    public LocalDateInterval getInterval() {
        return LocalDateInterval.including(getStartDate(), getEndDate());
    }

    @Override
    public LocalDateInterval getEffectiveInterval() {
        return getInterval();
    }

    @Override
    public boolean isCurrent() {
        return getInterval().contains(getClockService().now());
    }

    // //////////////////////////////////////

    @Persistent(mappedBy = "next")
    @Column(name = "previousFixedAssetRegistrationId", allowsNull = "true")
    private FixedAssetRegistration previous;

    public FixedAssetRegistration getPrevious() {
        return previous;
    }

    public void setPrevious(FixedAssetRegistration previous) {
        this.previous = previous;
    }

    // //////////////////////////////////////

    @Column(name = "nextFixedAssetRegistrationId", allowsNull = "true")
    private FixedAssetRegistration next;

    public FixedAssetRegistration getNext() {
        return next;
    }

    public void setNext(FixedAssetRegistration next) {
        this.next = next;
    }

    // //////////////////////////////////////

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public FixedAsset remove() {
        FixedAsset fixedAsset = getSubject();
        getContainer().remove(this);
        getContainer().flush();
        return fixedAsset;
    }

}