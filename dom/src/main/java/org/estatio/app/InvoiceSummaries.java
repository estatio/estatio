/*
 *  Copyright 2012-2013 Eurocommercial Properties NV
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
package org.estatio.app;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;

@Immutable
public class InvoiceSummaries extends EstatioDomainService<InvoiceSummaryForPropertyDueDate> {

    public InvoiceSummaries() {
        super(InvoiceSummaries.class, InvoiceSummaryForPropertyDueDate.class);
    }

    // //////////////////////////////////////
    // TODO: remove this method once we've settled on the best approach for view
    // models. Currently preferring the database view

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<InvoiceSummaryForPropertyDueDate> invoicesDom() {
        List<Property> propertyList = properties.allProperties();
        return Lists.newArrayList(
                Iterables.transform(propertyList, toSummary()).iterator());
    }

    private Function<Property, InvoiceSummaryForPropertyDueDate> toSummary() {
        return new Function<Property, InvoiceSummaryForPropertyDueDate>() {

            @Override
            public InvoiceSummaryForPropertyDueDate apply(final Property property) {
                final InvoiceSummaryForPropertyDueDate summary =
                        getContainer().newViewModelInstance(InvoiceSummaryForPropertyDueDate.class, property.getReference());
                summary.setProperty(property);
                return summary;
            }
        };
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name = "Invoices", sequence = "80")
    public List<InvoiceSummaryForPropertyDueDate> invoiceSummary() {
        return allInstances();
    }

    // //////////////////////////////////////

    private Properties properties;

    public void injectProperties(final Properties properties) {
        this.properties = properties;
    }

}
