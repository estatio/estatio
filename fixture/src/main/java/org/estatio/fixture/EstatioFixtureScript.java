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
package org.estatio.fixture;

import org.apache.isis.applib.fixturescripts.FixtureScript;

public abstract class EstatioFixtureScript extends FixtureScript {

    protected EstatioFixtureScript() {
    }

    protected EstatioFixtureScript(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    protected EstatioFixtureScript(String friendlyName, String localName, Discoverability discoverability) {
        super(friendlyName, localName, discoverability);
    }

    // //////////////////////////////////////

    public static enum Prereqs {
        EXEC,
        SKIP
    }

    private Prereqs prereqs = Prereqs.EXEC;

    public EstatioFixtureScript withNoPrereqs() {
        return with(Prereqs.SKIP);
    }

    private EstatioFixtureScript with(final Prereqs prereqs) {
        this.prereqs = prereqs;
        return this;
    }

    protected boolean isExecutePrereqs() {
        return prereqs == Prereqs.EXEC;
    }

    // //////////////////////////////////////

    protected void executeChild(final String localNameOverride, final FixtureScript fixtureScript, ExecutionContext executionContext) {
        // cascade the prereqs setting
        if(fixtureScript instanceof EstatioFixtureScript) {
            final EstatioFixtureScript estatioFixtureScript = (EstatioFixtureScript) fixtureScript;
            estatioFixtureScript.with(prereqs);
        }
        super.executeChild(localNameOverride, fixtureScript, executionContext);
    }

}