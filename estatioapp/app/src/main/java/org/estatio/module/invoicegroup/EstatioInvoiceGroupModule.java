/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.module.invoicegroup;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.estatio.module.asset.EstatioAssetModule;
import org.estatio.module.invoicegroup.dom.InvoiceGroup;
import org.estatio.module.lease.dom.breaks.EventSourceLinkForBreakOption;

@XmlRootElement(name = "module")
public final class EstatioInvoiceGroupModule extends ModuleAbstract {

    public EstatioInvoiceGroupModule(){}

    @Override
    public Set<Module> getDependencies(){
        return Sets.newHashSet(
                new EstatioAssetModule()
            );
    }

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {
            @Override
            protected void execute(final ExecutionContext executionContext) {

                String schema;
                String sql;
                String table;

                // InvoiceAttribute
                schema = schemaOf(InvoiceGroup.class);
                table = "InvoiceGroupProperty";
                sql = String.format("DELETE FROM \"%s\".\"%s\" ", schema, table);
                this.isisJdoSupport.executeUpdate(sql);

                deleteFrom(InvoiceGroup.class);
            }
        };
    }
}
