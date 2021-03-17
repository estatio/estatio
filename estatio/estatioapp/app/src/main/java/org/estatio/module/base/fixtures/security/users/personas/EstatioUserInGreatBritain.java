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
package org.estatio.module.base.fixtures.security.users.personas;

import java.util.Arrays;

import org.apache.isis.applib.annotation.Programmatic;

import org.isisaddons.module.security.dom.user.AccountType;
import org.isisaddons.module.security.seed.scripts.AbstractUserAndRolesFixtureScript;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;

import org.estatio.module.base.fixtures.security.perms.personas.EstatioUserRoleAndPermissions;

@Programmatic
public class EstatioUserInGreatBritain extends AbstractUserAndRolesFixtureScript {

    public static final String USER_NAME = "estatio-user-gb";
    public static final String PASSWORD = "pass";
    public static final String AT_PATH = ApplicationTenancy_enum.Gb.getPath();
    public static final String EMAIL_ADDRESS = null;

    public EstatioUserInGreatBritain() {
        super(
                USER_NAME,
                PASSWORD,
                EMAIL_ADDRESS,
                AT_PATH,
                AccountType.LOCAL,
                Arrays.asList(EstatioUserRoleAndPermissions.ROLE_NAME));
    }
    

}
