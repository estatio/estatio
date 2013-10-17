/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;

import org.estatio.dom.PowerType;
import org.estatio.dom.utils.StringUtils;

public enum FixedAssetRegistrationType implements PowerType<FixedAssetRegistration> {

    CADASTRAL_REGISTRATION(CadastralRegistration.class);

    private final Class<? extends FixedAssetRegistration> clss;

    private FixedAssetRegistrationType(final Class<? extends FixedAssetRegistration> clss) {
        this.clss = clss;
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

    // //////////////////////////////////////

    public FixedAssetRegistration create(final DomainObjectContainer container){ 
        try {
            FixedAssetRegistration registration = container.newTransientInstance(clss);
            return registration;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }
    
}
