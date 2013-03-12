package org.estatio.dom.numerator;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;
import org.estatio.dom.utils.StringUtils;

public enum NumeratorType {
    INVOICE_NUMBER(InvoiceNumber.class);

    private Class<? extends Numerator> cls;

    private NumeratorType(Class<? extends Numerator> cls) {
        this.cls = cls;
    }

    public Numerator create(DomainObjectContainer container) {
        try {
            Numerator numerator = container.newTransientInstance(cls);
            numerator.setType(this);
            return numerator;
        } catch (Exception ex) {
            throw new ApplicationException(ex);
        }
    }

    public String title() {
        return StringUtils.enumTitle(this.toString());
    }
}
