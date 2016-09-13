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
package org.estatio.app.menus.documents;

import java.util.List;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;

import org.incode.module.documents.dom.impl.applicability.Binder;
import org.incode.module.documents.dom.services.ClassNameServiceAbstract;
import org.incode.module.documents.dom.services.ClassNameViewModel;
import org.incode.module.documents.dom.spi.BinderClassNameService;

@DomainService(nature = NatureOfService.DOMAIN)
public class BinderClassNameServiceForEstatio extends ClassNameServiceAbstract<Binder> implements
        BinderClassNameService {

    private static final String PACKAGE_PREFIX = "org.estatio";

    public BinderClassNameServiceForEstatio() {
        super(Binder.class, PACKAGE_PREFIX);
    }

    @Override
    public List<ClassNameViewModel> binderClassNames() {
        return this.classNames();
    }
}
