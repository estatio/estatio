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
package org.estatio.module.party.fixtures.person.builders;

import javax.inject.Inject;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;
import org.apache.isis.applib.value.Password;

import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

import org.estatio.module.party.dom.Person;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"person"}, callSuper = false)
@ToString(of={"person"})
@Accessors(chain = true)
public final class ApplicationUserBuilder
        extends BuilderScriptAbstract<ApplicationUser, ApplicationUserBuilder> {

    @Getter @Setter
    private Person person;

    @Getter @Setter
    private String securityUsername;

    @Getter @Setter
    private String securityUserAccountCloneFrom;

    @Getter
    ApplicationUser object;

    @Override
    protected void execute(ExecutionContext executionContext) {

        checkParam("person", executionContext, Person.class);
        checkParam("securityUsername", executionContext, String.class);

        defaultParam("securityUserAccountCloneFrom", executionContext, "estatio-admin");


        if(securityUsername != null) {
            ApplicationUser userToCloneFrom = applicationUserRepository.findByUsername(securityUserAccountCloneFrom);
            if(userToCloneFrom == null) {
                throw new IllegalArgumentException("Could not find any user with username: " + securityUserAccountCloneFrom);
            }

            object = applicationUserRepository.newLocalUserBasedOn(
                    securityUsername,
                    new Password("pass"), new Password("pass"),
                    userToCloneFrom, true, null);
            object.setAtPath(person.getAtPath());
            person.setUsername(securityUsername);

            executionContext.addResult(this, securityUsername, userToCloneFrom);
        }
    }

    @Inject
    ApplicationUserRepository applicationUserRepository;

}

