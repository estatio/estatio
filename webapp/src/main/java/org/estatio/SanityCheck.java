package org.estatio;

import javax.jdo.spi.PersistenceCapable;

import org.estatio.dom.lease.Lease;

public class SanityCheck {

    public static void main(String[] args) {
        check(Lease.class);
    }

    private static void check(Class<?> cls) {
        if(PersistenceCapable.class.isAssignableFrom(cls)) {
            System.out.println(cls.getName() + " class enhanced OK");
            System.out.flush();
        } else {
            System.err.println(cls.getName() + " class NOT enhanced");
            System.err.flush();
        }
    }

}
