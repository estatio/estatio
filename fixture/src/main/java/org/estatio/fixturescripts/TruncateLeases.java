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
package org.estatio.fixturescripts;

import javax.inject.Inject;
import org.apache.isis.applib.fixturescripts.FixtureResultList;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;
import org.apache.isis.objectstore.jdo.applib.service.support.IsisJdoSupport;

public class TruncateLeases extends SimpleFixtureScript {

    public TruncateLeases() {
        setDiscoverability(Discoverability.DISCOVERABLE);
    }

    @Override
    protected void doRun(String parameters, FixtureResultList fixtureResults) {
        deleteFrom("Lease");
    }

    private void deleteFrom(final String table) {
        isisJdoSupport.executeUpdate("TRUNCATE " + "\"" + table + "\" CASCADE");
    }

    @Inject
    private IsisJdoSupport isisJdoSupport;

}
