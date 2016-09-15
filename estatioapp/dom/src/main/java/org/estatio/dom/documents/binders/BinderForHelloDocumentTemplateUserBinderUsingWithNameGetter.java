/*
 *
 *  Copyright 2012-2015 Eurocommercial Properties NV
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

package org.estatio.dom.documents.binders;

import java.util.Collections;

import org.incode.module.documents.dom.impl.applicability.Binder;
import org.incode.module.documents.dom.impl.docs.DocumentTemplate;

import org.estatio.dom.documents.datamodels.HelloDocumentTemplateUserDataModel;
import org.estatio.dom.WithNameGetter;

public class BinderForHelloDocumentTemplateUserBinderUsingWithNameGetter implements Binder {

    @Override
    public Binding newBinding(
            final DocumentTemplate documentTemplate,
            final Object domainObject) {

        if(!(domainObject instanceof WithNameGetter)) {
            throw new IllegalArgumentException("Domain object must be of type WithNameGetter");
        }
        WithNameGetter withNameGetter = (WithNameGetter) domainObject;

        final HelloDocumentTemplateUserDataModel dataModel = new HelloDocumentTemplateUserDataModel();
        dataModel.setUser(withNameGetter.getName());

        return new Binding(dataModel, dataModel, Collections.singletonList(domainObject));
    }

}
