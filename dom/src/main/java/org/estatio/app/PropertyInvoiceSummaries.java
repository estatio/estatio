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

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.MemberOrder;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;

@Immutable
public class PropertyInvoiceSummaries extends EstatioDomainService<PropertyInvoiceSummary> {

    public PropertyInvoiceSummaries() {
        super(PropertyInvoiceSummaries.class, PropertyInvoiceSummary.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Fixed Assets", sequence = "80")
    public List<PropertyInvoiceSummary> propertyInvoicesDom() {
        List<Property> propertyList = properties.allProperties();
        return Lists.newArrayList(
                Iterables.transform(propertyList, toSummary()).iterator());
    }

    private Function<Property, PropertyInvoiceSummary> toSummary() {
        return new Function<Property, PropertyInvoiceSummary>(){

             @Override
             public PropertyInvoiceSummary apply(final Property property) {
                 final PropertyInvoiceSummary summary = 
                     getContainer().newViewModelInstance(PropertyInvoiceSummary.class, property.getReference());
                 summary.setProperty(property);
                 return summary;
             }
         };
    }

    
    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Fixed Assets", sequence = "80")
    public List<PropertyInvoiceSummary> propertyInvoicesSql() {
        return allInstances();
    }

    
    private Properties properties;
    public void injectProperties(final Properties properties) {
        this.properties = properties;
    }
    
}
