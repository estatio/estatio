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
package org.estatio.module.asset.fixtures.property.builders;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRole;
import org.estatio.module.asset.dom.role.FixedAssetRoleTypeEnum;
import org.estatio.module.party.dom.Party;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"property", "owner"}, callSuper = false)
@Accessors(chain = true)
public class PropertyOwnerBuilder
        extends BuilderScriptAbstract<FixedAssetRole, PropertyOwnerBuilder> {

    @Getter @Setter
    private Property property;

    @Getter @Setter
    private Party owner;

    @Getter @Setter
    private LocalDate startDate;

    @Getter @Setter
    private LocalDate endDate;

    @Getter
    private FixedAssetRole object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("property", executionContext, Property.class);
        checkParam("owner", executionContext, Party.class);

        //wrap(property).newRole(FixedAssetRoleTypeEnum.ASSET_MANAGER, manager, startDate, endDate);
        object = property.addRoleIfDoesNotExist(owner, FixedAssetRoleTypeEnum.PROPERTY_OWNER, startDate, endDate);
    }
}
