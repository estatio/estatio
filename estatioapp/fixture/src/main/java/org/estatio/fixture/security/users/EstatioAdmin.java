/*
 *  Copyright 2014 Jeroen van der Wal
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
package org.estatio.fixture.security.users;

import java.util.Arrays;
import org.isisaddons.module.security.dom.user.AccountType;
import org.isisaddons.module.security.seed.scripts.AbstractUserAndRolesFixtureScript;
import org.estatio.fixture.security.perms.EstatioAdminRoleAndPermissions;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGlobal;

public class EstatioAdmin extends AbstractUserAndRolesFixtureScript {

    public static final String USER_NAME = "estatio-admin";
    public static final String PASSWORD = "pass";
    public static final String EMAIL_ADDRESS = null;
    public static final String AT_PATH = ApplicationTenancyForGlobal.PATH;

    public EstatioAdmin() {
        super(
                USER_NAME,
                PASSWORD,
                EMAIL_ADDRESS,
                AT_PATH,
                AccountType.LOCAL,
                Arrays.asList(EstatioAdminRoleAndPermissions.ROLE_NAME));
    }

}
