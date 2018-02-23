/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.module.base;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.clock.TickingClockFixture;

import org.isisaddons.module.audit.AuditModule;
import org.isisaddons.module.command.dom.CommandDomModule;
import org.isisaddons.module.command.replay.CommandReplayModule;
import org.isisaddons.module.excel.ExcelModule;
import org.isisaddons.module.fakedata.FakeDataModule;
import org.isisaddons.module.freemarker.dom.FreeMarkerModule;
import org.isisaddons.module.pdfbox.dom.PdfBoxModule;
import org.isisaddons.module.poly.PolyModule;
import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.servletapi.ServletApiModule;
import org.isisaddons.module.stringinterpolator.StringInterpolatorModule;
import org.isisaddons.module.togglz.TogglzModule;
import org.isisaddons.module.xdocreport.dom.XDocReportModule;
import org.isisaddons.wicket.excel.cpt.ui.ExcelUiModule;
import org.isisaddons.wicket.fullcalendar2.cpt.applib.FullCalendar2ApplibModule;
import org.isisaddons.wicket.fullcalendar2.cpt.ui.FullCalendar2UiModule;
import org.isisaddons.wicket.gmap3.cpt.applib.Gmap3ApplibModule;
import org.isisaddons.wicket.pdfjs.cpt.PdfjsCptModule;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.base.services.BaseServicesModule;
import org.incode.module.errorrptslack.ErrorReportingSlackModule;
import org.incode.module.userimpersonate.UserImpersonateModule;
import org.incode.module.zip.ZipModule;

import org.estatio.module.base.fixtures.security.perms.personas.EstatioRolesAndPermissions;
import org.estatio.module.base.fixtures.security.userrole.personas.EstatioAdmin_Has_EstatioSuperuserRole;
import org.estatio.module.base.fixtures.security.users.personas.EstatioAdmin;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUser;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUserInFrance;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUserInGreatBritain;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUserInItaly;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUserInNetherlands;
import org.estatio.module.base.fixtures.security.users.personas.EstatioUserInSweden;

@XmlRootElement(name = "module")
public final class EstatioBaseModule extends ModuleAbstract {

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(

                // lib
                // (nothing for incode-module-base-dom)
                // (nothing for incode-module-fixturesupport-dom)
                // don't include the settings module, instead we use EstatioSettingsModule
                new ExcelModule(),
                new PdfBoxModule(),
                new PolyModule(),
                new ServletApiModule(),
                new StringInterpolatorModule(),
                new FakeDataModule(),
                new FreeMarkerModule(),
                new XDocReportModule(),
                new UserImpersonateModule(),
                new ZipModule(),

                // spi (remaining part of ECP's app module)
                new SecurityModule(),
                new CommandReplayModule(),
                new CommandDomModule(),
                new AuditModule(),
                new ErrorReportingSlackModule(),

                // wkt
                new ExcelUiModule(),
                new FullCalendar2ApplibModule(),
                new FullCalendar2UiModule(),
                new Gmap3ApplibModule(),
                new PdfjsCptModule(),

                // ext
                new TogglzModule(),

                new BaseServicesModule()
        );
    }

    @Override
    public Set<Class<?>> getAdditionalServices() {
        return Sets.newHashSet(
                org.isisaddons.module.security.dom.password.PasswordEncryptionServiceUsingJBcrypt.class,
                org.isisaddons.module.security.dom.permission.PermissionsEvaluationServiceAllowBeatsVeto.class
        );
    }

    private static final ThreadLocal<Boolean> refData = ThreadLocal.withInitial(() -> false);
    @Override
    public FixtureScript getRefDataSetupFixture() {
        if(refData.get()) {
            return null;
        }
        // else
        refData.set(true);
        return new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext executionContext) {
                executionContext.executeChild(this, new TickingClockFixture().setDate("2014-05-18"));
                executionContext.executeChild(this, new ApplicationTenancy_enum.PersistAll());

                // set up 3 estatio roles
                executionContext.executeChild(this, new EstatioRolesAndPermissions());

                // estatio-admin user with the estatio-admin role + superuser
                executionContext.executeChild(this, new EstatioAdmin());
                executionContext.executeChild(this, new EstatioAdmin_Has_EstatioSuperuserRole());

                // bunch of users with estatio-user role
                executionContext.executeChild(this, new EstatioUser());
                executionContext.executeChild(this, new EstatioUserInFrance());
                executionContext.executeChild(this, new EstatioUserInGreatBritain());
                executionContext.executeChild(this, new EstatioUserInItaly());
                executionContext.executeChild(this, new EstatioUserInNetherlands());
                executionContext.executeChild(this, new EstatioUserInSweden());

            }
        };
    }

}
