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

package org.estatio.module.settings.dom;

import org.joda.time.LocalDate;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.settings.dom.SettingType;

import lombok.Getter;
import lombok.Setter;

/**
 * Factors out common implementation; however this is NOT annotated with 
 * {@link javax.jdo.annotations.PersistenceCapable}, so that each subclass is its own root entity.
 */
public abstract class SettingAbstractForEstatio 
    extends org.estatio.module.settings.dom.SettingAbstract
    implements org.estatio.module.settings.dom.ApplicationSetting {

    // //////////////////////////////////////

    @Getter @Setter
    private String description;

    @MemberOrder(name="Description", sequence="1")
    @ActionLayout(named = "Update")
    public SettingAbstractForEstatio updateDescription(
            final @Parameter(optionality = Optionality.OPTIONAL) String description) {
        setDescription(description);
        return this;
    }
    public String default0UpdateDescription() {
        return getDescription();
    }
    
    // //////////////////////////////////////

    @Getter @Setter
    private SettingType type;

    // //////////////////////////////////////

    @Getter @Setter
    private String valueRaw;

    // //////////////////////////////////////
    
    @MemberOrder(name="ValueAsString", sequence="1")
    @ActionLayout(named = "Update")
    public SettingAbstractForEstatio updateAsString(
            final String value) {
        setValueRaw(value);
        return this;
    }
    public String default0UpdateAsString() {
        return getValueAsString();
    }
    public boolean hideUpdateAsString() {
        return typeIsNot(SettingType.STRING);
    }
    
    @MemberOrder(name="ValueAsInt", sequence="1")
    @ActionLayout(named = "Update")
    public SettingAbstractForEstatio updateAsInt(
            final Integer value) {
        setValueRaw(value.toString());
        return this;
    }
    public Integer default0UpdateAsInt() {
        return getValueAsInt();
    }
    public boolean hideUpdateAsInt() {
        return typeIsNot(SettingType.INT);
    }
    
    @MemberOrder(name="ValueAsLong", sequence="1")
    @ActionLayout(named = "Update")
    public SettingAbstractForEstatio updateAsLong(
            final Long value) {
        setValueRaw(value.toString());
        return this;
    }
    public Long default0UpdateAsLong() {
        return getValueAsLong();
    }
    public boolean hideUpdateAsLong() {
        return typeIsNot(SettingType.LONG);
    }
    
    @MemberOrder(name="ValueAsLocalDate", sequence="1")
    @ActionLayout(named = "Update")
    public SettingAbstractForEstatio updateAsLocalDate(
            final LocalDate value) {
        setValueRaw(value.toString(DATE_FORMATTER));
        return this;
    }
    public LocalDate default0UpdateAsLocalDate() {
        return getValueAsLocalDate();
    }
    public boolean hideUpdateAsLocalDate() {
        return typeIsNot(SettingType.LOCAL_DATE);
    }

    @MemberOrder(name="ValueAsBoolean", sequence="1")
    @ActionLayout(named = "Update")
    public SettingAbstractForEstatio updateAsBoolean(
            final Boolean value) {
        setValueRaw(value.toString());
        return this;
    }
    public Boolean default0UpdateAsBoolean() {
        return getValueAsBoolean();
    }
    public boolean hideUpdateAsBoolean() {
        return typeIsNot(SettingType.BOOLEAN);
    }
    
    // //////////////////////////////////////
    
    @Action(semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE)
    public SettingAbstractForEstatio delete() {
        container.remove(this);
        container.informUser("Setting deleted");
        return null;
    }
    
 
    // //////////////////////////////////////
    
    private DomainObjectContainer container;

    public void setDomainObjectContainer(final DomainObjectContainer container) {
        this.container = container;
    }


}
