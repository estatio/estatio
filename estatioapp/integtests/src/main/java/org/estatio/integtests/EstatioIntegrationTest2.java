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
package org.estatio.integtests;

import javax.inject.Inject;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.isis.applib.services.xactn.TransactionService;
import org.apache.isis.core.integtestsupport.IntegrationTestAbstract2;

import org.isisaddons.module.command.dom.BackgroundCommandServiceJdoRepository;

import org.estatio.app.EstatioAppManifest2;
import org.estatio.module.base.platform.applib.TickingFixtureClock;
import org.estatio.integtests.fakes.EstatioIntegTestFakeServicesModule;

/**
 * Base class for integration tests.
 */
public abstract class EstatioIntegrationTest2 extends IntegrationTestAbstract2 {

    private static final Logger LOG = LoggerFactory.getLogger(EstatioIntegrationTest2.class);

    @BeforeClass
    public static void initClass() {

        // TODO: almost the same as EstatioIntegrationTest, but doesn't run fixtures as 'estatio-admin' (use sudo service?)
        bootstrapUsing(EstatioAppManifest2.BUILDER
                .withAdditionalServices(EstatioIntegTestFakeServicesModule.class)
        );

        TickingFixtureClock.replaceExisting();

    }


    @Inject
    protected RunBackgroundCommandsService runBackgroundCommandsService;

    @Inject
    protected BackgroundCommandServiceJdoRepository backgroundCommandRepository;

    @Inject
    protected TransactionService transactionService;


}

