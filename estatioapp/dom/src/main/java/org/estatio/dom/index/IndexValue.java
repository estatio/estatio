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
package org.estatio.dom.index;

import java.math.BigDecimal;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.services.eventbus.ActionDomainEvent;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.EstatioDomainObject;
import org.estatio.dom.JdoColumnScale;
import org.estatio.dom.WithStartDate;
import org.estatio.dom.apptenancy.WithApplicationTenancyCountry;
import org.estatio.dom.utils.TitleBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds the {@link #getValue() value} of an {@link #getIndexBase() index
 * (base)} from a particular {@link #getStartDate() point in time} (until
 * succeeded by some other value).
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
                name = "findByIndexAndStartDate", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.index.IndexValue "
                        + "WHERE indexBase.index == :index "
                        + "   && startDate == :startDate"),
        @javax.jdo.annotations.Query(
                name = "findLastByIndex", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.index.IndexValue "
                        + "WHERE indexBase.index == :index "
                        + "ORDER BY startDate DESC")
})
@javax.jdo.annotations.Unique(
        name = "IndexValue_indexBase_startDate_IDX",
        members = { "indexBase", "startDate" })
public class IndexValue
        extends EstatioDomainObject<IndexValue>
        implements WithStartDate, WithApplicationTenancyCountry {

    public IndexValue() {
        super("indexBase, startDate desc");
    }

    public String title() {
        return TitleBuilder.start()
                .withName(getStartDate())
                .withParent(getIndexBase())
                .toString();
    }

    @PropertyLayout(
            named = "Application Level",
            describedAs = "Determines those users for whom this object is available to view and/or modify."
    )
    public ApplicationTenancy getApplicationTenancy() {
        return getIndexBase().getApplicationTenancy();
    }

    @javax.jdo.annotations.Persistent
    @javax.jdo.annotations.Column(allowsNull = "false")
    @Getter @Setter
    private LocalDate startDate;


    @javax.jdo.annotations.Column(name = "indexBaseId", allowsNull = "false")
    @PropertyLayout(hidden = Where.PARENTED_TABLES)
    @Getter @Setter
    private IndexBase indexBase;

    @javax.jdo.annotations.Column(scale = JdoColumnScale.IndexValue.INDEX_VALUE, allowsNull = "false")
    @Getter @Setter
    private BigDecimal value;

    @Action(semantics = SemanticsOf.NON_IDEMPOTENT, domainEvent = UpdateEvent.class)
    public void remove() {
        getContainer().remove(this);
    }

    public static class UpdateEvent extends ActionDomainEvent<IndexValue> {
        private static final long serialVersionUID = 1L;
    }

}