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

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.Programmatic;
import org.estatio.dom.EstatioDomainService;

@DomainService(menuOrder = "10", repositoryFor = LandRegister.class)
public class LandRegisters extends EstatioDomainService<LandRegister> {

    public LandRegisters() {
        super(LandRegisters.class, LandRegister.class);
    }

    // //////////////////////////////////////

    /**
     * for migration API only
     */
    @Programmatic
    public LandRegister newLandRegister() {
        return null;
    }

    @PostConstruct
    @Programmatic
    public void init(Map<String, String> properties) {
        fixedAssetRegistrationTypes.findOrCreate(LandRegisterConstants.FART_LAND_REGISTER, LandRegister.class);
    }

    // //////////////////////////////////////

    @Inject
    private FixedAssetRegistrationTypes fixedAssetRegistrationTypes;

}
