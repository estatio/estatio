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
package org.estatio.module.application.spiimpl.security;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

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
import org.estatio.module.capex.dom.invoice.IncomingInvoice;
import org.estatio.module.capex.dom.order.Order;
import org.estatio.module.capex.dom.order.OrderItem;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.party.dom.Person;
import org.estatio.module.party.dom.PersonRepository;

@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "99", objectType = "security.ApplicationTenancyEvaluatorForEstatio")
public class ApplicationTenancyEvaluatorForEstatio implements ApplicationTenancyEvaluator {

    @Inject
    QueryResultsCache queryResultsCache;

    @Inject
    PersonRepository personRepository;

    @Inject
    FixedAssetRoleRepository fixedAssetRoleRepository;

    public boolean handles(Class<?> cls) {
        return HasAtPath.class.isAssignableFrom(cls);
    }

    public String hides(Object domainObject, ApplicationUser applicationUser) {

        if (applicationUser.getUsername().contains("external.ecpnv") && applicationUser.getAtPath().startsWith("/ITA")){

            final Person userAsPerson = personRepository.findByUsername(applicationUser.getUsername());

            if (userAsPerson==null) return "Person for external user not found";

            // NOTE: for the moment we support just one fixed asset for an external user
            final List<Property> properties = fixedAssetRoleRepository.findByParty(userAsPerson).stream()
                    .map(r->r.getAsset())
                    .filter(a -> a.getClass().isAssignableFrom(Property.class))
                    .map(Property.class::cast)
                    .collect(Collectors.toList());

            if (properties.isEmpty()) return "No property could be derived for user";


            if (domainObject instanceof IncomingInvoice){

                IncomingInvoice invoice = (IncomingInvoice) domainObject;
                if (!invoiceVisibleForExternalUser(invoice, properties)) return "Invoice not visible for user";

            }

            if (domainObject instanceof Order){

                Order order = (Order) domainObject;
                if (!orderVisibleForExternalUser(order, properties)) return "Order not visible for user";

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

    boolean invoiceVisibleForExternalUser(final IncomingInvoice invoice, final List<Property> propertiesForUser){
        final Property propertyOnInvoice = invoice.getProperty();
        if (propertyOnInvoice == null ) return false;

        switch (propertyOnInvoice.getReference()){
        case "COL":
            if (!atLeastOneItemHasChargeWithReference(invoice, CHARGE_COL_EXT)) return false;
            break;

        case "GIG":
            if (!atLeastOneItemHasChargeWithReference(invoice, CHARGE_GIG_EXT)) return false;
            break;

        case "FAB":
            if (!atLeastOneItemHasChargeWithReference(invoice, CHARGE_FAB_EXT)) return false;
            break;

        default:
            return false;
        }

        if (propertiesForUser.contains(propertyOnInvoice)) return true;

        return false;
    }

    boolean orderVisibleForExternalUser(final Order order, final List<Property> propertiesForUser){
        final Property propertyOnOrder = order.getProperty();
        if (propertyOnOrder == null ) return false;

        switch (propertyOnOrder.getReference()){
        case "COL":
            if (!atLeastOneItemHasChargeWithReference(order, CHARGE_COL_EXT)) return false;
            break;

        case "GIG":
            if (!atLeastOneItemHasChargeWithReference(order, CHARGE_GIG_EXT)) return false;
            break;

        case "FAB":
            if (!atLeastOneItemHasChargeWithReference(order, CHARGE_FAB_EXT)) return false;
            break;

        default:
            return false;
        }

        if (propertiesForUser.contains(propertyOnOrder)) return true;

        return false;
    }

    private boolean atLeastOneItemHasChargeWithReference(final IncomingInvoice invoice, final String chargeExt) {
        for (InvoiceItem ii : invoice.getItems()){
            if (ii.getCharge()!=null && ii.getCharge().getReference().equals(chargeExt)) {
                return true;
            }
        }
        return false;
    }

    private boolean atLeastOneItemHasChargeWithReference(final Order order, final String chargeExt) {
        for (OrderItem oi : order.getItems()){
            if (oi.getCharge()!=null && oi.getCharge().getReference().equals(chargeExt)) {
                return true;
            }
        }
        return false;
    }

    public static String CHARGE_COL_EXT = "ITPR285";
    public static String CHARGE_GIG_EXT = "ITPR286";
    public static String CHARGE_FAB_EXT = "ITPR287";

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

    //region > helpers: applicationTenancyPathForCached, applicationTenancyPathFor, userTenancyPathForCached, userTenancyPathFor
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
