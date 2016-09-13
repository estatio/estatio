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

import com.google.common.collect.Lists;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.documents.dom.impl.docs.DocumentNature;
import org.incode.module.documents.dom.impl.rendering.Renderer;
import org.incode.module.documents.dom.services.ClassNameServiceAbstract;
import org.incode.module.documents.dom.services.ClassNameViewModel;

@DomainService(nature = NatureOfService.DOMAIN)
public class RendererClassNameService extends ClassNameServiceAbstract<Renderer> {

    private static final String PACKAGE_PREFIX = "org.estatio";

    public RendererClassNameService() {
        super(Renderer.class, PACKAGE_PREFIX);
    }

    @Programmatic
    public List<ClassNameViewModel> renderClassNamesFor(
            final DocumentNature inputNature,
            final DocumentNature outputNature) {
        if(inputNature == null || outputNature == null){
            return Lists.newArrayList();
        }
        return classNames(x -> inputNature.canActAsInputTo(x) && outputNature.canActAsOutputTo(x));
    }

}
