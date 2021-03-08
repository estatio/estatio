/*
 *  Copyright 2012-date Eurocommercial Properties NV
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
package org.estatio.module.capex.spiimpl.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyEvaluator;
import org.isisaddons.module.security.dom.tenancy.HasAtPath;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.capex.app.ExternalUserService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalConfigurationUtil;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.task.dom.state.StateTransition;
import org.estatio.module.task.dom.state.StateTransitionService;
import org.estatio.module.task.dom.task.Task;

@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "99", objectType = "security.ApplicationTenancyEvaluatorForExternalUsers")
public class ApplicationTenancyEvaluatorForExternalUsers implements ApplicationTenancyEvaluator {

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    StateTransitionService stateTransitionService;

    @Inject ExternalUserService externalUserService;

    public boolean handles(Class<?> cls) {
        return HasAtPath.class.isAssignableFrom(cls);
    }

    public String hides(Object domainObject, ApplicationUser applicationUser) {

        if (applicationUser.getUsername().contains("external.ecpnv") && applicationUser.getAtPath().startsWith("/ITA")){

            final List<Property> properties = propertiesForUserAsPersonCached(applicationUser);

            if (properties.isEmpty()) return "No property could be derived for user";


            if (domainObject instanceof IncomingInvoice){

                IncomingInvoice invoice = (IncomingInvoice) domainObject;
                if (!invoiceVisibleForExternalUser(invoice, properties)) return "Invoice not visible for user";

            }

            if (domainObject instanceof Order){

                Order order = (Order) domainObject;
                if (!orderVisibleForExternalUser(order, properties)) return "Order not visible for user";

            }

            if (domainObject instanceof Task){

                Task task = (Task) domainObject;
                if (!taskVisibleForExternalUser(task, properties)) return "Task not visible for user";

            }

        }

        return null;
    }

    public String disables(Object domainObject, ApplicationUser applicationUser) {
        return null;
    }

    boolean taskVisibleForExternalUser(final Task task, final List<Property> propertiesForUser){

        final StateTransition stateTransition = stateTransitionService.findFor(task);
        if (stateTransition==null) return false;

        final Object domainObject = stateTransition.getDomainObject();

        if (domainObject instanceof IncomingInvoice){
            IncomingInvoice invoice = (IncomingInvoice) domainObject;
            return invoiceVisibleForExternalUser(invoice, propertiesForUser);
        }

        if (domainObject instanceof Order){
            Order order = (Order) domainObject;
            return orderVisibleForExternalUser(order, propertiesForUser);
        }

        return false;
    }

    boolean invoiceVisibleForExternalUser(final IncomingInvoice invoice, final List<Property> propertiesForUser){

        final Property propertyOnInvoice = invoice.getProperty();
        if (propertyOnInvoice == null ) return false;

        if (IncomingInvoiceApprovalConfigurationUtil.propertyRefsWithExternalAssetManager.contains(propertyOnInvoice.getReference())
                && propertiesForUser.contains(propertyOnInvoice)) {
            return true; // Exception for INCSUP-730
        }

        if (!IncomingInvoiceApprovalConfigurationUtil.isInvoiceForExternalCenterManager(invoice)) return false;

        if (propertiesForUser.contains(propertyOnInvoice)) return true;

        return false;
    }

    boolean orderVisibleForExternalUser(final Order order, final List<Property> propertiesForUser){

        final Property propertyOnOrder = order.getProperty();
        if (propertyOnOrder == null ) return false;

        if (IncomingInvoiceApprovalConfigurationUtil.propertyRefsWithExternalAssetManager.contains(propertyOnOrder.getReference())
                && propertiesForUser.contains(propertyOnOrder)) {
            return true; // Exception for INCSUP-730
        }

        final List<String> propertyRefsFound = new ArrayList<>();
        IncomingInvoiceApprovalConfigurationUtil.PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP.forEach((k,v)->{
            if (k.equals(propertyOnOrder.getReference()) && atLeastOneItemHasProjectWithReference(order, v)) propertyRefsFound.add(k);
        });

        if (propertyRefsFound.isEmpty()) return false;

        if (propertiesForUser.contains(propertyOnOrder)) return true;

        return false;
    }

    private boolean atLeastOneItemHasProjectWithReference(final Order order, final String projectRefExt) {
        for (OrderItem oi : order.getItems()){
            if (oi.getProject()!=null && oi.getProject().getReference().equals(projectRefExt)) {
                return true;
            }
        }
        return false;
    }

    List<Property> propertiesForUserAsPersonCached(final ApplicationUser applicationUser){
        return (List<Property>)queryResultsCache.execute(
                (Callable) () -> propertiesForUserAsPerson(applicationUser),
                ApplicationTenancyEvaluatorForExternalUsers.class,
                "propertiesForUserAsPersonCached", applicationUser);
    }

    private List<Property> propertiesForUserAsPerson(final ApplicationUser applicationUser){
        return externalUserService.getPropertiesForExternalUser();
    }

}
