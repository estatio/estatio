
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
package org.estatio.dom.invoice;

import java.io.IOException;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Where;

import org.incode.module.documents.dom.impl.docs.DocumentTemplate;
import org.incode.module.documents.dom.mixins.T_createDocumentAndScheduleRendering;

@Mixin
public class Invoice_createDocumentAndScheduleRendering extends T_createDocumentAndScheduleRendering<Invoice> {

    public Invoice_createDocumentAndScheduleRendering(final Invoice domainObject) {
        super(domainObject);
    }

    /**
     * For use only programmatically.
     */
    @Action(hidden = Where.EVERYWHERE)
    @Override
    public Object $$(
            final DocumentTemplate template,
            @ParameterLayout(named = "Action") final Intent intent) throws IOException {
        return super.$$(template, intent);
    }
}
