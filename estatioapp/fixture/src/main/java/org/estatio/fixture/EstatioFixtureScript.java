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

import java.util.Random;
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

    static Random random;
    static {
        random = new Random(System.currentTimeMillis());
    }

    public final Faker2 faker() {
        return container.injectServicesInto(new Faker2(random));
    }

    // //////////////////////////////////////

    private static enum Prereqs {
        EXEC,
        SKIP
    }

    private static ThreadLocal<Prereqs> prereqs = new ThreadLocal<Prereqs>() {
        @Override
        protected Prereqs initialValue() {
            return Prereqs.EXEC;
        }
    };

    public static void withSkipPrereqs(final Runnable runnable) {
        try {
            prereqs.set(EstatioFixtureScript.Prereqs.SKIP);
            runnable.run();
        } finally {
            prereqs.set(EstatioFixtureScript.Prereqs.EXEC);
        }

    }

    protected boolean isExecutePrereqs() {
        return prereqs.get() == Prereqs.EXEC;
    }

}