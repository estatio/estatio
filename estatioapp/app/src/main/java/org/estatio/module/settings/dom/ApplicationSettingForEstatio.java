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

import javax.jdo.annotations.IdentityType;

import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainServiceLayout;

import org.estatio.module.settings.dom.ApplicationSetting;
import org.estatio.module.settings.dom.SettingType;

import org.incode.module.base.dom.types.DescriptionType;

import org.estatio.module.settings.dom.types.SettingKeyType;
import org.estatio.module.settings.dom.types.SettingTypeType;

import lombok.Getter;
import lombok.Setter;

@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.APPLICATION,
        table="ApplicationSetting"
        ,schema = "dbo"     // Isis' ObjectSpecId inferred from @DomainObject#objectType
)
@javax.jdo.annotations.Queries({ 
     @javax.jdo.annotations.Query(
             name = "findByKey", language = "JDOQL", 
             value = "SELECT "
                     + "FROM org.estatio.module.settings.dom.ApplicationSettingForEstatio "
                     + "WHERE key == :key"),
     @javax.jdo.annotations.Query(
            name = "findAll", language = "JDOQL",
            value = "SELECT "
                     + "FROM org.estatio.module.settings.dom.ApplicationSettingForEstatio "
                    + "ORDER BY key")
})
@DomainObject(
        objectType = "org.estatio.domsettings.ApplicationSettingForEstatio"
)
@DomainServiceLayout(named = "Application Setting")
public class ApplicationSettingForEstatio extends SettingAbstractForEstatio implements ApplicationSetting {

    @javax.jdo.annotations.Column(allowsNull="false", length= SettingKeyType.Meta.MAX_LEN)
    @javax.jdo.annotations.PrimaryKey
    @Getter @Setter
    private String key;

    // //////////////////////////////////////

    @javax.jdo.annotations.Column(length= DescriptionType.Meta.MAX_LEN)
    @javax.jdo.annotations.Persistent
    @Override
    public String getDescription() {
        return super.getDescription();
    }
    @Override
    public void setDescription(final String description) {
        super.setDescription(description);
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull="false")
    @javax.jdo.annotations.Persistent
    @Override
    public String getValueRaw() {
        return super.getValueRaw();
    }
    @Override
    public void setValueRaw(final String valueAsRaw) {
        super.setValueRaw(valueAsRaw);
    }
    
    // //////////////////////////////////////

    @javax.jdo.annotations.Column(allowsNull="false", length= SettingTypeType.Meta.MAX_LEN)
    @javax.jdo.annotations.Persistent
    @Override
    public SettingType getType() {
        return super.getType();
    }
    @Override
    public void setType(final SettingType type) {
        super.setType(type);
    }

}
