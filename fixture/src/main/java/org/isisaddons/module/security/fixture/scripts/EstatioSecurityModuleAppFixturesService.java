/*
 *  Copyright 2014 Dan Haywood
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
package org.isisaddons.module.security.fixture.scripts;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.isisaddons.module.security.dom.role.ApplicationRole;

/**
 * Enables fixtures to be installed from the application.
 */
@DomainService(menuOrder = "99")
@Named("Prototyping")
public class EstatioSecurityModuleAppFixturesService extends FixtureScripts {

    public EstatioSecurityModuleAppFixturesService() {
        super(EstatioSecurityModuleAppFixturesService.class.getPackage().getName());
    }

    @Override
    public FixtureScript default0RunFixtureScript() {
        return findFixtureScriptFor(EstatioSecurityModuleAppSetUp.class);
    }

    /**
     * Raising visibility to <tt>public</tt> so that choices are available for
     * first param of {@link #runFixtureScript(FixtureScript, String)}.
     */
    @Override
    public List<FixtureScript> choices0RunFixtureScript() {
        return super.choices0RunFixtureScript();
    }

    // //////////////////////////////////////

    @ActionSemantics(ActionSemantics.Of.NON_IDEMPOTENT)
    @Prototype
    @MemberOrder(sequence = "20")
    public Object installFixturesAndReturnFirstRole() {
        final List<FixtureResult> fixtureResultList = findFixtureScriptFor(EstatioSecurityModuleAppSetUp.class).run(null);
        for (FixtureResult fixtureResult : fixtureResultList) {
            final Object object = fixtureResult.getObject();
            if (object instanceof ApplicationRole) {
                return object;
            }
        }
        getContainer().warnUser("No rules found in fixture; returning all results");
        return fixtureResultList;
    }

}
