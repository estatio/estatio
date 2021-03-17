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
package org.estatio.module.base.fixtures.security.userrole.personas;

import org.estatio.module.base.fixtures.security.perms.personas.EstatioAdminRoleAndPermissions;
import org.estatio.module.base.fixtures.security.users.personas.EstatioAdmin;

public class EstatioAdmin_Has_EstatioAdminRole extends AbstractUserRoleFixtureScript {
    public EstatioAdmin_Has_EstatioAdminRole() {
        super(EstatioAdmin.USER_NAME, EstatioAdminRoleAndPermissions.ROLE_NAME);
    }
}
