package org.estatio.fixturescripts;

import java.util.concurrent.Callable;

import org.apache.isis.applib.ApplicationException;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Named;

@Named("Script")
public enum FixtureScript {

    GenerateTopModelInvoice(GenerateTopModelInvoice.class);
    
    private Class<? extends Callable<Object>> cls;

    private FixtureScript(Class<? extends Callable<Object>> cls) {
        this.cls = cls;
    }
    
    public Object run(DomainObjectContainer container) {
        final Callable<Object> callable = (Callable<Object>) container.newTransientInstance(cls);
        try {
            return callable.call();
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }
    
}
