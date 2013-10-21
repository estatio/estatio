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

import javax.jdo.annotations.Persistent;

import org.joda.time.LocalDate;

import org.apache.isis.applib.AbstractViewModel;
import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.DescribedAs;
import org.apache.isis.applib.annotation.Immutable;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Title;

import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.Property;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;

@Bookmarkable
@Immutable
public class InvoiceSummaryForPropertyDueDateStatus extends AbstractViewModel {

    /**
     * {@link ViewModel} implementation.
     */
    @Override
    public String viewModelMemento() {
        return getReference();
    }

    /**
     * {@link ViewModel} implementation.
     */
    @Override
    public void viewModelInit(final String memento) {
        setReference(memento);
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
    @Title(sequence = "1", prepend = "[", append = "] ")
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

    private String name;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @DescribedAs("Unique name for this property")
    @Title(sequence = "2")
    public String getName() {
        return name != null ? name : (name = getProperty().getName());
    }

    public void setName(final String name) {
        this.name = name;
    }

    // //////////////////////////////////////

    @javax.jdo.annotations.Persistent
    private LocalDate dueDate;

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    // //////////////////////////////////////

    @Persistent
    private InvoiceStatus status;

    public void setStatus(final InvoiceStatus status) {
        this.status = status;
    }

    public InvoiceStatus getStatus() {
        return status;
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

    private List<Invoice> invoices;

    public List<Invoice> getInvoices() {
        if (invoices == null) {
            setInvoices(invoicesService.findInvoices(getProperty(), getDueDate(), getStatus()));
        }
        return invoices;
    }

    public void setInvoices(final List<Invoice> invoices) {
        this.invoices = invoices;
    }

    // //////////////////////////////////////

    private Properties properties;

    public void injectProperties(final Properties properties) {
        this.properties = properties;
    }

    private Invoices invoicesService;

    public void injectInvoicesService(final Invoices invoicesService) {
        this.invoicesService = invoicesService;
    }

}
