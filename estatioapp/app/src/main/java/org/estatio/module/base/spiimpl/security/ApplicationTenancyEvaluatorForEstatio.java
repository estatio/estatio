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
package org.estatio.module.base.spiimpl.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancyEvaluator;
import org.isisaddons.module.security.dom.tenancy.HasAtPath;
import org.isisaddons.module.security.dom.user.ApplicationUser;

import org.estatio.module.asset.dom.Property;
import org.estatio.module.asset.dom.role.FixedAssetRoleRepository;
import org.estatio.module.capex.app.ExternalUserService;
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.invoice.IncomingInvoiceItem;
import org.estatio.module.capex.dom.invoice.approval.IncomingInvoiceApprovalConfigurationUtil;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.party.dom.PersonRepository;
import org.estatio.module.task.dom.state.StateTransition;
import org.estatio.module.task.dom.state.StateTransitionService;
import org.estatio.module.task.dom.task.Task;

@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "99", objectType = "security.ApplicationTenancyEvaluatorForEstatio")
public class ApplicationTenancyEvaluatorForEstatio implements ApplicationTenancyEvaluator {

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PersonRepository personRepository;

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

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

        final String objectTenancyPath = applicationTenancyPathForCached(domainObject);
        if(objectTenancyPath == null) {
            return null;
        }
        final String userTenancyPath = userTenancyPathForCached(applicationUser);
        if (userTenancyPath == null) {
            return "User has no tenancy";
        }

        if (objectVisibleToUser(objectTenancyPath, userTenancyPath)) {
            return null;
        }
        return String.format("User with tenancy \'%s\' is not permitted to view object with tenancy \'%s\'", userTenancyPath, objectTenancyPath);
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

        if (!IncomingInvoiceApprovalConfigurationUtil.isInvoiceForExternalCenterManager(invoice)) return false;

        if (propertiesForUser.contains(propertyOnInvoice)) return true;

        return false;
    }

    boolean orderVisibleForExternalUser(final Order order, final List<Property> propertiesForUser){

        final Property propertyOnOrder = order.getProperty();
        if (propertyOnOrder == null ) return false;

        final List<String> propertyRefsFound = new ArrayList<>();
        IncomingInvoiceApprovalConfigurationUtil.PROPERTY_REF_EXTERNAL_PROJECT_REF_MAP.forEach((k,v)->{
            if (k.equals(propertyOnOrder.getReference()) && atLeastOneItemHasProjectWithReference(order, v)) propertyRefsFound.add(k);
        });

        if (propertyRefsFound.isEmpty()) return false;

        if (propertiesForUser.contains(propertyOnOrder)) return true;

        return false;
    }

    private boolean atLeastOneItemHasProjectWithReference(final IncomingInvoice invoice, final String projectRefExt) {
        for (InvoiceItem ii : invoice.getItems()){
            IncomingInvoiceItem cii = (IncomingInvoiceItem) ii;
            if (cii.getProject()!=null && cii.getProject().getReference().equals(projectRefExt)) {
                return true;
            }
        }
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

    public String disables(Object domainObject, ApplicationUser applicationUser) {
        final String objectTenancyPath = applicationTenancyPathForCached(domainObject);
        if(objectTenancyPath == null) {
            return null;
        }
        final String userTenancyPath = userTenancyPathForCached(applicationUser);
        if (userTenancyPath == null) {
            return "User has no tenancy";
        }

        if (objectEnabledForUser(objectTenancyPath, userTenancyPath)) {
            return null;
        }
        return String.format("User with tenancy \'%s\' is not permitted to edit object with tenancy \'%s\'", userTenancyPath, objectTenancyPath);
    }

    boolean objectVisibleToUser(String objectTenancyPath, String userTenancyPath) {
        List<String> userTenancyPaths = split(userTenancyPath, ';');
        for (String tenancyPath : userTenancyPaths) {
            boolean visibleForThisPath = objectVisibleToUserForSinglePath(objectTenancyPath, tenancyPath);
            if(visibleForThisPath) {
                return true;
            }
        }
        return false;
    }

    private boolean objectVisibleToUserForSinglePath(final String objectTenancyPath, final String userTenancyPath) {
        final List<String> objectTenancyPathList = split(objectTenancyPath, '/');
        final List<String> userTenancyPathList = split(userTenancyPath, '/');

        for (int i = 0; i < objectTenancyPathList.size(); i++) {
            final String objectTenancyPathPart = objectTenancyPathList.get(i);
            if(i >= userTenancyPathList.size()) {
                // run out of parts for the user tenancy, so the user tenancy is higher than object
                return true;
            }
            final String userTenancyPathPart = userTenancyPathList.get(i);
            if(!partsEqual(objectTenancyPathPart, userTenancyPathPart)) {
                return false;
            }
        }
        // run out of parts for the object tenancy, so the user tenancy is same or lower than the object
        return true;
    }

    boolean objectEnabledForUser(String objectTenancyPath, String userTenancyPath) {
        List<String> userTenancyPaths = split(userTenancyPath, ';');
        for (String tenancyPath : userTenancyPaths) {
            boolean enabledForThisPath = objectEnabledForUserSinglePath(objectTenancyPath, tenancyPath);
            if(enabledForThisPath) {
                return true;
            }
        }
        return false;
    }

    private boolean objectEnabledForUserSinglePath(final String objectTenancyPath, final String userTenancyPath) {
        final List<String> objectTenancyPathList = split(objectTenancyPath, '/');
        final List<String> userTenancyPathList = split(userTenancyPath, '/');

        for (int i = 0; i < objectTenancyPathList.size(); i++) {
            final String objectTenancyPathPart = objectTenancyPathList.get(i);
            if(i >= userTenancyPathList.size()) {
                // run out of parts for the user tenancy, so the user tenancy is higher than object
                return true;
            }
            final String userTenancyPathPart = userTenancyPathList.get(i);
            if(!partsEqual(objectTenancyPathPart, userTenancyPathPart)) {
                return false;
            }
        }
        // run out of parts for the object tenancy, so the user tenancy is same or lower than the object
        final boolean sameSize = objectTenancyPathList.size() == userTenancyPathList.size();
        return sameSize;
    }

    protected boolean partsEqual(String objectTenancyPathPart, String userTenancyPathPart) {
        if (Objects.equals(objectTenancyPathPart, userTenancyPathPart)) {
            return true;
        }
        // eg allow "X-CAR" user to match with "CAR"
        if (!userTenancyPathPart.startsWith("X-")) {
            return false;
        }
        final String baseUserTenancyPathPart = userTenancyPathPart.substring(2);
        return Objects.equals(objectTenancyPathPart, baseUserTenancyPathPart);
    }

    private static List<String> split(final String objectTenancyPath, final char separator) {
        return FluentIterable.from(Splitter.on(separator)
                            .split(objectTenancyPath))
                            .filter(s -> !com.google.common.base.Strings.isNullOrEmpty(s))
                            .transform(s -> s.trim())
                            .toList();
    }

    //region > helpers: propertiesForUserAsPersonCached, propertiesForUserAsPerson, applicationTenancyPathForCached, applicationTenancyPathFor, userTenancyPathForCached, userTenancyPathFor

    List<Property> propertiesForUserAsPersonCached(final ApplicationUser applicationUser){
        return (List<Property>)queryResultsCache.execute(
                (Callable) () -> propertiesForUserAsPerson(applicationUser),
                ApplicationTenancyEvaluatorForEstatio.class,
                "propertiesForUserAsPersonCached", applicationUser);
    }

    private List<Property> propertiesForUserAsPerson(final ApplicationUser applicationUser){
        return externalUserService.getPropertiesForExternalUser();
    }

    private String applicationTenancyPathForCached(final Object domainObject) {
        return (String)queryResultsCache.execute(
                (Callable) () -> applicationTenancyPathFor(domainObject),
                ApplicationTenancyEvaluatorForEstatio.class,
                "applicationTenancyPathForCached", domainObject);
    }

    private String applicationTenancyPathFor(Object domainObject) {
        if (!(domainObject instanceof HasAtPath)) {
            return null;
        }
        HasAtPath tenantedObject = (HasAtPath) domainObject;
        return tenantedObject.getAtPath();
    }

    private String userTenancyPathForCached(final ApplicationUser applicationUser) {
        return (String)queryResultsCache.execute(
                (Callable) () -> userTenancyPathFor(applicationUser),
                ApplicationTenancyEvaluatorForEstatio.class, "userTenancyPathForCached", applicationUser);
    }

    private String userTenancyPathFor(final ApplicationUser applicationUser) {
        // previously the code had this clause, but this is now always false because
        // ApplicationUser (in sec module, as of 1.13.6) does not (cannot) implement
        // org.estatio.dom.WithApplicationTenancy
//        if(handles(applicationUser.getClass())) {
//            return applicationTenancyPathFor(applicationUser);
//        }
        return applicationUser.getAtPath();
    }
    //endregion

}
