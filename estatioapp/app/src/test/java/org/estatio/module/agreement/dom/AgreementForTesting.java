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
package org.estatio.module.agreement.dom;

import javax.jdo.annotations.DiscriminatorStrategy;

import org.apache.isis.applib.annotation.DomainObject;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

@DomainObject(
        objectType = "org.estatio.module.agreement.dom.AgreementForTesting"
)
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.VALUE_MAP,
        column = "discriminator",
        value = "org.estatio.module.agreement.dom.AgreementForTesting"
)
public class AgreementForTesting extends Agreement {

    public AgreementForTesting() {
        super(null, null);
    }

    public ApplicationTenancy getApplicationTenancy() {
        return null;
    }

    @Override
    public Agreement changePrevious(Agreement previousAgreement) {
        return null;
    }
}