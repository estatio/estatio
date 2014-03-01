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
package org.estatio.dom.invoice.viewmodel;

import java.math.BigDecimal;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Render;
import org.apache.isis.applib.annotation.Render.Type;
import org.apache.isis.applib.annotation.Title;
import org.apache.isis.applib.services.memento.MementoService.Memento;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Invoice;

/**
 * View model that surfaces information about each property along with summary
 * details of its invoices in their various states.
 */
@javax.jdo.annotations.PersistenceCapable(
        identityType = IdentityType.NONDURABLE,
        table = "InvoiceSummaryForPropertyDueDate",
        extensions = {
                @Extension(vendorName = "datanucleus", key = "view-definition",
                        value = "CREATE VIEW \"InvoiceSummaryForPropertyDueDate\" " +
                                "( " +
                                "  {this.reference}, " +
                                "  {this.dueDate}, " +
                                "  {this.total}, " +
                                "  {this.netAmount}, " +
                                "  {this.vatAmount}, " +
                                "  {this.grossAmount} " +
                                ") AS " +
                                "SELECT " +
                                "   \"FixedAsset\".\"reference\" , " +
                                "   \"Invoice\".\"dueDate\", " +
                                "   COUNT(DISTINCT(\"Invoice\".\"id\")) AS \"total\", " +
                                "   SUM(\"InvoiceItem\".\"netAmount\") AS \"netAmount\", " +
                                "   SUM(\"InvoiceItem\".\"vatAmount\") AS \"vatAmount\", " +
                                "   SUM(\"InvoiceItem\".\"grossAmount\") AS \"grossAmount\" " +
                                "  FROM \"Invoice\" " +
                                "  INNER JOIN \"Lease\"   " +
                                "    ON \"Invoice\".\"leaseId\" = \"Lease\".\"id\" " +
                                "  INNER JOIN \"Occupancy\" " +
                                "    ON \"Lease\".\"id\"              = \"Occupancy\".\"leaseId\" " +
                                "  INNER JOIN \"Unit\"   " +
                                "    ON \"Unit\".\"id\"               = \"Occupancy\".\"unitId\" " +
                                "  INNER JOIN \"Property\" " +
                                "    ON \"Property\".\"id\"           = \"Unit\".\"propertyId\" " +
                                "  INNER JOIN \"FixedAsset\" " +
                                "    ON \"FixedAsset\".\"id\"         = \"Property\".\"id\" " +
                                "  INNER JOIN \"InvoiceItem\" " +
                                "    ON \"InvoiceItem\".\"invoiceId\" = \"Invoice\".\"id\" " +
                                "GROUP BY " +
                                " \"FixedAsset\".\"reference\", " +
                                " \"Invoice\".\"dueDate\"")
        })
@javax.jdo.annotations.Queries({
    @javax.jdo.annotations.Query(
            name = "findByDueDate", language = "JDOQL",
            value = "SELECT " +
                    "FROM org.estatio.dom.invoice.viewmodel.InvoiceSummaryForPropertyDueDate " +
                    "WHERE " +
                    "dueDate == :dueDate")
})

@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Bookmarkable
@Immutable
public class InvoiceSummaryForPropertyDueDate extends InvoiceSummaryAbstract {

    public String iconName(){
        return "InvoiceSummary";
    }
    
    /**
     * {@link org.apache.isis.applib.ViewModel} implementation.
     */
    @Override
    public String viewModelMemento() {
        final Memento memento = getMementoService().create();

        memento.set("reference", getReference())
                .set("dueDate", getDueDate())
                .set("netAmount", getNetAmount())
                .set("vatAmount", getVatAmount())
                .set("grossAmount", getGrossAmount())
                .set("total", getTotal());

        return memento.asString();
    }

    /**
     * {@link org.apache.isis.applib.ViewModel} implementation.
     */
    @Override
    public void viewModelInit(final String mementoStr) {
        final Memento memento = getMementoService().parse(mementoStr);

        setReference(memento.get("reference", String.class));
        setDueDate(memento.get("dueDate", LocalDate.class));
        setNetAmount(memento.get("netAmount", BigDecimal.class));
        setVatAmount(memento.get("vatAmount", BigDecimal.class));
        setGrossAmount(memento.get("grossAmount", BigDecimal.class));
        setTotal(memento.get("total", Integer.class));
    }

    // //////////////////////////////////////

    private String reference;

    /**
     * Used as the {@link #viewModelMemento() view model memento}, holds the
     * reference of the corresponding {@link #getProperty()}.
     * 
     * <p>
     * This attribute is always guaranteed to be populated.
     */
    @javax.jdo.annotations.Column(allowsNull = "false")
    @DescribedAs("Unique reference code for this property")
    @Hidden
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    // //////////////////////////////////////

    /**
     * Annotated as {@link javax.jdo.annotations.NotPersistent not persistent}
     * because not mapped in the <tt>view-definition</tt>.
     */
    @javax.jdo.annotations.NotPersistent
    private Property property;

    /**
     * Lazily loaded from the {@link #getReference() reference}, provides access
     * to the underlying {@link Property}.
     */
    @Optional
    @Title(sequence = "1")
    public Property getProperty() {
        if (property == null) {
            setProperty(properties.findPropertyByReference(getReference()));
        }
        return property;
    }

    public void setProperty(final Property property) {
        this.property = property;
    }

    // //////////////////////////////////////

    private LocalDate dueDate;

    @Title(sequence = "2", prepend = " - ")
    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // //////////////////////////////////////

    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(final int total) {
        this.total = total;
    }

    // //////////////////////////////////////

    private BigDecimal vatAmount;

    @Hidden
    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(final BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    // //////////////////////////////////////

    private BigDecimal netAmount;

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(final BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    // //////////////////////////////////////

    private BigDecimal grossAmount;

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(final BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    // //////////////////////////////////////

    @Render(Type.EAGERLY)
    public List<Invoice> getInvoices() {
        return invoicesService.findInvoices(getProperty(), getDueDate());
    }

    // //////////////////////////////////////

    private Properties properties;

    final public void injectProperties(final Properties properties) {
        this.properties = properties;
    }

}
