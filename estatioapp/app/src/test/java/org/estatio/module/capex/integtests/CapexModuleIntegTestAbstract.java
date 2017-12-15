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
package org.estatio.module.capex.integtests;

import org.slf4j.event.Level;

import org.apache.isis.core.integtestsupport.IntegrationTestAbstract3;
import org.apache.isis.core.runtime.headless.logging.LogConfig;

import org.estatio.module.capex.EstatioCapexModule;

public abstract class CapexModuleIntegTestAbstract extends IntegrationTestAbstract3 {

    public CapexModuleIntegTestAbstract() {
        super(new LogConfig(Level.INFO, logPrintStream(Level.DEBUG)),
                new EstatioCapexModule());
    }
}