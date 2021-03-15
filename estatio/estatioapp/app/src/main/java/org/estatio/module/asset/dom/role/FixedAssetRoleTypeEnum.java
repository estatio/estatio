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

import com.google.common.base.Predicate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.base.dom.TitledEnum;

import org.estatio.module.party.dom.role.IPartyRoleType;
import org.estatio.module.party.dom.role.PartyRoleTypeServiceSupportAbstract;

public enum FixedAssetRoleTypeEnum implements TitledEnum, IPartyRoleType {

    PROPERTY_OWNER,
    PROPERTY_MANAGER, 
    ASSET_MANAGER,
    PROPERTY_CONTACT,
    TENANTS_ASSOCIATION,
    CENTER_MANAGER,
    INV_APPROVAL_DIRECTOR,    // meant for invoices above threshold approved by center manager
    PROPERTY_INV_MANAGER,
    TURNOVER_REPORTER,
    PROPERTY_ADVISOR // meant for external users that must have rights to access incoming invoices when asked for advice
    ;

    @Override
    public String getKey() {
        return this.name();
    }

    @Programmatic
    public Predicate<? super FixedAssetRole> matchingRole() {
        return (Predicate<FixedAssetRole>) far -> far != null && far.getType() == FixedAssetRoleTypeEnum.this;
    }

    @DomainService(nature = NatureOfService.DOMAIN)
    public static class SupportService
            extends PartyRoleTypeServiceSupportAbstract<FixedAssetRoleTypeEnum> {
        public SupportService() {
            super(FixedAssetRoleTypeEnum.class);
        }

    }

}
