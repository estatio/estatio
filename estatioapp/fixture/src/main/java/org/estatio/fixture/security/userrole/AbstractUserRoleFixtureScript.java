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
package org.estatio.fixture.security.userrole;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.role.ApplicationRole;
import org.isisaddons.module.security.dom.role.ApplicationRoleRepository;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

public abstract class AbstractUserRoleFixtureScript extends FixtureScript {

    private final String userName;
    private final String roleName;

    public AbstractUserRoleFixtureScript(
            final String userName,
            final String roleName) {
        this.userName = userName;
        this.roleName = roleName;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {
        addUserToRole(userName, roleName, executionContext);
    }

    protected ApplicationUser addUserToRole(
            final String userName,
            final String roleName,
            final ExecutionContext executionContext) {
        final ApplicationUser user = applicationUserRepository.findByUsername(userName);
        final ApplicationRole applicationRole = applicationRoleRepository.findByName(roleName);
        if(applicationRole != null) {
            user.addRole(applicationRole);
        }
        executionContext.addResult(this, roleName, applicationRole);
        return user;
    }

    @javax.inject.Inject
    private ApplicationUserRepository applicationUserRepository;
    @javax.inject.Inject
    private ApplicationRoleRepository applicationRoleRepository;

}
