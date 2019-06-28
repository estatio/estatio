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
package org.estatio.module.invoicegroup.fixtures;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;
import org.apache.isis.applib.services.factory.FactoryService;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.invoicegroup.dom.InvoiceGroup;
import org.estatio.module.invoicegroup.dom.InvoiceGroupRepository;
import org.estatio.module.invoicegroup.dom.InvoiceGroup_addProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of={"ref"},callSuper = false)
@ToString(of={"ref"})
@Accessors(chain = true)
public final class InvoiceGroupBuilder
        extends BuilderScriptAbstract<InvoiceGroup, InvoiceGroupBuilder> {

    @Getter @Setter
    String ref;
    @Getter @Setter
    String name;

    @Getter @Setter
    List<Property> properties = Lists.newArrayList();

    @Getter
    InvoiceGroup object;

    @Override
    protected void execute(final ExecutionContext executionContext) {

        checkParam("ref", executionContext, String.class);
        defaultParam("name", executionContext, getRef());

        final InvoiceGroup invoiceGroup = repository.upsert(getRef(), getName());
        for (final Property property : properties) {
            factoryService.mixin(InvoiceGroup_addProperty.class, invoiceGroup).act(property);
        }
        this.object = invoiceGroup;
    }

    @Inject
    InvoiceGroupRepository repository;
    @Inject
    FactoryService factoryService;

}
