package org.estatio.dom.numerator;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;
import org.estatio.dom.utils.StringUtils;

public enum NumeratorType {
    INVOICE_NUMBER(NumeratorForInvoiceNumber.class, "Invoice Number"), 
    COLLECTION_NUMBER(NumeratorForCollectionNumber.class, "Collection Number");

    private Class<? extends Numerator> cls;
    private String description;

    private NumeratorType(Class<? extends Numerator> cls, String description) {
        this.cls = cls;
        this.description = description;
    }

    public Numerator create(DomainObjectContainer container) {
        try {
            Numerator numerator = container.newTransientInstance(cls);
            numerator.setType(this);
            numerator.setDescription(description);
            return numerator;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }

    public String description() {
        return description;
    }
}
