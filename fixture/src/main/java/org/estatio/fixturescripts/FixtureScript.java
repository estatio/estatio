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
package org.estatio.fixturescripts;

import java.util.concurrent.Callable;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.FatalException;
import org.apache.isis.applib.annotation.Named;

import org.estatio.dom.utils.StringUtils;

@Named("Script")
public enum FixtureScript {

    GENERATE_TOPMODEL_INVOICE(GenerateTopModelInvoice.class),
    CREATE_BREAK_OPTIONS(CreateBreakOptions.class),
    CREATE_RETRO_INVOICES(CreateRetroInvoices.class),
    FIX_LEASE_TERMS(FixLeaseTerms.class),
    TRUNCATE_LEASES(TruncateLeases.class),
    TRUNCATE_INVOICES(TruncateInvoices.class);

    private Class<? extends Callable<Object>> cls;

    private FixtureScript(Class<? extends Callable<Object>> cls) {
        this.cls = cls;
    }

    public Object run(DomainObjectContainer container) {
        final Callable<Object> callable = (Callable<Object>) container.newTransientInstance(cls);
        try {
            return callable.call();
        } catch (Exception e) {
            throw new FatalException(e);
        }
    }

    public String title() {
        return StringUtils.enumTitle(this.name());
    }

}
