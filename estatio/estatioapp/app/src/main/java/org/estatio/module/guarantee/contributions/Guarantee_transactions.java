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
package org.estatio.module.guarantee.contributions;

import java.util.List;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.CollectionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.financial.dom.FinancialAccountTransaction;
import org.estatio.module.guarantee.dom.Guarantee;

/**
 * TODO: inline this mixin
 */
@Mixin(method = "exec")
public class Guarantee_transactions {

    private final Guarantee guarantee;

    public Guarantee_transactions(final Guarantee guarantee) {
        this.guarantee = guarantee;
    }


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed = Contributed.AS_ASSOCIATION)
    @CollectionLayout(defaultView = "table")
    public List<FinancialAccountTransaction> exec() {
        return guaranteeFinancialAccountTransactionService.transactions(guarantee);
    }


    @Inject
    GuaranteeFinancialAccountTransactionService guaranteeFinancialAccountTransactionService;

}
