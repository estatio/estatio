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
package org.estatio.dom.financial.publishing;

import java.util.List;

import org.apache.isis.applib.Identifier;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.services.publish.EventPayloadForActionInvocation;

import org.estatio.dom.financial.BankAccount;
import org.estatio.dom.financial.BankMandate;

/**
 * Describes the payload for publishing an {@link BankAccount} using Isis'.
 */
public class BankAccountPayload extends EventPayloadForActionInvocation<BankAccount> {

    private List<BankMandate> bankMandates;

    public BankAccountPayload(
            final Identifier actionIdentifier,
            final BankAccount target,
            final List<? extends Object> arguments,
            final Object result,
            final List<BankMandate> bankMandates) {
        super(actionIdentifier, target, arguments, result);
        this.bankMandates = bankMandates;
    }

    @Override
    @Render(Type.EAGERLY)
    public BankAccount getTarget() {
        return super.getTarget();
    }

    public List<BankMandate> getImpactedMandates() {
        return this.bankMandates;
    }

}
