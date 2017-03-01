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

import javax.inject.Inject;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.RestrictTo;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.fixturescripts.FixtureResult;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;

import org.isisaddons.module.security.dom.role.ApplicationRole;

/**
 * Enables fixtures to be installed from the application.
 */
@DomainService(
        nature = NatureOfService.VIEW_MENU_ONLY,
        objectType = "org.estatio.fixture.security.EstatioSecurityModuleFixturesMenu"
)
@DomainServiceLayout(
        named = "Prototyping",
        menuBar = DomainServiceLayout.MenuBar.SECONDARY,
        menuOrder = "20.2"
)
public class EstatioSecurityModuleFixturesMenu {


    @Action(
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-bolt",
            named = "Run Security Fixture Script"
    )
    public List<FixtureResult> runFixtureScript(FixtureScript fixtureScript, @ParameterLayout(
            named = "Parameters",
            describedAs = "Script-specific parameters (if any).  The format depends on the script implementation (eg key=value, CSV, JSON, XML etc)",
            multiLine = 10) @Optional String parameters) {
        return fixtureScripts.runFixtureScript(fixtureScript, parameters);
    }

    public FixtureScript default0RunFixtureScript() {
        return fixtureScripts.findFixtureScriptFor(EstatioSecurityModuleSeedFixture.class);
    }

    /**
     * Raising visibility to <tt>public</tt> so that choices are available for
     * first param of {@link #runFixtureScript(FixtureScript, String)}.
     */
    public List<FixtureScript> choices0RunFixtureScript() {
        return fixtureScripts.getFixtureScriptList();
    }


    // //////////////////////////////////////

    @Action(
            semantics = SemanticsOf.NON_IDEMPOTENT,
            restrictTo = RestrictTo.PROTOTYPING
    )
    @ActionLayout(
            cssClassFa = "fa-bolt"
    )
    @MemberOrder(sequence = "20")
    public Object installFixturesAndReturnFirstRole() {
        final List<FixtureResult> fixtureResultList = fixtureScripts.findFixtureScriptFor(EstatioSecurityModuleSeedFixture.class).run(null);
        for (FixtureResult fixtureResult : fixtureResultList) {
            final Object object = fixtureResult.getObject();
            if (object instanceof ApplicationRole) {
                return object;
            }
        }
        container.warnUser("No rules found in fixture; returning all results");
        return fixtureResultList;
    }


    // //////////////////////////////////////
    

    @Inject
    FixtureScripts fixtureScripts;
    @Inject
    DomainObjectContainer container;

}
