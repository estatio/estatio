/*
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
package org.estatio.module.application.seed;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.FixtureScripts;
import org.apache.isis.applib.services.registry.ServiceRegistry2;
import org.apache.isis.applib.services.repository.RepositoryService;

import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.lease.dom.invoicing.summary.InvoiceSummaryForPropertyDueDateStatus;
import org.estatio.module.link.dom.Link;
import org.estatio.module.link.dom.LinkRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@DomainService(nature = NatureOfService.DOMAIN, menuOrder = "99")
public class LinkModuleSeedService {

    @PostConstruct
    public void init() {

        if(System.getProperty("isis.integTest") != null) {
            return;
        }

        fixtureScripts.runFixtureScript(new SeedLink(), null);
    }

    @Inject
    FixtureScripts fixtureScripts;

    @AllArgsConstructor
    @Getter
    enum Link_data {
        PRELIM_LETTER_ITA(
                "Preliminary letter Italy", "/ITA", Invoice.class,
                "${reportServerBaseUrl}Preliminary+Letter&id=${this.id}&rs:Command=Render"),
        INVOICE_ITA(
                "Invoice Italy", "/ITA", Invoice.class,
                "${reportServerBaseUrl}Invoice&id=${this.id}&rs:Command=Render" ),
        INVOICE_OVERVIEW(
                "Invoices overview", "/", InvoiceSummaryForPropertyDueDateStatus.class,
                "${reportServerBaseUrl}Invoices&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}&rs:Command=Render"),
        PRELIM_LETTER_ITA_ON_SUMMARY(
                "Preliminary letter Italy", "/ITA", InvoiceSummaryForPropertyDueDateStatus.class,
                "${reportServerBaseUrl}Preliminary+Letter&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}&rs:Command=Render" ),
        INVOICE_ITA_ON_SUMMARY(
                "Invoice Italy", "/ITA", InvoiceSummaryForPropertyDueDateStatus.class,
                "${reportServerBaseUrl}Invoice&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}&rs:Command=Render" ),
        INVOICE_FRA(
                "Invoice France", "/FRA", Invoice.class,
                "${reportServerBaseUrl}InvoiceFrance&id=${this.id}&rs:Command=Render" ),
        INVOICE_FRA_ON_SUMMARY(
                "Invoice France", "/FRA", InvoiceSummaryForPropertyDueDateStatus.class,
                "${reportServerBaseUrl}InvoiceFrance&dueDate=${this.dueDate}&sellerId=${this.seller.id}&atPath=${this.atPath}&rs:Command=Render" ),
        ;


        private final String name;
        private final String atPath;
        private final Class<?> clazz;

        private final String urlTemplate;

        public boolean matches(final Link link) {
            return Objects.equals(link.getName(), name) &&
                   link.getClassName().endsWith(clazz.getSimpleName()) &&
                   Objects.equals(link.getApplicationTenancyPath(), atPath);
        }

        public void upsertUsing(final ServiceRegistry2 serviceRegistry2) {
            final RepositoryService repositoryService =
                    serviceRegistry2.lookupService(RepositoryService.class);
            final LinkRepository linkRepository =
                    serviceRegistry2.lookupService(LinkRepository.class);

            final List<Link> links = linkRepository.allLinks().stream()
                                        .filter(this::matches)
                                        .collect(Collectors.toList());
            final Link link;
            switch (links.size()) {
            case 0:
                link = new Link();
                link.setName(name);
                link.setUrlTemplate(urlTemplate);
                link.setClassName(clazz.getName());
                link.setApplicationTenancyPath(atPath);
                repositoryService.persist(link);
                break;
            case 1:
                link = links.get(0);
                link.setUrlTemplate(getUrlTemplate());
                link.setClassName(getClazz().getName());
                break;
            default:
                throw new IllegalArgumentException("Found " + links.size() + " matching " + this);
            }
        }
    }

    static class SeedLink extends FixtureScript {

        @Override
        protected void execute(final ExecutionContext executionContext) {
            Arrays.stream(Link_data.values()).forEach(datum -> datum.upsertUsing(serviceRegistry));
        }
    }
}

