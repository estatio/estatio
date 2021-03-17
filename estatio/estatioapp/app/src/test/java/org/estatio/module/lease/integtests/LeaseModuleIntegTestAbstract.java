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
package org.estatio.module.lease.integtests;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.estatio.module.base.integtests.BaseModuleIntegTestAbstract;
import org.estatio.module.lease.EstatioLeaseModule;
import org.estatio.module.lease.fixtures.docfrag.enums.DocFragment_demo_enum;

public abstract class LeaseModuleIntegTestAbstract extends BaseModuleIntegTestAbstract {

    public LeaseModuleIntegTestAbstract() {
        super(new EstatioLeaseModule());
    }

    @Override
    public void bootstrapAndSetupIfRequired() {
        super.bootstrapAndSetupIfRequired();

        // TODO: push down to subclasses that need this... at the moment this fixture is hidden away and not obvious
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(final ExecutionContext ec) {
                ec.executeChildren(this,
                        // demo
                        DocFragment_demo_enum.InvoicePreliminaryLetterDescription_DemoGbr,
                        DocFragment_demo_enum.InvoicePreliminaryLetterDescription_DemoNld,
                        DocFragment_demo_enum.InvoiceDescription_DemoGbr,
                        DocFragment_demo_enum.InvoiceDescription_DemoNld,
                        DocFragment_demo_enum.InvoiceItemDescription_DemoGbr,
                        DocFragment_demo_enum.InvoiceItemDescription_DemoNld
                );
            }
        });
    }
}