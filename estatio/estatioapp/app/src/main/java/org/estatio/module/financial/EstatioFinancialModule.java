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
package org.estatio.module.financial;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;

import org.incode.module.fixturesupport.dom.scripts.TeardownFixtureAbstract;

import org.estatio.module.financial.dom.BankAccount;
import org.estatio.module.financial.dom.paperclips.PaperclipForBankAccount;
import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountTransaction;
import org.estatio.module.party.EstatioPartyModule;
import org.estatio.module.task.EstatioTaskModule;
import org.estatio.module.turnover.EstatioTurnoverModule;

@XmlRootElement(name = "module")
public final class EstatioFinancialModule extends ModuleAbstract {

    public EstatioFinancialModule(){}

    @Override
    public Set<Module> getDependencies() {
        return Sets.newHashSet(new EstatioTaskModule());
    }


    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract() {
            @Override
            protected void execute(final FixtureScript.ExecutionContext executionContext) {

                deleteFrom(PaperclipForBankAccount.class);
                deleteFrom(BankAccount.class);
                deleteFrom(FinancialAccountTransaction.class);
                deleteFrom(FinancialAccount.class);

            }
        };
    }



}
