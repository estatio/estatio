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
package org.estatio.module.financial.fixtures.fatransaction.builders;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixturescripts.BuilderScriptAbstract;

import org.estatio.module.financial.dom.FinancialAccount;
import org.estatio.module.financial.dom.FinancialAccountRepository;
import org.estatio.module.financial.dom.FinancialAccountTransaction;
import org.estatio.module.financial.dom.FinancialAccountTransactionRepository;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/*
public class BankMandateBuilder extends BuilderScriptAbstract<BankMandate, BankMandateBuilder> {

 */
@EqualsAndHashCode(of={"financialAccount", "date"}, callSuper = false)
@ToString(of={"financialAccount", "date"})
@Accessors(chain = true)
public class FinancialAccountTransactionBuilder extends BuilderScriptAbstract<FinancialAccountTransaction, FinancialAccountTransactionBuilder> {

    @Getter @Setter
    FinancialAccount financialAccount;

    @Getter @Setter
    LocalDate date;

    @Getter @Setter
    BigDecimal amount;

    @Getter @Setter
    String description;

    @Getter
    FinancialAccountTransaction object;

    @Override
    protected void execute(final ExecutionContext ec) {

        defaultParam("financialAccount", ec, 0);
        checkParam("date", ec, LocalDate.class);
        checkParam("amount", ec, BigDecimal.class);
        defaultParam("description", ec, "Fixture transaction");

        FinancialAccountTransaction financialAccountTransaction = financialAccountTransactionRepository.newTransaction(
                financialAccount,
                date,
                description,
                amount);
        ec.addResult(this, financialAccountTransaction);

        object = financialAccountTransaction;
    }

    @Inject
    FinancialAccountRepository financialAccountRepository;

    @Inject
    FinancialAccountTransactionRepository financialAccountTransactionRepository;

}
