/*
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
package org.estatio.module.application.seed;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

/**
 * Apologies: have chosen to load in the DocFragment entities this way rather than using flywaydb mostly because
 * of the difficult of writing an INSERT SQL statement for large text fields.  Also, there would be code duplication that way.
 */
@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "99")
public class DocumentTypesAndTemplatesSeedService {

    @PostConstruct
    public void init() {

        if(System.getProperty("estatio.integTest") != null) {
            return;
        }

        // If we get new versions of the templates in the future, then update this date
        // the upsert logic here should result in the new templates being inserted alongside existing
        // (with a different template date).

        //
        // using this date to ensure that prototyping still works (epoch date set back then)
        //
        final LocalDate templateDate = new LocalDate(2012,1,1);

        fixtureScripts.runFixtureScript(new DocumentTypesAndTemplatesFixture(templateDate), null);
    }

    @Inject
    FixtureScripts fixtureScripts;
}

