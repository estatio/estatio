/*
 *  Copyright 2014 Eurocommercial Properties NV
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
package org.estatio.module.base.seed;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

/**
 * Installs security seed data on application startup.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class EstatioSecurityModuleSeedService {

    @PostConstruct
    public void init() {

        if(System.getProperty("isis.headless") != null) {
            return;
        }

        fixtureScripts.runFixtureScript(new EstatioSecurityModuleSeedFixture(), null);
    }

    @Inject
    FixtureScripts fixtureScripts;

}
