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
package org.estatio.module.coda;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.estatio.module.capex.EstatioCapexModule;
import org.estatio.module.coda.dom.costcentre.CostCentre;
import org.estatio.module.coda.dom.doc.CodaDocHead;
import org.estatio.module.coda.dom.doc.CodaDocLine;
import org.estatio.module.coda.dom.supplier.CodaAddress;
import org.estatio.module.coda.dom.supplier.CodaBankAccount;
import org.estatio.module.coda.dom.supplier.CodaSupplier;
import org.estatio.module.lease.EstatioLeaseModule;
import org.estatio.module.settings.EstatioSettingsModule;

@XmlRootElement(name = "module")
public final class EstatioCodaModule extends ModuleAbstract {

    public enum FeedName {
        CODA_DOC,
        CODA_SUPPLIER
    }

    public EstatioCodaModule(){}

    @Override public Set<Module> getDependencies() {
        return Sets.newHashSet(
                new EstatioCapexModule(),

                // TODO: this is required because of InvoiceForLease_republish.
                //       but we should split out 'coda' into two separate modules, this functionality is unrelated.
                new EstatioLeaseModule(),

                new EstatioSettingsModule()
        );
    }

    @Override public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {

            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {
                deleteFrom(CodaDocLine.class);
                deleteFrom(CodaDocHead.class);
                deleteFrom(CostCentre.class);

                deleteFrom(CodaAddress.class);
                deleteFrom(CodaBankAccount.class);
                deleteFrom(CodaSupplier.class);
            }

        };
    }
}
