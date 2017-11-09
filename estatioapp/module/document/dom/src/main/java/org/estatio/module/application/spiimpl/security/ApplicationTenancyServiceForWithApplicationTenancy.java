
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
package org.estatio.module.application.spiimpl.security;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.estatio.module.base.dom.apptenancy.WithApplicationTenancy;

@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "100")
public class ApplicationTenancyServiceForWithApplicationTenancy
        implements org.incode.module.document.dom.spi.ApplicationTenancyService,
                   org.incode.module.docfragment.dom.spi.ApplicationTenancyService  {

    @Override
    public String atPathFor(final Object domainObject) {
        if(domainObject instanceof WithApplicationTenancy) {
            final WithApplicationTenancy withApplicationTenancy = (WithApplicationTenancy) domainObject;
            return withApplicationTenancy.getApplicationTenancy().getPath();
        }
        return null;
    }
}
