package org.estatio.services.exceprecog;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import org.apache.isis.applib.services.exceprecog.ExceptionRecognizerGeneral;

class ExceptionRecognizerSubstituting extends ExceptionRecognizerGeneral {
    public ExceptionRecognizerSubstituting(final String messageFragment, final String replacement) {
        super(new Predicate<Throwable>() {
            public boolean apply(Throwable ex) {
                return ex.getMessage() != null && ex.getMessage().contains(messageFragment); 
            }
        }, new Function<String, String>() {
            public String apply(String ex) {
                return replacement;
            }
        });
    }
}