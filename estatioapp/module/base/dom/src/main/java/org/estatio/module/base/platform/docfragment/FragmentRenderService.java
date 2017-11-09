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
package org.estatio.module.base.platform.docfragment;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;

import org.incode.module.docfragment.dom.api.DocFragmentService;

import org.estatio.module.base.dom.UdoDomainService;

import freemarker.template.TemplateException;

/**
 * A simple wrapper around {@link DocFragmentService} that handles any checked exceptions.
 */
@DomainService(nature = NatureOfService.DOMAIN)
public class FragmentRenderService extends UdoDomainService<FragmentRenderService> {

    public FragmentRenderService() {
        super(FragmentRenderService.class);
    }

    @Programmatic
    public String render(final Object domainObject, final String fragmentName) {
        try {
            return docFragmentService.render(domainObject, fragmentName);
        } catch (IOException | TemplateException e) {
            throw new ApplicationException(e);
        }
    }

    @Inject
    DocFragmentService docFragmentService;

}
