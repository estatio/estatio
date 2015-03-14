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
package org.estatio.fixture.security;

import java.util.List;
import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Prototype;
import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

/**
 * Enables fixtures to be installed from the application.
 */
@DomainService
@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "20.2"
)
public class EstatioSecurityModuleFixturesService extends FixtureScripts {

    public EstatioSecurityModuleFixturesService() {
        super(EstatioSecurityModuleFixturesService.class.getPackage().getName());
    }

    @Prototype
    @ActionLayout(
            cssClassFa = "fa-bolt",
            named = "Run Security Fixture Script"
    )
    @Override
    public List<FixtureResult> runFixtureScript(FixtureScript fixtureScript, @ParameterLayout(
            named = "Parameters",
            describedAs = "Script-specific parameters (if any).  The format depends on the script implementation (eg key=value, CSV, JSON, XML etc)",
            multiLine = 10) @Optional String parameters) {
        return super.runFixtureScript(fixtureScript, parameters);
    }

    @Override
    public FixtureScript default0RunFixtureScript() {
        return findFixtureScriptFor(EstatioSecurityModuleSeedFixture.class);
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
    @ActionLayout(
            cssClassFa = "fa-bolt"
    )
    @MemberOrder(sequence = "20")
    public Object installFixturesAndReturnFirstRole() {
        final List<FixtureResult> fixtureResultList = findFixtureScriptFor(EstatioSecurityModuleSeedFixture.class).run(null);
        for (FixtureResult fixtureResult : fixtureResultList) {
            final Object object = fixtureResult.getObject();
            if (object instanceof ApplicationRole) {
                return object;
            }
        }
        getContainer().warnUser("No rules found in fixture; returning all results");
        return fixtureResultList;
    }
    
    // //////////////////////////////////////
    

    

}
