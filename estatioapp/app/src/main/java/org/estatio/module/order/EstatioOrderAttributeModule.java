/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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
package org.estatio.module.order;

import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Sets;

import org.apache.isis.applib.Module;
import org.apache.isis.applib.ModuleAbstract;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.fixturescripts.teardown.TeardownFixtureAbstract2;

import org.incode.module.classification.dom.ClassificationModule;
import org.incode.module.communications.CommunicationsModule;
import org.incode.module.docfragment.DocFragmentModule;
import org.incode.module.docrendering.freemarker.FreemarkerDocRenderingModule;
import org.incode.module.docrendering.gotenberg.dom.impl.GotenbergRenderingModule;
import org.incode.module.docrendering.stringinterpolator.StringInterpolatorDocRenderingModule;
import org.incode.module.docrendering.xdocreport.XDocReportDocRenderingModule;

import org.estatio.module.assetfinancial.EstatioAssetFinancialModule;
import org.estatio.module.event.EstatioEventModule;
import org.estatio.module.index.EstatioIndexModule;
import org.estatio.module.invoice.EstatioInvoiceModule;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.attr.InvoiceAttribute;
import org.estatio.module.invoice.dom.paperclips.PaperclipForInvoice;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemSource;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseType;
import org.estatio.module.lease.dom.breaks.BreakOption;
import org.estatio.module.lease.dom.breaks.BreakOptionRepository;
import org.estatio.module.lease.dom.breaks.EventSourceLinkForBreakOption;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.dom.occupancy.Occupancy;
import org.estatio.module.lease.dom.occupancy.tags.Activity;
import org.estatio.module.lease.dom.occupancy.tags.Brand;
import org.estatio.module.lease.dom.occupancy.tags.Sector;
import org.estatio.module.lease.dom.occupancy.tags.UnitSize;
import org.estatio.module.settings.EstatioSettingsModule;

@XmlRootElement(name = "module")
public final class EstatioOrderAttributeModule extends ModuleAbstract {

    public EstatioOrderAttributeModule(){}

    @Override
    public Set<Module> getDependencies(){
        return Sets.newHashSet(
                new EstatioInvoiceModule(),
                new EstatioAssetFinancialModule(),
                new EstatioIndexModule(),
                new EstatioEventModule(),
                new EstatioSettingsModule(),

                // stuff from incode platform, but which we're going to inline back into Estatio
                new CommunicationsModule(),
                new FreemarkerDocRenderingModule(),
                new StringInterpolatorDocRenderingModule(),
                new GotenbergRenderingModule(),
                new XDocReportDocRenderingModule(),
                new DocFragmentModule(),
                new ClassificationModule()

                );
    }

    @Override
    public FixtureScript getTeardownFixture() {
        return new TeardownFixtureAbstract2() {
            @Override
            protected void execute(final ExecutionContext executionContext) {

                String schema;
                String sql;
                String table;

                deleteFrom(EventSourceLinkForBreakOption.class);

                // InvoiceAttribute
                schema = schemaOf(InvoiceAttribute.class);
                table = tableOf(InvoiceAttribute.class);
                sql = String.format(
                        "DELETE FROM \"%s\".\"%s\" "
                                + "WHERE \"%s\" IN "
                                + "(SELECT \"id\" FROM \"%s\".\"%s\" WHERE \"%s\" = '%s') ",
                        schema, table, "invoiceId",
                        schemaOf(Invoice.class), tableOf(Invoice.class), // supertype of IncomingInvoice
                        discriminatorColumnOf(Invoice.class),
                        discriminatorValueOf(InvoiceForLease.class)
                );
                this.isisJdoSupport.executeUpdate(sql);

                // PaperclipForInvoice
                schema = schemaOf(PaperclipForInvoice.class);
                table = tableOf(PaperclipForInvoice.class);
                sql = String.format(
                        "DELETE FROM \"%s\".\"%s\" "
                                + "WHERE \"%s\" IN "
                                + "(SELECT \"id\" FROM \"%s\".\"%s\" WHERE \"%s\" = '%s') ",
                        schema, table, "invoiceId",
                        schemaOf(Invoice.class), tableOf(Invoice.class), // supertype of IncomingInvoice
                        discriminatorColumnOf(Invoice.class),
                        discriminatorValueOf(InvoiceForLease.class)
                );
                this.isisJdoSupport.executeUpdate(sql);

                deleteFrom(InvoiceItemForLease.class);
                deleteFrom(InvoiceForLease.class);

                deleteFrom(BreakOption.class); // superclass handles the subclasses
                deleteFrom(LeaseItemSource.class);
                deleteFrom(LeaseTerm.class);
                deleteFrom(LeaseItemSource.class);
                deleteFrom(LeaseItem.class);
                deleteFrom(Occupancy.class);

                deleteFrom(Activity.class);
                deleteFrom(Brand.class);
                deleteFrom(Sector.class);
                deleteFrom(UnitSize.class);

                deleteFrom(Lease.class);
                deleteFrom(LeaseType.class);
            }

            @Inject
            BreakOptionRepository breakOptionRepository;

        };

    }



}
